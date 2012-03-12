package sk.tuke.ursus.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.RoomAdapter;
import sk.tuke.ursus.entities.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import sk.tuke.ursus.R;

public class RoomSelection extends Activity {
	private ArrayList<Room> roomsList = new ArrayList<Room>();
	private Parser parser;
	private RoomAdapter adapter;
	private GridView gridView;
	private Animation shrink;
	private Animation enlarge;
	private MyApplication myApp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myApp = (MyApplication)getApplication();

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.room_selection);

		shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
		enlarge = AnimationUtils.loadAnimation(this, R.anim.enlarge);

		try {
			
			initRoomList();

			adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, roomsList);
			
			gridView = (GridView) findViewById(R.id.gridViewRooms);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {
					Intent intent = new Intent("sk.tuke.ursus.activities.ROOMINVENTORY");
					myApp.setCurrentRoom(roomsList.get(i));

					startActivity(intent);

				}

			});
		} catch (FileNotFoundException e) {
			scriptsFailedDialog();
		} catch (IOException e) {
			connectionFailedDialog();
		}
	}

	private void initRoomList() throws IOException {
		ArrayList<Room> tmpList = myApp.getRoomsList();
		if (tmpList == null) {
			//to je ten problem s ty mze neberie zmeneny xml link, opravit
			Toast.makeText(getApplicationContext(), "LOADING NEW", Toast.LENGTH_SHORT).show();
			initParser();
		} else {
			Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			roomsList = tmpList;
		}
	}

	private void initParser() throws IOException {
		parser = new Parser();
		parser.downloadXML(myApp.getXmlURL());
		parser.parseXML();
		roomsList = parser.getRoomsList();
		myApp.setRoomsList(roomsList);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void connectionFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection failed");
		builder.setMessage("Couldn't connect to server.\nPlease check your internet connection");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}
	
	private void scriptsFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection failed");
		builder.setMessage("Please make sure the URLs to scripts are correct and scripts are present on the server.");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.select_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			initParser();
			Toast.makeText(getApplicationContext(), "Rooms reloaded", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Reloading rooms failed", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

}