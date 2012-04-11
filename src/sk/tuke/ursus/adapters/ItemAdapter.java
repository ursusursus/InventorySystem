package sk.tuke.ursus.adapters;

import java.util.List;

import sk.tuke.ursus.R;
import sk.tuke.ursus.ViewHolder;
import sk.tuke.ursus.entities.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * Adaptér položiek
 * @author Vlastimil Breèka
 *
 */
public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {
	
	/**
	 * Biela farba
	 */
	private static final int WHITE = 0xFFFFFFFF;
	
	/**
	 * Tmavomodrá farba
	 */
	private static final int DARK_BLUE = 0xFF2B5BE5;
	
	/**
	 * Svetlomodrá farba
	 */
	private static final int LIGHT_BLUE = 0xFFDEDEEB;
	
	/**
	 * Sivá farba
	 */
	private static final int GRAY = 0xFF424242;
	
	
	/**
	 * Zoznam položiek
	 */
	private List<Item> items;
	
	/**
	 * Mód filtrovania, inicializovaný na - Všetky vidite¾né
	 */
	private int mode = 0;

	
	/**
	 * Konštruktor
	 * @param context Kontext
	 * @param textViewResourceId ID pre .xml resource
	 * @param items Zoznam položiek
	 */
	public ItemAdapter(Context context, int textViewResourceId, List<Item> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	/**
	 * Metóda getView, je stále volaná na danom listView.
	 * Ak je položka na sklade, nastaví pozadie na tmavomodré a farbu písma na bielu.
	 * Ak položka nie je na sklade, nastaví sa pozadie na svetlomodré a farbu písma na sivú
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.room_inventory_item, parent, false);

			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.item_name_textview);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Item item = items.get(position);

		holder.textView.setText(item.getDesc().toLowerCase());

		if (item.isInStock()) {
			convertView.setBackgroundColor(DARK_BLUE);
			holder.textView.setTextColor(WHITE);
		} else if (!(item.isInStock())) {
			convertView.setBackgroundColor(LIGHT_BLUE);
			holder.textView.setTextColor(GRAY);
		}

		handleViewFilter(convertView, item);
		
		return convertView;
	}

	/**
	 * Reaguje na rôzne filtrovacie módy.
	 * Módy sú:
	 * - 0 - Všetky vidite¾né.
	 * - 1 - Iba na sklade. 
	 * - 2 - Iba chýbajúce.
	 * @param view View
	 * @param item Položka
	 */
	private void handleViewFilter(View view, Item item) {
		switch (mode) {
		case 0:
			if (view.getVisibility() != View.VISIBLE) {
				view.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			if (item.isInStock()) {
				view.setVisibility(View.INVISIBLE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			if (!(item.isInStock())) {
				view.setVisibility(View.INVISIBLE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	/**
	 * Nastaví filtrovací mód
	 * @param mode Filtrovací mód
	 */
	public void setFilteringMode(int mode) {
		this.mode = mode;
	}

}
