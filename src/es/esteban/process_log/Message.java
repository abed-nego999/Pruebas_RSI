package es.esteban.process_log;

import java.util.ArrayList;
import java.util.List;

public class Message
{
    private long             id;
    private String           tipo;
    private List<LogMessage> events;

    public Message(long id)
    {
        super();
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
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
        if (events == null)
        {
            events = new ArrayList<LogMessage>();
        }
        return events;
    }

    public void setEvents(List<LogMessage> events)
    {
        this.events = events;
    }

    public LogMessage getEventMessage(LogMessage.EVENT event)
    {
        return events.stream().filter(logMessage -> event == logMessage.getEvent()).findAny().orElse(null);
    }

    public boolean isOkFromGoogle()
    {
        return isEvent(LogMessage.EVENT.OK_FROM_GOOGLE, false);
    }

    public boolean isFullCircuit()
    {
        boolean isSentToJms = isEvent(LogMessage.EVENT.SENT_TO_JMS, true);
        boolean isReceivedFromJms = isEvent(LogMessage.EVENT.RECEIVED_FROM_JMS, true);
        boolean isSentToGoogle = isEvent(LogMessage.EVENT.SENT_TO_GOOGLE, true);
        boolean isResponseFromGoogle = isEvent(LogMessage.EVENT.NOK_FROM_GOOGLE, true)
                || isEvent(LogMessage.EVENT.OK_FROM_GOOGLE, true);

        return isSentToJms && isReceivedFromJms && isSentToGoogle && isResponseFromGoogle;
    }

    private boolean isEvent(LogMessage.EVENT event, boolean limitToOne)
    {
        long count = events.stream().filter(logMessage -> event == logMessage.getEvent()).count();
        return count > 0 && (count == 1 || !limitToOne);
    }
}
