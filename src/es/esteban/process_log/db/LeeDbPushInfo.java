package es.esteban.process_log.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import es.esteban.process_log.Message;

public class LeeDbPushInfo
{
    private static Connection conn = null;

    public LeeDbPushInfo()
    {
        super();
    }

    private Connection getConnection() throws SQLException
    {
        if (conn == null || conn.isClosed())
        {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//bancapro.risa:2521/BANCAP_GNR1", "u028721", "Qw3rty05");
        }

        return conn;
    }

    public String readMessageType(long messageId) throws Exception
    {
        String messageType = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement("SELECT TIPO_OPERACION FROM BEL.BDPTB186_MENSAJES_PUSH WHERE ID_MENSAJE = ?");
            pstmt.setString(1, String.valueOf(messageId));
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                messageType = rs.getString("TIPO_OPERACION");
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Error SQL", e);
        }
        finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            if (pstmt != null)
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            if (conn != null)
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }

        return messageType;
    }

    public void fillMessageTypes(List<Message> messages) throws Exception
    {
        List<Message> messageBatch = new ArrayList<Message>();
        for (int i = 0; i < messages.size(); i++)
        {
            messageBatch.add(messages.get(i));
            if (messageBatch.size() == 100)
            {
                fillMessageTypesBatch(messageBatch);
                messageBatch.clear();
            }
        }

        if (messageBatch.size() > 0)
        {
            fillMessageTypesBatch(messageBatch);
            messageBatch.clear();
        }
    }

    private void fillMessageTypesBatch(List<Message> messages) throws Exception
    {
        StringBuilder query = new StringBuilder("SELECT ID_MENSAJE, TIPO_OPERACION FROM BEL.BDPTB186_MENSAJES_PUSH WHERE ID_MENSAJE IN (");
        boolean firstLoop = true;
        for (Message message : messages)
        {
            if (!firstLoop)
            {
                query.append(",");
            }
            query.append("'").append(message.getId()).append("'");

            firstLoop = false;
        }
        query.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query.toString());
            while (rs.next())
            {
                long messageId = Long.parseLong(rs.getString("ID_MENSAJE"));
                String messageType = rs.getString("TIPO_OPERACION");

                messages.stream().filter(message -> messageId == message.getId()).forEach(message -> message.setTipo(messageType));
            }
        }
        catch (SQLException e)
        {
            throw new Exception("Error SQL", e);
        }
        finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            if (stmt != null)
                try
                {
                    stmt.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            if (conn != null)
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
    }
}
