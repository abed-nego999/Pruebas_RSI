package es.esteban.process_log;

import java.time.LocalDateTime;

public class LogMessage
{
    public enum EVENT
    {
        NULL_EVENT, SENT_TO_JMS, RECEIVED_FROM_JMS, SENT_TO_GOOGLE, OK_FROM_GOOGLE, NOK_FROM_GOOGLE
    }

    private EVENT         event;
    private LocalDateTime date;
    private String        logFile;

    public LogMessage(EVENT event, LocalDateTime date, String logFile)
    {
        super();
        this.event = event;
        this.date = date;
        this.logFile = logFile;
    }

    public EVENT getEvent()
    {
        return event;
    }

    public void setEvent(EVENT event)
    {
        this.event = event;
    }

    public LocalDateTime getDate()
    {
        return date;
    }

    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }

    public String getLogFile()
    {
        return logFile;
    }

    public void setLogFile(String logFile)
    {
        this.logFile = logFile;
    }
}
