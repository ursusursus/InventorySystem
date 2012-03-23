package sk.tuke.ursus;

import java.io.Serializable;
import java.util.ArrayList;

import sk.tuke.ursus.entities.Room;


import android.app.Application;

public class MyApplication extends Application implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Room currentRoom;
	private ArrayList<Room> roomsList;
	private ArrayList<String> emailAddresses;
	private String xmlURL;
	private String phpURL;
	private boolean isReadyToStart = false;
	public static final String FILENAME = "settings.invsys";

	public ArrayList<Room> getRoomsList() {
		return roomsList;
	}

	public void setRoomsList(ArrayList<Room> roomsList) {
		this.roomsList = roomsList;
	}

	public ArrayList<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(ArrayList<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(Room room) {
		this.currentRoom = room;
	}

	public String getXmlURL() {
		return xmlURL;
	}

	public String getPhpURL() {
		return phpURL;
	}

	public void setXmlURL(String xmlURL) {
		this.xmlURL = xmlURL;
	}

	public void setPhpURL(String phpURL) {
		this.phpURL = phpURL;
	}

	public boolean isReadyToStart() {
		return isReadyToStart;
	}

	public void setReadyToStart(boolean isReady) {
		this.isReadyToStart = isReady;
	}
}
