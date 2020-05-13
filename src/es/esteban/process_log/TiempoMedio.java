package es.esteban.process_log;

public class TiempoMedio
{
    private int ocurrencias;
    private int tiempoTotal;
    private int maxTiempo = 0;

    public TiempoMedio()
    {
    }

    public void addTiempoSegundos(int segundos)
    {
        ocurrencias++;
        tiempoTotal += segundos;
        if (maxTiempo < segundos)
        {
            maxTiempo = segundos;
        }
    }

    public float getMediaMinutos()
    {
        int tiempoMedioSegundos = (int) tiempoTotal / ocurrencias;
        return (float) tiempoMedioSegundos / 60;
    }

    public float getMaxMinutos()
    {
        return (float) maxTiempo / 60;
    }

    public int getOccurences()
    {
        return ocurrencias;
    }
}
