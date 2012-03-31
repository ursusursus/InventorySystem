package sk.tuke.ursus.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Miestnost
 * @author Vlastimil Brecka
 *
 */
public class Room implements Serializable {

	/**
	 * ID pre serializaciu
	 */
	private static final long serialVersionUID = -1925576220775476933L;
	
	/**
	 * Nazov miestnosti
	 */
	private String name;
	
	/**
	 * Obsah miestnosti
	 */
	private List<Item> content;

	/**
	 * Konstruktor
	 * @param name Meno miestnosti
	 */
	public Room(String name) {
		content = new ArrayList<Item>();
		this.name = name;
	}

	/**
	 * Prida polozku do miestnosti
	 * @param item Polozka
	 */
	public void addItem(Item item) {
		content.add(item);
	}
	
	/**
	 * Zresetuje miestnost
	 */
	public void reset() {
		for (Item i : content) {
			i.removeFromStock();
		}
	}

	
	/**
	 * Vrati meno miestnosti
	 * @return Meno miestnost
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Vrati obsah miestnosti
	 * @return Obsah miestnosti
	 */
	public List<Item> getContentList() {
		return content;
	}

	/**
	 * Vrati pocet chybajucich poloziek
	 * @return Pocet chybajucich poloziek
	 */
	public int getMissingCount() {

		int count = 0;

		for (Item i : content) {
			if (!(i.isInStock())) {
				count++;
			}
		}
		return count;
	}
	

	/**
	 * Vrati pocet poloziek na sklade
	 * @return Pocet poloziek na sklade
	 */
	public int getInStockCount() {
		int count = 0;

		for (Item i : content) {
			if ((i.isInStock())) {
				count++;
			}
		}
		return count;
	}
}
