package es.esteban.sigpes.calendario;

import java.time.LocalTime;

public class HorarioLaboral
{
    private LocalTime horaDeEntrada;
    private LocalTime horaDeSalida;

    public HorarioLaboral(LocalTime horaDeEntrada, LocalTime horaDeSalida)
    {
        super();
        this.horaDeEntrada = horaDeEntrada;
        this.horaDeSalida = horaDeSalida;
    }

    public LocalTime getHoraDeEntrada()
    {
        return horaDeEntrada;
    }

    public void setHoraDeEntrada(LocalTime horaDeEntrada)
    {
        this.horaDeEntrada = horaDeEntrada;
    }

    public LocalTime getHoraDeSalida()
    {
        return horaDeSalida;
    }

    public void setHoraDeSalida(LocalTime horaDeSalida)
    {
        this.horaDeSalida = horaDeSalida;
    }
}
