package es.esteban.pruebas;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Prueba2
{
    private static String        url1              = "http://contenido.ruralvia.com/cms/estatico/multicanalidad/formacion/414006018/ruralvia_privado_P1/web/es/persona_juridica/banner_destacado/p1.html";
    private static String        url2              = "https://www.rm-static.com/cms/estatico/multicanalidad/formacion/415012002/ruralvia_privado_P1/web/es/persona_juridica/banner_lateral/p1.html";
    private static String        url3              = "https://www.rm-static.com/cms/estatico/multicanalidad/formacion/420004016/ruralvia_privado_P1/movil/es/persona_fisica/boton_inicio/p1.html";

    private static final Pattern SCRIPT_LINK_REGEX = Pattern.compile("buttonLink\\d{2}:\\\"(.*?)\\\"", Pattern.MULTILINE);

    public static void main(String[] args)
    {
        String imagen = null;
        List<String> links = new ArrayList<String>();
        String title = null;
        String subtitle = null;

        try
        {
            Document doc = Jsoup.connect(url3).get();
            // Obtener imagen
            Elements imgElements = doc.select("img");
            if (imgElements.size() > 0)
            {
                imagen = imgElements.attr("src");
            }
            Elements aElements = doc.select("a");
            String link = aElements.attr("href");
            if (link != null && !"".equals(link.trim()))
            {
                links.add(link);
            }

            // Obtener textos
            Elements titleElements = doc.select("span.title");
            if (titleElements.size() > 0)
            {
                title = titleElements.html();
            }
            
            Elements subtitleElements = doc.select("span.subtitle");
            if (subtitleElements.size() > 0)
            {
                subtitle = subtitleElements.html();
                subtitle = subtitle.substring(0, subtitle.indexOf("<span"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Imagen: " + imagen);
        for (String link : links)
        {
            System.out.println("Link: " + link);
        }
        System.out.println("Título: " + title);
        System.out.println("Subtítulo: " + subtitle);
    }

}
