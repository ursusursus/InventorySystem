package sk.tuke.ursus.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.R;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.RoomAdapter;
import sk.tuke.ursus.entities.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class RoomSelection extends Activity {
	
	private static final int CONNECTION_FAILED = 0;
	private static final int PARSING_ERROR = 1;

	private MyApplication app;
	private Vibrator vibrator;
	
	private RoomAdapter adapter;
	private GridView gridView;
	private ProgressDialog dialog;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (MyApplication) getApplication();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		setContentView(R.layout.room_selection);

		gridView = (GridView) findViewById(R.id.gridViewRooms);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {
				
				vibrator.vibrate(30);

				Intent intent = new Intent(getApplicationContext(), RoomInventory.class);
				app.setCurrentRoom(app.getRoomsList().get(i));
				startActivity(intent);
			}

		});

		init();

	}

	private void init() {
		ArrayList<Room> tmpList = app.getRoomsList();
		if (tmpList == null) {
			downloadAndParse();
		} else {
			Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, tmpList);
			gridView.setAdapter(adapter);
		}
	}
	
	private void downloadAndParse() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Please,wait");
		dialog.setMessage("Processing...");
		DownloadAndParseTask task = new DownloadAndParseTask();
		task.execute(app.getXmlURL());
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
		downloadAndParse();
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case CONNECTION_FAILED:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Conncetion failed")
					.setMessage("Please check your internet connection and make sure the URL to .xml source is correct.")
					.setNeutralButton("Dismiss", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create();
			break;
		case PARSING_ERROR:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Parsing error")
					.setMessage("Source .xml file is malformed or not in valid Inventory System format. Please check source file for errors.")
					.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
				
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					}).create();
			break;
		}
		return dialog;
	}


	/*private void connectionFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Connection failed");
		builder.setMessage("Couldn't connect to server. Please check your internet connection.");
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
		builder.setTitle("Unable to establish connection");
		builder.setMessage("Please make the URL to .xml source is correct.");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}

	private void parsingErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Parsing error");
		builder.setMessage("Source .xml file not in valid Inventory System format. Please check source file for errors.");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();

	}*/


	private class DownloadAndParseTask extends AsyncTask<String, Void, ArrayList<Room>> {

		private Exception e = null;

		@Override
		protected ArrayList<Room> doInBackground(String... urls) {

			ArrayList<Room> list = new ArrayList<Room>();
			for (String url : urls) {
				try {
					list = new Parser().parseXML(url);
				} catch (Exception e) {
					this.e = e;
				}
			}
			return list;
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected void onPostExecute(ArrayList<Room> result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (e == null) {
				app.setRoomsList(result);
				adapter = new RoomAdapter(getApplicationContext(), R.layout.room_item, result);
				gridView.setAdapter(adapter);
			} else {
				if (e instanceof SAXException) {
					showDialog(PARSING_ERROR);
					e.printStackTrace();
				} else if (e instanceof IOException) {
					showDialog(CONNECTION_FAILED);
					e.printStackTrace();
				} 
			}
		}
	}

}