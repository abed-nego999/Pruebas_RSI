package es.esteban.sigpes.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class DbSigpeReader
{
    private Connection                     conn;

    private static final String            QUERY_SIGPES      = "SLEECT * FROM SIGPES S WHERE MES_CONTABLE = ? ORDER BY ID, FECHA_ENTRADA";
    private static final String            UPDATE_SIGPE_DIAS = "UPDATE sigpe.sigpes SET dias = ? WHERE id = ? AND fecha_entrada = ?";
    private static final String            COL_MES_CONTABLE  = "MES_CONTABLE";
    private static final String            COL_ID            = "ID";
    private static final String            COL_TITULO        = "TITULO";
    private static final String            COL_FECHA_ENTRADA = "FECHA_ENTRADA";
    private static final String            COL_FECHA_SALIDA  = "FECHA_SALIDA";
    private static final String            COL_DIAS          = "DIAS";
    private static final String            COL_TECNICO_DRAGO = "TECNICO_DRAGO";
    private static final String            COL_TECNICO_RSI   = "TECNICO_RSI";
    private static final String            COL_FUNCIONAL_RSI = "FUNCIONAL_RSI";
    private static final String            COL_COMENTARIO    = "COMENTARIO";

    private static final DateTimeFormatter dateFormatter     = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:MM");

    public DbSigpeReader()
    {

    }

    public Set<DbSigpe> getAllSigpes(LocalDate mesContable)
    {
        Set<DbSigpe> sigpes = new HashSet<DbSigpe>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            conn = getConnection();
            pstmt = getConnection().prepareStatement(QUERY_SIGPES);
            pstmt.setDate(1, (Date) Date.from(mesContable.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                DbSigpe sigpe = getSigpe(rs);
                sigpes.add(sigpe);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeAllObjects(conn, pstmt, rs);
        }

        return sigpes;
    }

    public boolean updateSigpeDias(String id, LocalDateTime fechaEntrada, int dias)
    {
        boolean result = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = getConnection().prepareStatement(UPDATE_SIGPE_DIAS);
            pstmt.setInt(1, dias);
            pstmt.setString(2, id);
            pstmt.setDate(3, (Date) Date.from(fechaEntrada.atZone(ZoneId.systemDefault()).toInstant()));
            int rowCount = pstmt.executeUpdate();

            if (rowCount == 1)
            {
                result = true;
                conn.commit();
            }
            else
            {
                result = false;
                conn.rollback();
                throw new SQLException("Error: El update ha afectado a [" + rowCount + "] filas");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                conn.setAutoCommit(true);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            closeAllObjects(conn, pstmt, rs);
        }

        return result;
    }

    private Connection getConnection() throws SQLException
    {
        if (conn == null || conn.isClosed())
        {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sigpe", "root", "root");
        }

        return conn;
    }

    private DbSigpe getSigpe(ResultSet rs) throws SQLException
    {
        DbSigpe sigpe = new DbSigpe();

        Date mesContable = null;
        String id = null;
        String titulo = null;
        Date fechaEntrada = null;
        Date fechaSalida = null;
        int dias = -1;
        String tecnicoDrago = null;
        String tecnicoRsi = null;
        String funcionalRsi = null;
        String comentario = null;

        mesContable = rs.getDate(COL_MES_CONTABLE);
        id = rs.getString(COL_ID);
        titulo = rs.getString(COL_TITULO);
        fechaEntrada = rs.getDate(COL_FECHA_ENTRADA);
        fechaSalida = rs.getDate(COL_FECHA_SALIDA);
        dias = rs.getInt(COL_DIAS);
        tecnicoDrago = rs.getString(COL_TECNICO_DRAGO);
        tecnicoRsi = rs.getString(COL_TECNICO_RSI);
        funcionalRsi = rs.getString(COL_FUNCIONAL_RSI);
        comentario = rs.getString(COL_COMENTARIO);

        if (mesContable != null)
        {
            sigpe.setMesContable(mesContable.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        sigpe.setId(id);
        sigpe.setTitulo(titulo);
        if (fechaEntrada != null)
        {
            sigpe.setFechaEntrada(fechaEntrada.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (fechaSalida != null)
        {
            sigpe.setFechaSalida(fechaSalida.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        sigpe.setDias(dias);
        sigpe.setTecnicoDrago(tecnicoDrago);
        sigpe.setTecnicoRsi(tecnicoRsi);
        sigpe.setFuncionalRsi(funcionalRsi);
        sigpe.setComentario(comentario);

        return sigpe;
    }

    private void closeAllObjects(Connection conn, Statement stmt, ResultSet rs)
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
