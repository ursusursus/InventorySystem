package sk.tuke.ursus.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.R;
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

/**
 * Aktivita výberu miestnosti
 * @author Vlastimil Brecka
 *
 */
public class RoomSelection extends Activity {
	
	/**
	 * Konstanta dialogu zlyhania pripojenia
	 */
	private static final int CONNECTION_FAILED = 0;
	
	/**
	 * Konstanta dialogu zlyhania parsovania
	 */
	private static final int PARSING_ERROR = 1;

	/**
	 * Premenna aplikacie, drzi globalne premenne 
	 */
	private MyApplication app;
	
	/**
	 * Premenna vibratoru
	 */
	private Vibrator vibrator;
	
	/**
	 * Adapter miestnosti
	 */
	private RoomAdapter adapter;
	
	/**
	 * GriView
	 */
	private GridView gridView;
	
	/**
	 * Dialog progresu
	 */
	private ProgressDialog dialog;
	
	
	/**
	 * Metoda onCreate
	 */
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

	/**
	 * Inicializuje miestnosti, ak este neboli nacitane, stiahne .xml subor a preparsuje ho
	 */
	private void init() {
		ArrayList<Room> tmpList = app.getRoomsList();
		if (tmpList == null) {
			downloadAndParse();
		} else {
			//Toast.makeText(getApplicationContext(), "RE-LOADING", Toast.LENGTH_SHORT).show();
			adapter = new RoomAdapter(getApplicationContext(), R.layout.room_selection_item, tmpList);
			gridView.setAdapter(adapter);
		}
	}
	
	/**
	 * Stiahne .xml subor a preparsuje ho
	 */
	private void downloadAndParse() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Please,wait");
		dialog.setMessage("Processing...");
		DownloadAndParseTask task = new DownloadAndParseTask();
		task.execute(app.getXmlURL());
	}

	
	/**
	 * Metoda onResume
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Vytvori menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.roomselection_menu, menu);
		return true;
	}

	/**
	 * Metoda onOptionsItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		downloadAndParse();
		return true;
	}
	
	/**
	 * Metoda onCreateDialog
	 */
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

	/**
	 * Asynchronna uloha, stiahne a preparsuje zdrojovy .xml subor
	 * @author Vlastimil Brecka
	 *
	 */
	private class DownloadAndParseTask extends AsyncTask<String, Void, ArrayList<Room>> {

		/**
		 * Vynimka
		 */
		private Exception e = null;

		/**
		 * Uloha na pozadi
		 */
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

		/**
		 * Pred vykonanim
		 */
		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		/**
		 * Po vykonani, zrusi dialog a vrati preparsovane miestnosti
		 */
		@Override
		protected void onPostExecute(ArrayList<Room> result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (e == null) {
				app.setRoomsList(result);
				adapter = new RoomAdapter(getApplicationContext(), R.layout.room_selection_item, result);
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