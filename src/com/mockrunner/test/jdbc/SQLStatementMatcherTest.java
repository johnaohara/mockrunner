package com.mockrunner.test.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mockrunner.jdbc.SQLStatementMatcher;

import junit.framework.TestCase;

public class SQLStatementMatcherTest extends TestCase
{
	public void testGetMatchingObjects()
	{
		Map testMap = new HashMap();
		testMap.put("Test1", "TestObject1");
		testMap.put("Test2", "TestObject2");
		List testList = new ArrayList();
		testList.add("TestList1");
		testList.add("TestList2");
		testList.add("TestList3");
		testMap.put("Test4", testList);
		SQLStatementMatcher matcher = new SQLStatementMatcher(true, false);
		List resultList = matcher.getMatchingObjects(testMap, "Test", false, false);
		assertEquals(3, resultList.size());
		assertTrue(resultList.contains("TestObject1"));
		assertTrue(resultList.contains("TestObject2"));
		assertTrue(resultList.contains(testList));
	}
    
    public void testGetMatchingObjectsResolveCollection()
    {
        Map testMap = new HashMap();
        testMap.put("Test1", "TestObject1");
        testMap.put("Test2", "TestObject2");
        testMap.put("Test3", "TestObject3");
        testMap.put("Test", "TestObject");
        SQLStatementMatcher matcher = new SQLStatementMatcher(true, true);
        List resultList = matcher.getMatchingObjects(testMap, "Test", true, false);
        assertTrue(resultList.size() == 1);
        assertTrue(resultList.contains("TestObject"));
        assertFalse(resultList.contains("TestObject1"));
        assertFalse(resultList.contains("TestObject2"));
        assertFalse(resultList.contains("TestObject3"));
        matcher = new SQLStatementMatcher(true, false);
        resultList = matcher.getMatchingObjects(testMap, "Test", true, false);
        assertTrue(resultList.size() == 4);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        matcher = new SQLStatementMatcher(true, false);
        resultList = matcher.getMatchingObjects(testMap, "test", true, false);
        assertTrue(resultList.isEmpty());
        matcher = new SQLStatementMatcher(false, false);
        resultList = matcher.getMatchingObjects(testMap, "test", true, false);
        assertTrue(resultList.size() == 4);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        List testList = new ArrayList();
        testList.add("TestList1");
        testList.add("TestList2");
        testList.add("TestList3");
        testMap.put("Test4", testList);
        matcher = new SQLStatementMatcher(true, false);
        resultList = matcher.getMatchingObjects(testMap, "", true, false);
        assertTrue(resultList.size() == 7);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        assertTrue(resultList.contains("TestList1"));
        assertTrue(resultList.contains("TestList2"));
        assertTrue(resultList.contains("TestList3"));
        matcher = new SQLStatementMatcher(true, false);
        resultList = matcher.getMatchingObjects(testMap, "", true, true);
        assertTrue(resultList.isEmpty());
        matcher = new SQLStatementMatcher(true, false);
        resultList = matcher.getMatchingObjects(testMap, "TestObjectObject", true, true);
        assertTrue(resultList.size() == 1);
        assertTrue(resultList.contains("TestObject"));
    }
    
    public void testContains()
    {
        ArrayList list = new ArrayList();
        list.add("TestString1");
        list.add("TestString2");
        list.add("TestString3");
        SQLStatementMatcher matcher = new SQLStatementMatcher(false, false);
        assertTrue(matcher.contains(list, "TESTSTRING",false));
        matcher = new SQLStatementMatcher(true, false);
        assertFalse(matcher.contains(list, "TESTSTRING", false));
        matcher = new SQLStatementMatcher(false, true);
        assertFalse(matcher.contains(list, "TESTSTRING", false));
        matcher = new SQLStatementMatcher(true, true);
        assertTrue(matcher.contains(list, "TestString3", false));
        matcher = new SQLStatementMatcher(true, true);
        assertFalse(matcher.contains(list, "TestString4", false));
        matcher = new SQLStatementMatcher(false, false);
        assertFalse(matcher.contains(list, "TestString4", false));
        matcher = new SQLStatementMatcher(false, false);
        assertFalse(matcher.contains(list, "TESTSTRING1XYZ", false));
        matcher = new SQLStatementMatcher(false, false);
        assertTrue(matcher.contains(list, "TESTSTRING1XYZ", true));
        matcher = new SQLStatementMatcher(true, false);
        assertFalse(matcher.contains(list, "TEstString3Test", true));
        matcher = new SQLStatementMatcher(false, false);
        assertTrue(matcher.contains(list, "TEstString3Test", true));
    }
    
    public void testDoesStringMatch()
    {
        SQLStatementMatcher matcher = new SQLStatementMatcher(true, false);
        assertFalse(matcher.doesStringMatch("X", "x"));
        matcher = new SQLStatementMatcher(false, true);
        assertTrue(matcher.doesStringMatch("X", "x"));
        matcher = new SQLStatementMatcher(false, false);
        assertTrue(matcher.doesStringMatch("Test", "tes"));
        matcher = new SQLStatementMatcher(true, false);
        assertFalse(matcher.doesStringMatch("Test", "tes"));
        matcher = new SQLStatementMatcher(true, false);
        assertTrue(matcher.doesStringMatch("Test", ""));
        matcher = new SQLStatementMatcher(true, false);
        assertTrue(matcher.doesStringMatch("Test", null));
        matcher = new SQLStatementMatcher(false, true);
        assertFalse(matcher.doesStringMatch("Test", null));
        matcher = new SQLStatementMatcher(true, true);
        assertTrue(matcher.doesStringMatch(null, null));
        matcher = new SQLStatementMatcher(true, true);
        assertTrue(matcher.doesStringMatch("ThisIsATest", "ThisIsATest"));
    }
}
