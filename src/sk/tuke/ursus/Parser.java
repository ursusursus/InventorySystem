package sk.tuke.ursus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;

public class Parser extends DefaultHandler {
	
	private Item tempItem;
	private Room tempRoom;
	private javax.xml.parsers.SAXParser parser;
	
	public static final String regexQR = "(EVID.C.: (........))";
	public static final String regexXML = "(http(s?)://)([A-Za-z0-9])(.[A-Za-z0-9]+)*(.xml)";
	public static final String regexPHP = "(http(s?)://)([A-Za-z0-9])(.[A-Za-z0-9]+)*(.php)";
	public static final String regexEmail = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public Parser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<Room> roomsList;

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
		
		if(qName.equalsIgnoreCase("inventory")) {
			roomsList = new ArrayList<Room>(); 
		} else if (qName.equalsIgnoreCase("room")) {
			tempRoom = new Room(attr.getValue("name"));
		} else if(qName.equalsIgnoreCase("item")) {
			if(attr.getValue("EVID.C.") != null && attr.getValue("Stare_C.") != null && attr.getValue("Opis") != null && attr.getValue("Kusov") != null && attr.getValue("Miestnost") != null) {
				tempItem = new Item(attr.getValue("EVID.C."),attr.getValue("Stare_C."),attr.getValue("Opis"),attr.getValue("Kusov"),attr.getValue("Miestnost"));
			} else {
				throw new SAXException("One or more attributes are missing.");
			}
		} else {
			throw new SAXException("Invalid item.");
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if(qName.equalsIgnoreCase("inventory")) {
			
		} else if(qName.equalsIgnoreCase("room")) {
			roomsList.add(tempRoom);
		} else if(qName.equalsIgnoreCase("item")) {
			tempRoom.addItem(tempItem);
		} else {
			throw new SAXException("Invalid item.");
		}
	}
	
	public ArrayList<Room> parseXML(String url) throws SAXException, IOException {
		parser.parse(url, this);	
		return roomsList;
	}
	
	public String parseQRCode(String input) throws SAXException {
		Pattern p = Pattern.compile(regexQR);
		Matcher m = p.matcher(input);
		if (m.find()) {
			return m.group(2);
		} else {
			throw new SAXException("Invalid QR code format");
		}
	}


	
}
