package com.mockrunner.example.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.mockrunner.example.servlet.ImageButtonFilter;
import com.mockrunner.servlet.ServletTestModule;
import com.mockrunner.struts.ActionTestCaseAdapter;

/**
 * Example test for {@link LogoutAction}. Demonstrates the testing of 
 * actions with specified filters.
 */
public class LogoutActionTest extends ActionTestCaseAdapter
{
    private ServletTestModule servletModule;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        servletModule = createServletTestModule();
    }

    public void testLogout() throws Exception
    {
        getMockObjectFactory().getMockRequest().setupAddParameter("logout.x", "11");
        getMockObjectFactory().getMockRequest().setupAddParameter("logout.y", "11");
        servletModule.createFilter(ImageButtonFilter.class);
        servletModule.setDoChain(true);
        servletModule.doFilter();
        getMockObjectFactory().addRequestWrapper(new HttpServletRequestWrapper((HttpServletRequest)servletModule.getFilteredRequest()));
        actionPerform(LogoutAction.class);
        assertFalse(getMockObjectFactory().getMockSession().isValid());
    }  
}