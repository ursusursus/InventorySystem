package sk.tuke.ursus.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1925576220775476933L;
	
	private String name;
	
	private List<Item> content;

	public Room(String name) {
		content = new ArrayList<Item>();
		this.name = name;
	}

	
	public void addItem(Item item) {
		content.add(item);
	}
	
	
	public void reset() {
		for (Item i : content) {
			i.removeFromStock();
		}
	}

	
	public String getName() {
		return name;
	}

	
	public List<Item> getContentList() {
		return content;
	}

	
	public int getMissingCount() {

		int count = 0;

		for (Item i : content) {
			if (!(i.isInStock())) {
				count++;
			}
		}
		return count;
	}
	

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
