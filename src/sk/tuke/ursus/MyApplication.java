package sk.tuke.ursus;

import java.io.Serializable;
import java.util.ArrayList;

import sk.tuke.ursus.entities.Room;


import android.app.Application;

/**
 * Podtrieda Application, umoznuje drzat globalne premenne v aplikacii
 * @author Vlastimil Brecka
 *
 */
public class MyApplication extends Application implements Serializable {
	
	/**
	 * ID pre serializaciu 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Aktualna miestnost
	 */
	private Room currentRoom;
	
	/**
	 * Zoznam miestnosti
	 */
	private ArrayList<Room> roomsList;
	
	/**
	 * Zoznam e-mailovych adries pre e-mailovu notifikaciu
	 */
	private ArrayList<String> emailAddresses;
	
	/**
	 * Cesta k .xml zdrojovemu suboru
	 */
	private String xmlURL;
	
	/**
	 * Cesta k .php exportovaciemu skriptu
	 */
	private String phpURL;
	
	/**
	 * Ci je mozne spustit inventuru
	 */
	private boolean isReadyToStart = false;
	
	
	/**
	 * Nazov suboru na disku v ktoromu su ulozene nastavenia
	 */
	public static final String FILENAME = "settings.invsys";

	/**
	 * Vrati zoznam miestnosti
	 * @return zoznam miestnosti
	 */
	public ArrayList<Room> getRoomsList() {
		return roomsList;
	}

	/**
	 * Nastavi zoznam miestnosti
	 * @param roomsList zoznam miestnosti
	 */
	public void setRoomsList(ArrayList<Room> roomsList) {
		this.roomsList = roomsList;
	}

	/**
	 * Vrati aktualnu miestnosti
	 * @return aktualna miestnost
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Nastavi aktualnu miestnost
	 * @param room aktualna miestnost
	 */
	public void setCurrentRoom(Room room) {
		this.currentRoom = room;
	}
	
	/**
	 * Vrati e-mailove adresy
	 * @return e-mailove adresy
	 */
	public ArrayList<String> getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * Nastavi e-mailove adresy
	 * @param emailAddresses pre notifikaciu
	 */
	public void setEmailAddresses(ArrayList<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * Vrati cestu k .xml suboru
	 * @return cesta k .xml suboru
	 */
	public String getXmlURL() {
		return xmlURL;
	}

	/**
	 * Vrati cestu k .php skriptu
	 * @return cesta k .php skriptu
	 */
	public String getPhpURL() {
		return phpURL;
	}

	/**
	 * Nastavi cestu k .xml suboru
	 * @param xmlURL cesta k .xml suboru
	 */
	public void setXmlURL(String xmlURL) {
		this.xmlURL = xmlURL;
	}

	/**
	 * Nastavi cestu k .php skriptu
	 * @param phpURL cesta k .php skriptu
	 */
	public void setPhpURL(String phpURL) {
		this.phpURL = phpURL;
	}

	/**
	 * Ci je mozne zacat inventuru. 
	 * Inventuru je mozne zacat ak je nastava .xml cesta, .php cesta a aspon jedna e-mailova adresa
	 * @return True - ak je mozne zacat, False - ak nie
	 */
	public boolean isReadyToStart() {
		return isReadyToStart;
	}

	/**
	 * Nastavi ci je mozne zacat inventuru
	 * @param isReady ci je mozne zacat
	 */
	public void setReadyToStart(boolean isReady) {
		this.isReadyToStart = isReady;
	}
}
