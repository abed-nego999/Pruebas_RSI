package es.esteban.process_log;

import java.util.ArrayList;
import java.util.List;

public class Message
{
    private long             id;
    private String           tipo;
    private List<LogMessage> events;
    private long             sentJms        = 0;
    private long             receivedJms    = 0;
    private long             sentGoogle     = 0;
    private long             responseGoogle = 0;
    private boolean          okFromGoogle   = false;

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

    private List<LogMessage> getEvents()
    {
        if (events == null)
        {
            events = new ArrayList<LogMessage>();
        }
        return events;
    }

    public void addEvent(LogMessage event)
    {
        switch (event.getEvent())
        {
            case SENT_TO_JMS:
                sentJms++;
                break;
            case NOK_FROM_GOOGLE:
                responseGoogle++;
                break;
            case NULL_EVENT:
                break;
            case OK_FROM_GOOGLE:
                responseGoogle++;
                okFromGoogle = true;
                break;
            case RECEIVED_FROM_JMS:
                receivedJms++;
                break;
            case SENT_TO_GOOGLE:
                sentGoogle++;
                break;
            default:
                break;
        }

        getEvents().add(event);
    }

    public LogMessage getEventMessage(LogMessage.EVENT event)
    {
        return events.stream().filter(logMessage -> event == logMessage.getEvent()).findAny().orElse(null);
    }

    public boolean isOkFromGoogle()
    {
        return okFromGoogle;
    }

    public boolean isFullCircuit()
    {
        boolean isSentToJms = (sentJms == 1);
        boolean isReceivedFromJms = (receivedJms == 1);
        boolean isSentToGoogle = (sentGoogle == 1);
        boolean isResponseFromGoogle = (responseGoogle == 1);

        return isSentToJms && isReceivedFromJms && isSentToGoogle && isResponseFromGoogle;
    }
}
