package sk.tuke.ursus.entities;

import java.io.Serializable;

/**
 * Polozka
 * @author Vlastimil Brecka
 *
 */
public class Item implements Serializable {

	/**
	 * ID pre serializaciu
	 */
	private static final long serialVersionUID = 2773543494487194304L;
	
	/**
	 * Ci je polozka na sklade
	 */
	private boolean inStock = false;
	
	/**
	 * ID polozky
	 */
	private String ID;
	
	/**
	 * Stare ID polozky
	 */
	private String oldID;
	
	/**
	 * Opis polozky
	 */
	private String desc;
	
	/**
	 * Kvantita
	 */
	private String quantity;
	
	/**
	 * Miestnost v ktorej sa polozka nachadza
	 */
	private String room;

	/**
	 * Konstruktor
	 * @param ID ID polozky 
	 * @param oldID Stare ID polozky 
	 * @param desc Opis polozky
	 * @param quantity Kvantita polozky
	 * @param room Miestnost v ktorej sa nachadza polozka
	 */
	public Item(String ID, String oldID, String desc, String quantity, String room) {
		this.ID = ID;
		this.oldID = oldID;
		this.desc = desc;
		this.quantity = quantity;
		this.room = room;
	}

	/**
	 * Ci je na sklade
	 * @return True ak polozka je, False ak nie je
	 */
	public boolean isInStock() {
		return inStock;
	}

	/**
	 * Oznaci polozku ako najdenu
	 */
	public void putInStock() {
		if (inStock == false) {			
			inStock = true;
		}
	}

	/**
	 * Oznaci polozku ako nenajdenu
	 */
	public void removeFromStock() {
		if (inStock == true)
			inStock = false;
	}
	
	/**
	 * Vrati ID polozky
	 * @return ID polozky
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Vrati stare ID polozky
	 * @return Stare ID polozky
	 */
	public String getOldID() {
		return oldID;
	}

	/**
	 * Vrati popis polozky 
	 * @return Popis polozky 
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Vrati kvantitu polozky
	 * @return Kvantita polozky
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * Vrati miestnost
	 * @return Miestnost
	 */
	public String getRoom() {
		return room;
	}
}
