package es.esteban.process_log;

import java.util.HashMap;
import java.util.Map;

public class TiempoMedio
{
    private long              ocurrencias;
    private long              tiempoTotal;
    private long              maxTiempo        = 0;
    private Map<String, Long> occurencesByType = new HashMap<String, Long>();

    public TiempoMedio()
    {
    }

    public void addTiempoSegundos(long segundos, String messageType)
    {
        ocurrencias++;
        tiempoTotal += segundos;
        if (maxTiempo < segundos)
        {
            maxTiempo = segundos;
        }

        if (occurencesByType.get(messageType) == null)
        {
            occurencesByType.put(messageType, 0L);
        }
        long typeOccurences = occurencesByType.get(messageType);
        occurencesByType.put(messageType, typeOccurences + 1);
    }

    public float getMediaMinutos()
    {
        long tiempoMedioSegundos = (long) tiempoTotal / ocurrencias;
        return (float) tiempoMedioSegundos / 60;
    }

    public float getMaxMinutos()
    {
        return (float) maxTiempo / 60;
    }

    public long getOccurences()
    {
        return ocurrencias;
    }

    public Map<String, Long> getOccurencesByType()
    {
        return occurencesByType;
    }

}
