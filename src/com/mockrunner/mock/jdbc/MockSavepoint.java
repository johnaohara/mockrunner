package com.mockrunner.mock.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Mock implementation of <code>Savepoint</code>.
 */
public class MockSavepoint implements Savepoint
{
    private String name;
    private int id;
    
    public MockSavepoint()
    {
        this("");
    }
    
    public MockSavepoint(String name)
    {
        this.name = name;
        this.id = (int)(Integer.MAX_VALUE * Math.random());
    }
    
    public int getSavepointId() throws SQLException
    {
        return id;
    }

    public String getSavepointName() throws SQLException
    {
        return name;
    }

}
