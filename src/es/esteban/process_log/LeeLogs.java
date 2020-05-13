package es.esteban.process_log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeeLogs
{

    private static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss,SSS", new Locale("en"));
    private static Pattern           SENT_JMS           = Pattern.compile("(\\d{2} \\D{3} \\d{4} \\d{2}:\\d{2}:\\d{2},\\d{3}).*(?:Enviado mensaje a la cola JMS|-> IN mdb CRM:).*(?:idMensaje|id_mensaje)=(\\d+),");
    private static Pattern           RECEIVED_JMS       = Pattern.compile("(\\d{2} \\D{3} \\d{4} \\d{2}:\\d{2}:\\d{2},\\d{3}).*\\^\\^ UP mdb BEL: Mensaje_MDB_Banca.*idMensaje=(\\d+),");
    private static Pattern           SENT_GOOGLE        = Pattern.compile("(\\d{2} \\D{3} \\d{4} \\d{2}:\\d{2}:\\d{2},\\d{3}).*Datos de Entrada.*id_mensaje=(\\d+),");
    private static Pattern           OK_GOOGLE          = Pattern.compile("(\\d{2} \\D{3} \\d{4} \\d{2}:\\d{2}:\\d{2},\\d{3}).*ENVIAR OK - El mensaje (\\d+) --");
    private static Pattern           NOK_GOOGLE         = Pattern.compile("(\\d{2} \\D{3} \\d{4} \\d{2}:\\d{2}:\\d{2},\\d{3}) ERROR.*?[Mm]ensaje(?: |: |=)(\\d+)");
    private static List<Message>     MESSAGES_HISTORY   = new ArrayList<Message>();

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if (args == null || args.length < 1)
        {
            throw new IllegalArgumentException("No se han recibido parámetros. Debe indicar la carpeta de los logs y el fichero de salida");
        }

        if (args.length != 2)
        {
            throw new IllegalArgumentException("No se han recibido parámetros correctos. Debe indicar la carpeta de los logs y el fichero de salida");
        }

        String inputParam = args[0];
        String outputParam = args[1];
        File folder = new File(inputParam);

        if (!folder.isDirectory())
        {
            throw new IllegalArgumentException("Debe indicar la carpeta de los logs como parámetro, no un fichero");
        }

        List<File> logFiles = readAllFiles(folder);

        if (logFiles == null || logFiles.size() == 0)
        {
            throw new NoSuchFileException("No se encontraron ficheros en la carpeta " + folder);
        }

        for (File logFile : logFiles)
        {
            List<String> logLines = readAllLines(logFile);
            for (String logLine : logLines)
            {
                try
                {
                    long messageId = -1L;
                    LocalDateTime eventDate = null;
                    LogMessage.EVENT event = LogMessage.EVENT.NULL_EVENT;

                    Matcher m = SENT_JMS.matcher(logLine);
                    if (m.find())
                    {
                        event = LogMessage.EVENT.SENT_TO_JMS;
                    }
                    else
                    {
                        m = RECEIVED_JMS.matcher(logLine);
                        if (m.find())
                        {
                            event = LogMessage.EVENT.RECEIVED_FROM_JMS;
                        }
                        else
                        {
                            m = SENT_GOOGLE.matcher(logLine);
                            if (m.find())
                            {
                                event = LogMessage.EVENT.SENT_TO_GOOGLE;
                            }
                            else
                            {
                                m = OK_GOOGLE.matcher(logLine);
                                if (m.find())
                                {
                                    event = LogMessage.EVENT.OK_FROM_GOOGLE;
                                }
                                else
                                {
                                    m = NOK_GOOGLE.matcher(logLine);
                                    if (m.find())
                                    {
                                        event = LogMessage.EVENT.NOK_FROM_GOOGLE;
                                    }
                                }
                            }
                        }
                    }

                    if (event != LogMessage.EVENT.NULL_EVENT)
                    {
                        messageId = Long.parseLong(m.group(2));
                        eventDate = LocalDateTime.parse(m.group(1), DATETIME_FORMATTER);
                    }

                    if (messageId > -1L && eventDate != null && event != LogMessage.EVENT.NULL_EVENT)
                    {
                        Message mensaje = getMessageById(messageId);
                        mensaje.getEvents().add(new LogMessage(event, eventDate, logFile.getName()));
                    }
                }
                catch (Exception e)
                {
                    System.out.println("No se pudo parsear la línea " + logLine);
                    e.printStackTrace();
                }
            }
        }

        // Vamos a pintar los resultados
        File outputFile = new File(outputParam);
        if (outputFile.exists())
        {
            outputFile.delete();
        }
        outputFile.createNewFile();

        // generateHtml(outputFile);
        // generateCsv(outputFile);
        generateCsvMedias(outputFile);

        System.exit(0);
    }

    private static List<String> readAllLines(File file) throws FileNotFoundException, IOException
    {
        List<String> logLines = new ArrayList<String>();
        BufferedReader br = null;
        try
        {
            FileReader fr = new FileReader(file);   // reads the file
            br = new BufferedReader(fr);  // creates a buffering character input stream
            String line;
            while ((line = br.readLine()) != null)
            {
                logLines.add(line);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
        }
        return logLines;
    }

    private static List<File> readAllFiles(File directory)
    {
        List<File> result = new ArrayList<File>();
        if (directory.isDirectory())
        {
            File[] files = directory.listFiles();
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    result.addAll(readAllFiles(file));
                }
                else
                {
                    result.add(file);
                }
            }
        }
        else
        {
            result.add(directory);
        }

        return result;
    }

    private static Message getMessageById(long messageId)
    {
        Message mensaje = MESSAGES_HISTORY.stream().filter(customer -> messageId == customer.getId()).findAny().orElse(null);
        if (mensaje == null)
        {
            mensaje = new Message(messageId);
        }

        return mensaje;
    }

    private static void generateHtml(File outputFile) throws IOException
    {
        /*FileOutputStream fileOutStream = null;
        try
        {
            fileOutStream = new FileOutputStream(outputFile);
            fileOutStream.write("<!DOCTYPE html>\r\n<html>\r\n<head>\r\n<title>Resumen de logs</title>\r\n</head>\r\n<body><ul>".getBytes());
            for (Long messageId : MESSAGES_HISTORY.keySet())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("\r\n<li>Mensaje: " + String.valueOf(messageId) + "\r\n<ul>\r\n");
                SortedMap<Date, LogMessage> messageEvents = MESSAGES_HISTORY.get(messageId);
                for (Date eventDate : messageEvents.keySet())
                {
                    sb.append("<li title=\"");
                    sb.append(messageEvents.get(eventDate).getLogFile());
                    sb.append("\">" + DATE_FORMAT.format(eventDate) + ": ");
                    switch (messageEvents.get(eventDate).getEvent())
                    {
                        case NULL_EVENT:
                            break;
                        case SENT_TO_JMS:
                            sb.append("Enviado a JMS</li>\r\n");
                            break;
                        case RECEIVED_FROM_JMS:
                            sb.append("Recibido de JMS</li>\r\n");
                            break;
                        case SENT_TO_GOOGLE:
                            sb.append("Enviado a GOOGLE</li>\r\n");
                            break;
                        case OK_FROM_GOOGLE:
                            sb.append("OK de GOOGLE</li>\r\n");
                            break;
                        case NOK_FROM_GOOGLE:
                            sb.append("Error desde GOOGLE</li>\r\n");
                            break;
                    }
                }
        
                sb.append("\r\n</ul>\r\n</li>\r\n");
        
                fileOutStream.write(sb.toString().getBytes());
            }
        
            fileOutStream.write("</body>\r\n</html>".getBytes());
        
            fileOutStream.flush();
            fileOutStream.close();
        }
        catch (Exception e)
        {
            System.out.println("No se pudo escribir el fichero " + outputFile);
            e.printStackTrace();
        }
        finally
        {
            if (fileOutStream != null)
            {
                fileOutStream.close();
            }
        }*/
    }

    private static void generateCsv(File outputFile) throws IOException
    {
        /*FileOutputStream fileOutStream = null;
        try
        {
            fileOutStream = new FileOutputStream(outputFile);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            DecimalFormat df = new DecimalFormat("0.000");
            fileOutStream.write("ID_MENSAJE;FECHA ENVIADO JMS;FECHA RECIBIDO JMS;FECHA ENVIADO;TIEMPO JMS;TIEMPO APP\r\n".getBytes());
            for (Long messageId : MESSAGES_HISTORY.keySet())
            {
                SortedMap<Date, LogMessage> messageEvents = MESSAGES_HISTORY.get(messageId);
                if (messageEvents.size() == 4
                        && messageEvents.get(messageEvents.lastKey()).getEvent() == LogMessage.EVENT.OK_FROM_GOOGLE)
                {
                    String idMessage = String.valueOf(messageId);
                    Date fechaEnviadoJms = null;
                    Date fechaRecibidoJms = null;
                    Date fechaEnviado = null;
                    for (Date eventDate : messageEvents.keySet())
                    {
                        switch (messageEvents.get(eventDate).getEvent())
                        {
                            case NOK_FROM_GOOGLE:
                                break;
                            case NULL_EVENT:
                                break;
                            case OK_FROM_GOOGLE:
                                break;
                            case RECEIVED_FROM_JMS:
                                fechaRecibidoJms = eventDate;
                                break;
                            case SENT_TO_GOOGLE:
                                fechaEnviado = eventDate;
                                break;
                            case SENT_TO_JMS:
                                fechaEnviadoJms = eventDate;
                                break;
                            default:
                                break;
                        }
                    }
                    float minutosJms = (float) (fechaRecibidoJms.getTime() - fechaEnviadoJms.getTime()) / 60000;
                    float minutosApp = (float) (fechaEnviado.getTime() - fechaRecibidoJms.getTime()) / 60000;
        
                    StringBuilder dataLine = new StringBuilder();
                    dataLine.append(idMessage).append(";");
                    dataLine.append(sdf.format(fechaEnviadoJms)).append(";");
                    dataLine.append(sdf.format(fechaRecibidoJms)).append(";");
                    dataLine.append(sdf.format(fechaEnviado)).append(";");
                    dataLine.append(df.format(minutosJms)).append(";");
                    dataLine.append(df.format(minutosApp)).append("\r\n");
                    fileOutStream.write(dataLine.toString().getBytes());
                }
            }
        
            fileOutStream.flush();
            fileOutStream.close();
        }
        catch (Exception e)
        {
            System.out.println("No se pudo escribir el fichero " + outputFile);
            e.printStackTrace();
        }
        finally
        {
            if (fileOutStream != null)
            {
                fileOutStream.close();
            }
        }*/
    }

    private static void generateCsvMedias(File outputFile) throws IOException
    {
        Map<Long, TiempoMedio> tiempos = new TreeMap<Long, TiempoMedio>();
        for (Message mensaje : MESSAGES_HISTORY)
        {
            if (mensaje.isFullCircuit() && mensaje.isOkFromGoogle())
            {
                LocalDateTime fechaRecibidoJms = mensaje.getEventMessage(LogMessage.EVENT.RECEIVED_FROM_JMS).getDate();
                LocalDateTime fechaEnviadoJms = mensaje.getEventMessage(LogMessage.EVENT.SENT_TO_JMS).getDate();
                LocalDateTime fechaEnviado = mensaje.getEventMessage(LogMessage.EVENT.SENT_TO_GOOGLE).getDate();

                long segundosJms = Duration.between(fechaRecibidoJms, fechaEnviadoJms).getSeconds();

                long minutoEnviado = fechaEnviado.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 60000;
                TiempoMedio tiempo = tiempos.get(minutoEnviado);
                if (tiempo == null)
                {
                    tiempo = new TiempoMedio();
                    tiempos.put(minutoEnviado, tiempo);
                }

                tiempo.addTiempoSegundos(segundosJms);
            }
        }

        /* AQUÍ SE ESCRIBE EN EL FICHERO DESTINO */
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        DecimalFormat df = new DecimalFormat("0.000");
        FileOutputStream fileOutStream = null;
        try
        {
            fileOutStream = new FileOutputStream(outputFile);
            fileOutStream.write("MINUTO;TIEMPO MEDIO;TIEMPO MAXIMO;NUM MENSAJES\r\n".getBytes());

            for (long minuto : tiempos.keySet())
            {
                String sMinuto = sdf.format(new Date(minuto * 60000));
                StringBuilder sb = new StringBuilder();
                sb.append(sMinuto).append(";");
                sb.append(String.valueOf(df.format(tiempos.get(minuto).getMediaMinutos()))).append(";");
                float maxMinutos = tiempos.get(minuto).getMaxMinutos();
                if (maxMinutos > 0)
                {
                    sb.append(String.valueOf(df.format(maxMinutos))).append(";");
                }
                else
                {
                    sb.append("").append(";");
                }
                sb.append(String.valueOf(tiempos.get(minuto).getOccurences())).append("\r\n");
                fileOutStream.write(sb.toString().getBytes());
            }

            fileOutStream.flush();
            fileOutStream.close();
        }
        catch (Exception e)
        {
            System.out.println("No se pudo escribir el fichero " + outputFile);
            e.printStackTrace();
        }
        finally
        {
            if (fileOutStream != null)
            {
                fileOutStream.close();
            }
        }
    }
}
