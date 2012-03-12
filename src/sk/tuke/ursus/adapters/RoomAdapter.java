package sk.tuke.ursus.adapters;

import java.util.List;

import sk.tuke.ursus.entities.Room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.tuke.ursus.R;

public class RoomAdapter extends ArrayAdapter<Room> {

	private List<Room> rooms;
	private Context context;

	public RoomAdapter(Context context, int textViewResourceId, List<Room> rooms) {
		super(context, textViewResourceId, rooms);
		this.context = context;
		this.rooms = rooms;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		LayoutInflater inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.room_item, parent, false);

		Room room = rooms.get(position);

		TextView tv = (TextView) view.findViewById(R.id.room_name_textview);
		tv.setText(room.getName());

		return view;
	}

}
