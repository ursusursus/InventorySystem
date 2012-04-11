package sk.tuke.ursus.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Miestnos s polokami
 * @author Vlastimil Breèka
 *
 */
public class Room implements Serializable {

	/**
	 * ID pre serializáciu
	 */
	private static final long serialVersionUID = -1925576220775476933L;
	
	/**
	 * Názov miestnosti
	 */
	private String name;
	
	/**
	 * Obsah miestnosti
	 */
	private List<Item> content;

	/**
	 * Konštruktor
	 * @param name Meno miestnosti
	 */
	public Room(String name) {
		content = new ArrayList<Item>();
		this.name = name;
	}

	/**
	 * Pridá poloku do miestnosti
	 * @param item Poloka, ktorú chceme prida do miestnosti
	 */
	public void addItem(Item item) {
		content.add(item);
	}
	
	/**
	 * Zresetuje miestnos,
	 * to znamená e nastaví všetky poloky ako nenájdené
	 */
	public void reset() {
		for (Item i : content) {
			i.removeFromStock();
		}
	}

	
	/**
	 * Vráti meno miestnosti
	 * @return Meno miestnosti
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Vráti obsah miestnosti
	 * @return Obsah miestnosti
	 */
	public List<Item> getContentList() {
		return content;
	}

	/**
	 * Vráti poèet chıbajúcich poloiek
	 * @return Pocet chıbajúcich poloiek
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
	 * Vráti poèet poloiek na sklade
	 * @return Poèet poloiek na sklade
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
