package paxosk.sqoss.svn.logs;

import java.util.*;
import java.sql.*;

public class MyHashPool extends HashSet
{    
    public MyHashPool(Connection con) 
    {
        super();
        
        try
        {
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT pk1_revision FROM Commit;");
            while (rs.next())
            {
                this.add(rs.getLong(1));
            }
        }
        catch(SQLException e)
        {
            System.err.println("Failed to initialize pool with existing IDs");
            e.printStackTrace();
        }
    }//MyHashPool
    
}
