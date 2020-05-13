package es.esteban.sigpes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import es.esteban.sigpes.calendario.CalendarioMadrid2020;

public class GeneraInformes
{
    private LocalDate mesContable;

    public static void main(String[] args) throws Exception
    {
        LocalDate date = LocalDate.parse("01/" + args[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        GeneraInformes generaInformes = new GeneraInformes(date);

        generaInformes.getInforme();
    }

    public GeneraInformes(LocalDate mesContable)
    {
        this.mesContable = mesContable;
    }

    private void getInforme()
    {
        CalendarioMadrid2020 calendario = new CalendarioMadrid2020();

        LocalDateTime fechaInicial = LocalDateTime.of(2020, Month.APRIL, 30, 8, 0);
        LocalDateTime fechaFinal = LocalDateTime.of(2020, Month.MAY, 4, 8, 0);
        long horas = calendario.getHorasEntreFechas(fechaInicial, fechaFinal);
        System.out.println("Horas: " + horas);
    }
}
