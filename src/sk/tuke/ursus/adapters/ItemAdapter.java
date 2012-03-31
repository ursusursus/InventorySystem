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
 * Adapter poloziek
 * @author Vlastimil Brecka
 *
 */
public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {
	
	/**
	 * Biela farba
	 */
	private static final int WHITE = 0xFFFFFFFF;
	
	/**
	 * Tmavomodra farba
	 */
	private static final int DARK_BLUE = 0xFF2B5BE5;
	
	/**
	 * Svetlomodra farba
	 */
	private static final int LIGHT_BLUE = 0xFFDEDEEB;
	
	/**
	 * Siva farba
	 */
	private static final int GRAY = 0xFF424242;
	
	
	/**
	 * Zoznam poloziek
	 */
	private List<Item> items;
	
	/**
	 * Mod filtrovania inicializovany na - vsetky viditelne
	 */
	private int mode = 0;

	/**
	 * Konstruktor
	 * @param context Kontext
	 * @param textViewResourceId ID pre .xml resource
	 * @param items Zoznam poloziek
	 */
	public ItemAdapter(Context context, int textViewResourceId, List<Item> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	/**
	 * Metoda getView, je stale volana na danom listView
	 * Ak je polozka na sklade, nastavi pozadie na tmavomodre a farbu pisma na bielu.
	 * Ak polozka nie je na sklade, nastavi sa pozadie na svetlomodre a farba pisma na sivu
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
	 * Reaguje na rozne filtrovacie mody
	 * @param view View
	 * @param item Polozka
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
	 * Nastavi filtrovaci mod
	 * @param mode Filtrovaci mod
	 */
	public void setFilteringMode(int mode) {
		this.mode = mode;
	}

}
