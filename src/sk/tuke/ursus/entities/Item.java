package sk.tuke.ursus.entities;

import java.io.Serializable;

/**
 * Poloûka skladu
 * @author Vlastimil BreËka
 *
 */
public class Item implements Serializable {

	/**
	 * ID pre serializ·ciu
	 */
	private static final long serialVersionUID = 2773543494487194304L;
	
	/**
	 * »i je poloûka na sklade
	 */
	private boolean inStock = false;
	
	/**
	 * ID poloûky
	 */
	private String ID;
	
	/**
	 * StarÈ ID poloûky
	 */
	private String oldID;
	
	/**
	 * Opis poloûky
	 */
	private String desc;
	
	/**
	 * Kvantita
	 */
	private String quantity;
	
	/**
	 * Miestnosù v ktorej sa poloûka nach·dza
	 */
	private String room;

	/**
	 * Konötruktor
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
	 * »i je na sklade
	 * @return True ak poloûka je, False ak nie je
	 */
	public boolean isInStock() {
		return inStock;
	}

	/**
	 * OznaËÌ poloûku ako n·jdenu
	 */
	public void putInStock() {
		if (inStock == false) {			
			inStock = true;
		}
	}

	/**
	 * OznaËÌ poloûku ako nen·jdenu
	 */
	public void removeFromStock() {
		if (inStock == true)
			inStock = false;
	}
	
	/**
	 * Vr·ti ID poloûky
	 * @return ID poloûky
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Vr·ti stare ID poloûky
	 * @return StarÈ ID poloûky
	 */
	public String getOldID() {
		return oldID;
	}

	/**
	 * Vr·ti popis poloûky 
	 * @return Popis poloûky 
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Vr·ti kvantitu poloûky
	 * @return Kvantita poloûky
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * Vr·ti miestnosù
	 * @return Miestnosù
	 */
	public String getRoom() {
		return room;
	}
}
