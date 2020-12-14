package es.esteban.sigpes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import es.esteban.sigpes.calendario.CalendarioMadrid2020;
import es.esteban.sigpes.db.DbSigpe;
import es.esteban.sigpes.db.DbSigpeReader;

public class GeneraInformes
{
    private Date          mesContable;
    private DbSigpeReader reader = new DbSigpeReader();

    public static void main(String[] args) throws Exception
    {
        GeneraInformes generaInformes = new GeneraInformes(new SimpleDateFormat("dd/MM/yyyy").parse("01/" + args[0]));

        generaInformes.getInforme();
    }

    public GeneraInformes(Date mesContable)
    {
        this.mesContable = mesContable;
    }

    private void getInforme()
    {
        CalendarioMadrid2020 calendario = new CalendarioMadrid2020();

        System.out.println("Mes Contable: " + mesContable);

        Set<DbSigpe> sigpes = reader.getAllSigpes(new java.sql.Date(mesContable.getTime()));
        for (DbSigpe sigpe : sigpes)
        {
            int dias = calendario.getDiasEntreFechas(sigpe.getFechaEntrada(), sigpe.getFechaSalida());
            reader.updateSigpeDias(sigpe.getId(), sigpe.getFechaEntrada(), dias);
            System.out.println("SIGPE: " + sigpe.getId() + " -> " + dias + " días");
        }
    }
}
