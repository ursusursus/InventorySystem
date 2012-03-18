package sk.tuke.ursus.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.RoomAdapter;
import sk.tuke.ursus.entities.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myApp = (MyApplication) getApplication();

		// fullscreen
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.room_selection);

		initRoomList();

		adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, roomsList);

		gridView = (GridView) findViewById(R.id.gridViewRooms);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {

				//Intent intent = new Intent("sk.tuke.ursus.activities.ROOMINVENTORY");
				Intent intent = new Intent(getApplicationContext(), RoomInventory.class);
				myApp.setCurrentRoom(roomsList.get(i));
				startActivity(intent);
			}

		});
		
	}

	private void initRoomList() {
		ArrayList<Room> tmpList = myApp.getRoomsList();
		if (tmpList == null) {
			// to je ten problem s ty mze neberie zmeneny xml link, opravit
			Toast.makeText(getApplicationContext(), "LOADING NEW!", Toast.LENGTH_SHORT).show();
			initParser();
		} else {
			Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			roomsList = tmpList;
		}
	}

	private void initParser() {

		try {
			
			parser = new Parser();
			parser.downloadXML(myApp.getXmlURL());
			parser.parseXML();
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
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.select_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		initParser();
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

	public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> {

		int myProgress;

		@Override
		protected void onPreExecute() {
			myProgress = 0;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while (myProgress < 100) {
				myProgress++;
				publishProgress(myProgress);
				SystemClock.sleep(100);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			progressDialog.setProgress(values[0]);
		}

	}

}