package sk.tuke.ursus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;

/**
 * XML Parser, implement·cia SAX parsera z Java API
 * @author Vlastimil BreËka
 *
 */
public class Parser extends DefaultHandler {
	
	/**
	 * Regul·rny v˝raz pre QR kÛd
	 */
	public static final String regexQR = "(EVID.C.: (........))";
	
	/**
	 * Regul·rny v˝raz pre .xml URL
	 */
	public static final String regexXML = "(http(s?)://)([A-Za-z0-9])(.[A-Za-z0-9]+)*(.xml)";
	
	/**
	 * Regul·rny v˝raz pre .php URL
	 */
	public static final String regexPHP = "(http(s?)://)([A-Za-z0-9])(.[A-Za-z0-9]+)*(.php)";
	
	/**
	 * Regul·rny v˝raz pre e-mailov˙ adresu
	 */
	public static final String regexEmail = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * DoËasn· poloûka
	 */
	private Item tempItem;
	
	/**
	 * DoËasn· miestnosù
	 */
	private Room tempRoom;
	
	/**
	 * Zoznam miestnostÌ ktor· bude vr·ten·
	 */
	private ArrayList<Room> roomsList;
	
	/**
	 * SAX parser
	 */
	private SAXParser parser;
	
	
	/**
	 * Konötruktor
	 */
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

	/**
	 * Ak bol n·jden˝ zaËiatoËn˝ xml tag
	 */
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
	
	/**
	 * Ak bol n·jden˝ uzatv·racÌ xml tag
	 */
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
	
	
	/**
	 * Stiahne a preparsuje .xml zdrojov˝ s˙bor
	 * @param url Cesta k .xml zdroju
	 * @return Zoznam miestnosti
	 * @throws SAXException 
	 * @throws IOException
	 */
	public ArrayList<Room> parseXML(String url) throws SAXException, IOException {
		parser.parse(url, this);	
		return roomsList;
	}
	
	/**
	 * Preparsuje obsah naËÌtanÈho QR kÛdu
	 * @param input Obsah QR kÛdu
	 * @return ID poloûky
	 * @throws SAXException Obsah QR kÛdu nie je v InventorySystem form·te
	 */
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
