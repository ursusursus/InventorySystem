package sk.tuke.ursus.adapters;

import java.util.List;

import sk.tuke.ursus.entities.Item;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import sk.tuke.ursus.R;

public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {

	private List<Item> items;
	private Context context;
	private int mode = 0;
	private Animation fadeAway;

	public ItemAdapter(Context context, int textViewResourceId, List<Item> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.fadeAway = AnimationUtils.loadAnimation(context, R.anim.fade_away);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		// if (convertView == null) { 
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.room_inventory_item, parent, false);
	/*	 }
		else {
			view =  convertView;
		}*/
		Item item = items.get(position);

		TextView tv = (TextView) view.findViewById(R.id.item_name_textview);
		tv.setText(item.getDesc().toLowerCase());
		/*
		 * if (item.isInStock() && item.isLatestInStock()) {
		 * Log.i(String.valueOf(position), "FIALOVY");
		 * tv.setTextColor(0xFFFFFFFF); view.setBackgroundColor(0xFF8904B1); }
		 */

		if (item.isInStock()) {
			if (item.isLatestInStock()) {
				Log.i(String.valueOf(position), "FIALOVY");
				tv.setTextColor(0xFFFFFFFF);
				view.setBackgroundColor(0xFF8904B1);
				item.setNotLatestInStock();
			} else {
				Log.i(String.valueOf(position), "MODRY");
				view.setBackgroundColor(0xFF2B5BE5);
				tv.setTextColor(0xFFFFFFFF);
			}
		} else if (!(item.isInStock())) {
			Log.i(String.valueOf(position), "SVETLY");
			view.setBackgroundColor(0xFFDEDEEB);
			tv.setTextColor(0xFF424242);
		}
		/*
		 * if (!(item.isInStock())) { Log.i("je", "SVETLY");
		 * view.setBackgroundColor(0xFFDEDEEB); tv.setTextColor(0xFF424242); }
		 * if (item.isInStock()) { Log.i("je", "MODRY");
		 * view.setBackgroundColor(0xFF2B5BE5); tv.setTextColor(0xFFFFFFFF); }
		 * if (item.isInStock() && item.isLatestInStock()) { Log.i("je",
		 * "FIALOVY"); tv.setTextColor(0xFFFFFFFF);
		 * view.setBackgroundColor(0xFF8904B1); //item.setNotLatestInStock(); }
		 */

		switch (mode) {
		case 0:
			break;
		case 1:
			if (item.isInStock()) {
				if (item.animatable == true) {
					view.startAnimation(fadeAway);
					item.animatable = false;
				} else {
					view.setVisibility(View.GONE);
				}
			}
			break;
		case 2:
			if (!(item.isInStock())) {
				view.startAnimation(fadeAway);
				// view.setVisibility(View.GONE);
			}
			break;
		}
		
		
		return view;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

}
