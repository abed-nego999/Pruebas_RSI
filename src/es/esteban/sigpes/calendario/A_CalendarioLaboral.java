package es.esteban.sigpes.calendario;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public abstract class A_CalendarioLaboral
{
    private List<DayOfWeek>                weekendDays;
    private List<LocalDate>                holidays;
    private Map<DayOfWeek, HorarioLaboral> horarioLaboral;

    public A_CalendarioLaboral()
    {
        weekendDays = getWeekendDays();
        holidays = getHolidays();
        horarioLaboral = getHorarioLaboral();
    }

    public long getHorasEntreFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
    {
        long horasTranscurridas = 0;

        fechaInicial = getFechaHoraReal(fechaInicial);
        fechaFinal = getFechaHoraReal(fechaFinal);

        // Si las fechas no son iguales hay que ir sumando horas hasta llegar
        while (!fechaInicial.toLocalDate().equals(fechaFinal.toLocalDate()))
        {
            LocalTime horaDeSalida = horarioLaboral.get(fechaInicial.getDayOfWeek()).getHoraDeSalida();
            horasTranscurridas += Duration.between(fechaInicial.toLocalTime(), horaDeSalida).toHours();

            // Avanza la fecha inicial al día siguiente laborable a la hora de entrar
            fechaInicial = avanzarALaborable(fechaInicial);
        }

        horasTranscurridas += Duration.between(fechaInicial.toLocalTime(), fechaFinal.toLocalTime()).toHours();

        return horasTranscurridas;
    }

    public int getDiasEntreFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
    {
        long horas = getHorasEntreFechas(fechaInicial, fechaFinal);

        return Math.round((float) horas / getHorasAlDia());
    }

    protected LocalDateTime getFechaHoraReal(LocalDateTime fechaInicial)
    {
        LocalDateTime result;

        if (isFestivo(fechaInicial))
        {
            // La fecha inicial es festivo. Se avanza al siguiente laborable
            result = avanzarALaborable(fechaInicial);
        }
        else
        {
            // La fecha inicial no es festivo
            LocalTime horaInicial = fechaInicial.toLocalTime();
            LocalTime horaEntrada = horarioLaboral.get(fechaInicial.getDayOfWeek()).getHoraDeEntrada();
            LocalTime horaSalida = horarioLaboral.get(fechaInicial.getDayOfWeek()).getHoraDeSalida();

            if (horaInicial.compareTo(horaEntrada) < 0)
            {
                // La hora inicial es antes del horario laboral. Se avanza hasta la hora de entrada
                result = fechaInicial.toLocalDate().atTime(horaEntrada);
            }
            else if (horaInicial.compareTo(horaSalida) > 0)
            {
                // La hora inicial es después del horario laboral. Se avanza al siguiente laborable
                result = avanzarALaborable(fechaInicial);
            }
            else
            {
                // La hora inicial es dentro del horario laboral
                result = fechaInicial;
            }
        }
        return result;
    }

    protected LocalDateTime avanzarALaborable(LocalDateTime fecha)
    {
        LocalDate fechaSiguiente = fecha.toLocalDate();
        do
        {
            fechaSiguiente = fechaSiguiente.plusDays(1);
        }
        while (isFestivo(fechaSiguiente));
        LocalTime horaDeEntradaSiguiente = horarioLaboral.get(fechaSiguiente.getDayOfWeek()).getHoraDeEntrada();
        LocalDateTime fechaHoraSiguiente = fechaSiguiente.atTime(horaDeEntradaSiguiente);

        return fechaHoraSiguiente;
    }

    protected boolean isFestivo(LocalDateTime fecha)
    {
        return isFestivo(fecha.toLocalDate());
    }

    protected boolean isFestivo(LocalDate fecha)
    {
        if (weekendDays.contains(fecha.getDayOfWeek()) || holidays.contains(fecha))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected abstract Map<DayOfWeek, HorarioLaboral> getHorarioLaboral();

    protected abstract List<LocalDate> getHolidays();

    protected abstract List<DayOfWeek> getWeekendDays();

    protected abstract int getHorasAlDia();
}
