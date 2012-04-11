package sk.tuke.ursus;

import java.io.Serializable;
import java.util.ArrayList;

import sk.tuke.ursus.entities.Room;


import android.app.Application;

/**
 * Podtrieda Application, umoòuje dra globálne premenné v aplikácií
 * @author Vlastimil Breèka
 *
 */
public class MyApplication extends Application implements Serializable {
	
	/**
	 * ID pre serializáciu 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Aktuálna miestnos
	 */
	private Room currentRoom;
	
	/**
	 * Zoznam miestností
	 */
	private ArrayList<Room> roomsList;
	
	/**
	 * Zoznam e-mailovıch adries pre e-mailovú notifikáciu
	 */
	private ArrayList<String> emailAddresses;
	
	/**
	 * Cesta k .xml zdrojovému súboru
	 */
	private String xmlURL;
	
	/**
	 * Cesta k .php exportovaciemu skriptu
	 */
	private String phpURL;
	
	/**
	 * Èi je moné zaèa inventúru, 
	 * resp. èi sú všetky cesty a aspoò jedna e-mailová adresa, nastavené
	 */
	private boolean isReadyToStart = false;
	
	
	/**
	 * Názov súboru na disku v ktorom sú uloené nastavenia
	 */
	public static final String FILENAME = "settings.invsys";

	/**
	 * Vráti zoznam miestností
	 * @return Zoznam miestností
	 */
	public ArrayList<Room> getRoomsList() {
		return roomsList;
	}

	/**
	 * Nastavi zoznam miestností
	 * @param roomsList Zoznam miestnosti
	 */
	public void setRoomsList(ArrayList<Room> roomsList) {
		this.roomsList = roomsList;
	}

	/**
	 * Vráti aktuálnu miestnost
	 * @return Aktuálna miestnos v ktorej prebieha inventúra
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Nastaví aktuálnu miestnos
	 * @param room Aktuálna miestnos
	 */
	public void setCurrentRoom(Room room) {
		this.currentRoom = room;
	}
	
	/**
	 * Vráti e-mailové adresy pre notifikáciu
	 * @return E-mailové adresy
	 */
	public ArrayList<String> getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * Nastaví e-mailové adresy pre notifikáciu
	 * @param emailAddresses Pre notifikaciu
	 */
	public void setEmailAddresses(ArrayList<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * Vráti cestu k .xml súboru
	 * @return Cesta k .xml súboru
	 */
	public String getXmlURL() {
		return xmlURL;
	}

	/**
	 * Vráti cestu k .php skriptu
	 * @return Cesta k .php skriptu
	 */
	public String getPhpURL() {
		return phpURL;
	}

	/**
	 * Nastaví cestu k .xml súboru
	 * @param xmlURL Cesta k .xml súboru
	 */
	public void setXmlURL(String xmlURL) {
		this.xmlURL = xmlURL;
	}

	/**
	 * Nastaví cestu k .php skriptu
	 * @param phpURL Cesta k .php skriptu
	 */
	public void setPhpURL(String phpURL) {
		this.phpURL = phpURL;
	}

	/**
	 * Èi je moné zaèa inventúru. 
	 * Inventúru je moné zaèa ak je nastavená .xml cesta, .php cesta a aspoò jedna e-mailová adresa
	 * @return True ak je moné zaèa, False ak nie
	 */
	public boolean isReadyToStart() {
		return isReadyToStart;
	}

	/**
	 * Nastaví, èi je moné zaèa inventúru
	 * @param isReady Èi je moné zaèa
	 */
	public void setReadyToStart(boolean isReady) {
		this.isReadyToStart = isReady;
	}
}
