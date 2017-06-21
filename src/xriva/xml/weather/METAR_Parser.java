package xriva.xml.weather;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class METAR_Parser extends DefaultHandler
{
	private String source = "";
	@SuppressWarnings("rawtypes")
	private Hashtable weatherInfo = null;
	private String text = "";
	private boolean inItem = false;

	public METAR_Parser()
	{
		super();
	}

	public METAR_Parser(String url)
	{
		source = url;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public boolean parse()
	{
		if (weatherInfo == null)
			weatherInfo = new Hashtable(50, 10);
		else
			weatherInfo.clear();

		try
		{
			URL newsSource = new URL(source);
			InputStream data = newsSource.openStream();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			reader.setErrorHandler(this);
			reader.parse(new InputSource(data));
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			
			weatherInfo.put((String)ex.toString(), (String)ex.getMessage());
			return false;
		}

		return true;
	}
	/*
	 * SAX processing methods (callbacks) follow
	 *
	 */
	public void characters(char[] chars, int start, int length)
		throws SAXException
	{
		for (int x = start; x < (start + length); x++)
			text += chars[x];
	}

	public void endDocument() throws SAXException
	{
		super.endDocument();
	}

	@SuppressWarnings("unchecked")
	public void endElement(String uri, String name, String qName)
		throws SAXException
	{
		if (inItem)
		{
			weatherInfo.put(qName, text);
		}

		if (qName.equals("METAR"))
		{
			inItem = false;
		}
	}

	public void startDocument() throws SAXException
	{
		super.startDocument();
	}

	public void startElement(
		String uri,
		String name,
		String qName,
		Attributes attribs)
		throws SAXException
	{
		text = "";
		
		if (qName.equals("METAR"))
		{
			inItem = true;
		}
	}
	/**
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Hashtable getWeatherInfo()
	{
		return weatherInfo;
	}
}