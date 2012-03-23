package sk.tuke.ursus.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room extends Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1925576220775476933L;
	private String name;
	private List<Item> content = new ArrayList<Item>();

	public Room(String name) {
		this.name = name;
	}

	public void addItem(Item item) {
		content.add(item);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("///////\n");
		for (Item i : content) {
			sb.append(i.getDesc());
		}
		return sb.toString();
	}

	public String[] getMissingItems() {

		String[] temp = new String[getMissingCount()];
		int j = 0;

		for (Item i : content) {
			if (!(i.isInStock())) {
				temp[j++] = i.getDesc();
			}
		}
		return temp;
	}

	public String[] getContentNamesArrayy() {
		String[] temp = new String[content.size()];
		Collections.sort(content);

		for (int i = 0; i < content.size(); i++) {
			temp[i] = content.get(i).getDesc();
		}

		return temp;
	}

	public List<Item> getContentList() {
		Collections.sort(content);
		return content;
	}

	public List<Item> getContentList(boolean inStockOnly) {
		List<Item> separatedList = new ArrayList<Item>();
		if (inStockOnly == true) {
			for (Item i : content) {
				if (i.isInStock()) {
					separatedList.add(i);
				}
			}
		} else {
			for (Item i : content) {
				if (!i.isInStock()) {
					separatedList.add(i);
				}
			}
		}
		Collections.sort(separatedList);
		return separatedList;
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

	public void reset() {
		for (Item i : content) {
			i.removeFromStock();
		}
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
