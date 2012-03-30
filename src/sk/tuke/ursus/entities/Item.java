package sk.tuke.ursus.entities;

import java.io.Serializable;

public class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2773543494487194304L;
	
	private boolean inStock = false;
	
	private String ID;
	private String oldID;
	private String desc;
	private String quantity;
	private String room;

	public Item(String ID, String oldID, String desc, String quantity, String room) {
		this.ID = ID;
		this.oldID = oldID;
		this.desc = desc;
		this.quantity = quantity;
		this.room = room;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void putInStock() {
		if (inStock == false) {			
			inStock = true;
		}
	}

	public void removeFromStock() {
		if (inStock == true)
			inStock = false;
	}
	
	public String getID() {
		return ID;
	}

	public String getOldID() {
		return oldID;
	}

	public String getDesc() {
		return desc;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getRoom() {
		return room;
	}
}
