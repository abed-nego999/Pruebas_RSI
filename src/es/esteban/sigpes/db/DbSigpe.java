package es.esteban.sigpes.db;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DbSigpe
{
    private LocalDate     mesContable;
    private String        id;
    private String        titulo;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private int           dias;
    private String        tecnicoDrago;
    private String        tecnicoRsi;
    private String        FuncionalRsi;
    private String        comentario;

    public DbSigpe()
    {
        super();
    }

    public DbSigpe(LocalDate mesContable, String id, String titulo, LocalDateTime fechaEntrada,
            LocalDateTime fechaSalida, int dias, String tecnicoDrago, String tecnicoRsi, String funcionalRsi,
            String comentario)
    {
        super();
        this.mesContable = mesContable;
        this.id = id;
        this.titulo = titulo;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.dias = dias;
        this.tecnicoDrago = tecnicoDrago;
        this.tecnicoRsi = tecnicoRsi;
        FuncionalRsi = funcionalRsi;
        this.comentario = comentario;
    }

    public LocalDate getMesContable()
    {
        return mesContable;
    }

    public void setMesContable(LocalDate mesContable)
    {
        this.mesContable = mesContable;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public LocalDateTime getFechaEntrada()
    {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada)
    {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida()
    {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida)
    {
        this.fechaSalida = fechaSalida;
    }

    public int getDias()
    {
        return dias;
    }

    public void setDias(int dias)
    {
        this.dias = dias;
    }

    public String getTecnicoDrago()
    {
        return tecnicoDrago;
    }

    public void setTecnicoDrago(String tecnicoDrago)
    {
        this.tecnicoDrago = tecnicoDrago;
    }

    public String getTecnicoRsi()
    {
        return tecnicoRsi;
    }

    public void setTecnicoRsi(String tecnicoRsi)
    {
        this.tecnicoRsi = tecnicoRsi;
    }

    public String getFuncionalRsi()
    {
        return FuncionalRsi;
    }

    public void setFuncionalRsi(String funcionalRsi)
    {
        FuncionalRsi = funcionalRsi;
    }

    public String getComentario()
    {
        return comentario;
    }

    public void setComentario(String comentario)
    {
        this.comentario = comentario;
    }

}
