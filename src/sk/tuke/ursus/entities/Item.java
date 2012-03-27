package sk.tuke.ursus.entities;

import java.io.Serializable;

public class Item extends Entity implements Comparable<Item>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2773543494487194304L;
	private boolean inStock = false;
	private boolean isLatestInStock = true;
	private String ID;
	private String oldID;
	private String desc;
	private String quantity;
	private String room;
	public boolean animatable = true;

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

	@Override
	public int compareTo(Item i) {

		if (this.desc.compareTo(i.getDesc()) == 0) {
			return 0;
		} else if (this.desc.compareTo(i.getDesc()) == -1) {
			return -1;
		} else {
			return 1;
		}
	}

	public boolean isLatestInStock() {
		if (isLatestInStock) {
			return true;
		} else {
			return false;
		}
	}

	public void setLatestInStock(boolean latest) {
		isLatestInStock = latest;
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
