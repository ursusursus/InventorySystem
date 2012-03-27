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

public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {

	private List<Item> items;
	private int mode = 0;

	public ItemAdapter(Context context, int textViewResourceId, List<Item> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

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
			convertView.setBackgroundColor(0xFF2B5BE5);
			holder.textView.setTextColor(0xFFFFFFFF);
		} else if (!(item.isInStock())) {
			convertView.setBackgroundColor(0xFFDEDEEB);
			holder.textView.setTextColor(0xFF424242);
		}

		handleViewFilter(convertView, item);
		
		return convertView;
	}

	
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

	public void setFilteringMode(int mode) {
		this.mode = mode;
	}

}
