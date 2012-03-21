package sk.tuke.ursus.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;

import sk.tuke.ursus.HTTPConnectionHelper;
import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.RoomAdapter;
import sk.tuke.ursus.entities.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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
	private ProgressDialog progressDialog;
	private ProgressDialog pd;
	private Handler handler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myApp = (MyApplication) getApplication();

		setContentView(R.layout.room_selection);

		// initRoomList();
		// init();
		// downloadContent();
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				initRooms();
			}

		};

		initRoomList();

		adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, roomsList);

		gridView = (GridView) findViewById(R.id.gridViewRooms);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {
				
				Intent intent = new Intent(getApplicationContext(), RoomInventory.class);
				myApp.setCurrentRoom(roomsList.get(i));
				startActivity(intent);
			}

		});

	}

	private void init() {
		ArrayList<Room> tmpList = myApp.getRoomsList();
		if (tmpList == null) {
			Toast.makeText(getApplicationContext(), "LOADING NEW!", Toast.LENGTH_SHORT).show();
			downloadContent();
		} else {
			Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			roomsList = tmpList;
			// adapter.notifyDataSetChanged();
			// initRoomList();
		}

	}

	private void downloadContent() {

		pd = ProgressDialog.show(this, "Please, wait...", "Loading from server.");

		new Thread(new Runnable() {

			@Override
			public void run() {
				initParser2();
				handler.sendEmptyMessage(0);
			}

		}).start();

	}

	protected void initRooms() {

		roomsList = parser.getRoomsList();
		myApp.setRoomsList(roomsList);

		adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, roomsList);

		gridView = (GridView) findViewById(R.id.gridViewRooms);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {

				Intent intent = new Intent(getApplicationContext(), RoomInventory.class);
				myApp.setCurrentRoom(roomsList.get(i));
				startActivity(intent);
			}

		});

		Toast.makeText(getApplicationContext(), "Rooms loaded sucessfully.", Toast.LENGTH_SHORT).show();
		pd.dismiss();

	}

	private void initRoomList() {
		ArrayList<Room> tmpList = myApp.getRoomsList();
		if (tmpList == null) {
			initParser();
		} else {
			Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			roomsList = tmpList;
		}
	}

	private void initParser2() {

		try {

			parser = new Parser();
			parser.downloadXML(myApp.getXmlURL());
			//parser.parseXML();
			roomsList = parser.getRoomsList();
			// myApp.setRoomsList(roomsList);
			// Toast.makeText(getApplicationContext(),
			// "Rooms loaded sucessfully.", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			sourceNotFoundDialog();
			e.printStackTrace();
		} catch (UnknownHostException e) {
			invalidURLDialog();
			e.printStackTrace();
		} catch (IOException e) {
			connectionFailedDialog();
			e.printStackTrace();
		}
	}
	
	//oskusat na telefone
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}


	private void initParser() {
		
		try {
			
			parser = new Parser();
			//parser.downloadXML(myApp.getXmlURL());
			String result = HTTPConnectionHelper.download();
			parser.parseXML(result);
			roomsList = parser.getRoomsList();
			myApp.setRoomsList(roomsList);
			Toast.makeText(getApplicationContext(), "Rooms loaded sucessfully.", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			sourceNotFoundDialog();
			e.printStackTrace();
		} catch (UnknownHostException e) {
			invalidURLDialog();
			e.printStackTrace();
		} catch (IOException e) {
			connectionFailedDialog();
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.roomselection_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		initParser();
		//downloadContent();
		return true;
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

	private void sourceNotFoundDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection failed");
		builder.setMessage("Please make sure the .xml source is present on the server.");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}

	private void invalidURLDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection failed");
		builder.setMessage("Please make sure the URL to .xml source is correct.");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}

}