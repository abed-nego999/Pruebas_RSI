package es.esteban.process_log;

import java.util.List;

public class Message
{
    private String           id;
    private String           tipo;
    private List<LogMessage> events;

    public Message(String id)
    {
        super();
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public List<LogMessage> getEvents()
    {
        return events;
    }

    public void setEvents(List<LogMessage> events)
    {
        this.events = events;
    }

}
