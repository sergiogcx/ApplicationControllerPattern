package org.mvc.controllers;

// Our Class Dependencies ...
import org.mvc.Context;
import org.mvc.Controller;
import org.mvc.View;

// Parsing XML from News Source
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Data Gathering (from URL)
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


// This is our default Index Controller
public class IndexController extends Controller {

    // Our View handler ...
    public View view;
    public Context context_list;



    // Default Layout initialization ...
    public IndexController() {
        try {
            context_list = new Context();
        } catch (Exception e) {
            System.out.println("Could not initialize xml parser: " + e.toString());
        }
        view = new View("Layout");
        view.set_variable("title", "News Feed Reader");
    }

    // Overriding our default index method ...
    @Override
    public String index() {
        String context = "", context_menu = "", news_list = "";

        context = get_param("context");
        System.out.println("Context: " + context);
        int i = 0;

        View button = new View("Layout_ContextMenuPartial");
        for(String c:context_list.get_context_list()) {
            button.set_variable("link", "/?context=" + c);
            button.set_variable("color", view.button_colors[i]);
            button.set_variable("text", c);
            context_menu += button.render();
            i = i < (view.button_colors.length-1) ? (i+1) : 0;
        }

        // Let's put the news html together ...
        try {
            news_list = parse_news(context_list.get_news_url(context));
        } catch (Exception e) {
            System.out.println("Could not parse xml");
        }


        view.set_variable("news_list", news_list);
        view.set_variable("context_menu", context_menu);
        view.set_variable("current_context", context);

        return view.render();
    }

    private String parse_news(String xml_news_url) throws ParserConfigurationException, SAXException, IOException {
        String output = "";
        // Instantiate a view for each news box ...
        View news_block = new View("Layout_NewsPartial");

        // Allocate Document factory
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // Build URL & Download "Pure" RSS String ...
        URL url = new URL(xml_news_url);
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();

        Document document = dBuilder.parse(inputStream);
        NodeList nodes = document.getElementsByTagName("item");

        // Traversing each ITEM in XML DOM
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            Element line;

            // Take the value for the title node, and assign it to the title variable in the view
            NodeList title = element.getElementsByTagName("title");
            line = (Element) title.item(0);
            news_block.set_variable("title", getCharacterDataFromElement(line));

            // Take the value for the description node, and assign it to the description variable in the view
            NodeList desc = element.getElementsByTagName("description");
            line = (Element) desc.item(0);
            news_block.set_variable("description", getCharacterDataFromElement(line));

            // Take the value for the link node, and assign it to the link variable in the view
            NodeList link = element.getElementsByTagName("link");
            line = (Element) link.item(0);
            news_block.set_variable("link", getCharacterDataFromElement(line));

            // Append our newly created box to the output string...
            output += news_block.render();
        }

        return output;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }



}
