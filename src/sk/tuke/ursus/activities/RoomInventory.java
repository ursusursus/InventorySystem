package sk.tuke.ursus.activities;

import java.util.ArrayList;
import java.util.zip.Inflater;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.ItemAdapter;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import sk.tuke.ursus.R;

public class RoomInventory extends Activity implements View.OnClickListener {

	private Room currentRoom;
	private Button filterButton, resetButton, finishButton;
	private ImageButton cameraButton;
	private AlertDialog resetDialog, viewDialog, saveDialog, notFoundDialog;
	private ListView listView;
	private ItemAdapter adapter;
	private TextView itemCount;
	private GridView gridView;
	private GestureLibrary gestureLib;
	private Animation anim;
	private Animation popup;
	private float x0, y0, x1, y1;
	private LinearLayout lay;
	private int position;
	private Vibrator vibrator;
	private ViewPager viewPager;
	private Parser qrParser;
	private String tmpInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		x0 = x1 = y0 = y1 = 0;

		this.setContentView(R.layout.room);

		ArrayList<LinearLayout> list = new ArrayList<LinearLayout>();
		LinearLayout inv = (LinearLayout) View.inflate(this, R.layout.room_inventory, null);
		LinearLayout cam = (LinearLayout) View.inflate(this, R.layout.room_camera, null);
		list.add(inv);
		list.add(cam);

		viewPager = (ViewPager) findViewById(R.id.roomPager);
		ViewPagerAdapter pageAdapter = new ViewPagerAdapter(getApplicationContext(), list);
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				if (index == 1) {
					launchCamera();
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		MyApplication a = ((MyApplication) getApplicationContext());
		currentRoom = a.getCurrentRoom();

		anim = AnimationUtils.loadAnimation(this, R.anim.shrink);
		popup = AnimationUtils.loadAnimation(this, R.anim.popup);

		TextView roomNameTextView = (TextView) inv.findViewById(R.id.roomName);
		roomNameTextView.setText(currentRoom.getName());

		TextView itemCountTotal = (TextView) inv.findViewById(R.id.itemCountTotal);
		itemCountTotal.setText(" / " + currentRoom.getContentList().size());

		itemCount = (TextView) inv.findViewById(R.id.itemCount);
		itemCount.setText("0");

		adapter = new ItemAdapter(getApplicationContext(), R.layout.room_inventory_item, currentRoom.getContentList());

		gridView = (GridView) inv.findViewById(R.id.gridview);
		gridView.setAdapter(adapter);
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int i, long arg3) {
				Item item = currentRoom.getContentList().get(i);
				if (item.isInStock()) {
					item.removeFromStock();
				} else {
					item.putInStock();
				}
				updateInStockItemCount();
				adapter.notifyDataSetChanged();

				return true;
			}

		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {
				Item item = currentRoom.getContentList().get(i);
				createItemDetailsDialog(item);
			}

		});

		qrParser = new Parser();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.room_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			launchCamera();
			break;
		case R.id.filter:
			if (viewDialog == null) {
				createViewDialog();
			} else {
				viewDialog.show();
			}
			break;
		case R.id.reset:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("Reset");
			builder1.setMessage("All progress will be lost.\nDo you really want to reset?");
			builder1.setNegativeButton("No", null);
			builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					resetRoom();
				}

			});

			resetDialog = builder1.create();
			resetDialog.show();
			break;
		case R.id.finish:
			//Intent intent1 = new Intent("sk.tuke.ursus.activities.RESULT");
			Intent intent1 = new Intent(getApplicationContext(),Result.class);
			intent1.putExtra("total", (float) currentRoom.getContentList().size());
			intent1.putExtra("missing", (float) currentRoom.getMissingCount());

			startActivity(intent1);
			break;
		}
		return true;
	}

	protected void resetRoom() {
		Toast.makeText(getApplicationContext(), "Reseting", Toast.LENGTH_SHORT).show();
		currentRoom.reset();
		itemCount.setText("0");
		adapter.notifyDataSetChanged();
	}

	private void createViewDialog() {
		final CharSequence[] items = { "All", "Missing", "In stock" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("View");
		builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				Toast.makeText(getApplicationContext(), "Viewing '" + items[item] + "'", Toast.LENGTH_SHORT).show();
				/*
				 * if (item == 0) adapter = new
				 * ItemAdapter(getApplicationContext(),
				 * R.layout.room_inventory_item, currentRoom
				 * .getContentList(true)); else if (item == 1) adapter = new
				 * ItemAdapter(getApplicationContext(),
				 * R.layout.room_inventory_item, currentRoom
				 * .getContentList(false)); else if (item == 2) adapter = new
				 * ItemAdapter(getApplicationContext(),
				 * R.layout.room_inventory_item, currentRoom .getContentList());
				 */
				if (item == 0) {
					adapter.setMode(0);
				} else if (item == 1) {
					adapter.setMode(1);
				} else if (item == 2) {
					adapter.setMode(2);
				}
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				// listView.setAdapter(adapter);
				// gridView.setAdapter(adapter);
			}
		});

		viewDialog = builder.create();
		viewDialog.show();

	}

	private void launchCamera() {

		Toast.makeText(this, "Launching camera ...", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);

	}

	@Override
	public void onClick(View v) {
		// if (v.getId() == R.id.camera_button) {
		// launchCamera();
		// }
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		viewPager.setCurrentItem(0);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {

				String input = intent.getStringExtra("SCAN_RESULT");

				if (qrParser.parserQR(input)) {
					searchList(qrParser.getItemID());
				} else {
					Toast.makeText(getApplicationContext(), "Invalid QR", Toast.LENGTH_SHORT).show();
				}

			} else if (resultCode == RESULT_CANCELED) {
				// searchList(129);

				
		/*		  if (qrParser.parserQR("EVID.C.: 90240786 Stare C.: 1236/08 Opis: CPU Intel SR2500ALLXR Kusov: 1 Miestnost: 511A")) { 
					  searchList(qrParser.getItemID()); 
					  }
				  else { Toast.makeText(getApplicationContext(), "INVALID QR",
				  Toast.LENGTH_SHORT).show(); }*/
				 

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Canceled");
				builder.setMessage("Canceled reading QR code");
				builder.setNeutralButton("Dismiss", null);
				builder.create().show();

			}
		}
	}

	private void searchList(String searchResultID) {
		int position = 0;
		for (Item i : currentRoom.getContentList()) {
			if (i.getID().equals(searchResultID)) {
				if (!(i.isInStock())) {
					// vibrator.vibrate(new long[] { 10, 30 }, 2);
					createItemFoundDialog(i.getDesc());
					i.putInStock();
					this.position = position;
					// gridView.smoothScrollToPosition(position);
					// System.out.println("POSITION " + position);
					// gridView.getChildAt(position).startAnimation(popup);
					// adapter.notifyDataSetChanged();// refresh listView
					updateInStockItemCount();

					return;
				} else {
					createItemAlreadyInStockDialog(i.getDesc());
					return;
				}
			}
			position++;
		}
		createItemNotFoundDialog();

	}

	/*
	 * private void searchList(int searchResultID) { int position = 0; for (Item
	 * i : currentRoom.getContentList()) { if (i.getId() == searchResultID) { if
	 * (!(i.isInStock())) { vibrator.vibrate(new long[] { 10, 30, }, 2);
	 * createItemFoundDialog(i.getName()); i.putInStock(); this.position =
	 * position; // gridView.smoothScrollToPosition(position); //
	 * System.out.println("POSITION " + position); //
	 * gridView.getChildAt(position).startAnimation(popup); //
	 * adapter.notifyDataSetChanged();// refresh listView
	 * updateInStockItemCount();
	 * 
	 * return; } else { createItemAlreadyInStockDialog(i.getName()); return; } }
	 * position++; }
	 * 
	 * if (notFoundDialog == null) { createItemNotFoundDialog(); } else {
	 * notFoundDialog.show(); }
	 * 
	 * }
	 */

	private void createItemDetailsDialog(Item i) {
		String message = "ID: " + i.getID() + "\nOld ID: " + i.getOldID() + "\nDesc: " + i.getDesc() + "\nQuantity: " + i.getQuantity() + "\nRoom: " + i.getRoom();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Item details");
		builder.setMessage(message);
		builder.setNeutralButton("Dismiss", null);
		builder.create().show();
	}

	private void createItemFoundDialog(String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Success");
		builder.setMessage("Item '" + name + "' was found successfully !");
		builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				gridView.smoothScrollToPosition(position);
				adapter.notifyDataSetChanged();// refresh listView

			}
		});
		builder.create().show();
	}

	private void createItemNotFoundDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Failed");
		builder.setMessage("Item not found");
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				launchCamera();
			}

		});
		builder.setNegativeButton("Dismiss", null);
		notFoundDialog = builder.create();
		notFoundDialog.show();
	}

	private void createItemAlreadyInStockDialog(String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning");
		builder.setMessage("Item '" + name + "' is already in stock");
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				launchCamera();
			}

		});
		builder.setNegativeButton("Dismiss", null);
		notFoundDialog = builder.create();
		notFoundDialog.show();
	}

	private void updateInStockItemCount() {
		itemCount.setText(String.valueOf(currentRoom.getInStockCounter()));
	}

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) { if
	 * (event.getAction() == MotionEvent.ACTION_DOWN) { x0 = event.getRawX(); y0
	 * = event.getRawY(); } if (event.getAction() == MotionEvent.ACTION_UP) {
	 * vibrator.vibrate(20); x1 = event.getRawX(); y1 = event.getRawY(); float
	 * dx = Math.abs(x1 - x0); Log.i("DIFFERENCE", String.valueOf(dx)); if (dx >
	 * 150) { Log.i("SWIPE", "RECOGNIZED"); launchCamera(); return true; }
	 * 
	 * } return false; }
	 */	

}
