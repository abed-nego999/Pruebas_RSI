package es.esteban.process_log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LeeTiemposPush
{
    private static Connection conn = null;

    public static void main(String[] args)
    {
        System.out.println(getConnection());

        try
        {
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Connection getConnection()
    {
        try
        {
            if (conn == null)
            {
                conn = DriverManager.getConnection("jdbc:oracle:thin:@//bancapro.risa:2521/BANCAP_GNR1", "u028721", "Qw3rty04");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return conn;
    }
}
