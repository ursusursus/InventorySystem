package sk.tuke.ursus.activities;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.R;
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.adapters.ItemAdapter;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.customViews.ViewPagerIndicator;
import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RoomInventory extends Activity {

	private static final int VIEW_ALL = 0;
	private static final int VIEW_MISSING = 1;
	private static final int VIEW_IN_STOCK = 2;

	private static final int ITEM_DETAILS = 0;
	private static final int ITEM_FOUND = 1;
	private static final int ITEM_NOT_FOUND = 2;
	private static final int ITEM_ALREADY_IN_STOCK = 3;
	private static final int QR_CODE_FORMAT_NOT_RECOGNIZED = 4;
	private static final int READING_QR_CODE_CANCELED = 5;
	private static final int RESET_ROOM = 6;
	private static final int VIEW_FILTER = 7;

	private MyApplication app;
	private Vibrator vibrator;
	
	private Room currentRoom;
	private Item currentItem;
	private int currentItemIndex;
	
	private ViewPager viewPager;
	private TextView itemCountTextView;
	
	private GridView gridView;
	private ItemAdapter adapter;
	
	private Parser parser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.room);
		
		app = ((MyApplication) getApplicationContext());
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		currentRoom = app.getCurrentRoom();
		parser = new Parser();

		initViews();
		addListeners();

	}

	private void updateUI() {
		updateInStockItemCount();
		adapter.notifyDataSetChanged();
	}

	private void updateInStockItemCount() {
		itemCountTextView.setText(String.valueOf(currentRoom.getInStockCount()));
	}

	/*
	 * private void updateView(int index) { int firstVisible =
	 * gridView.getFirstVisiblePosition(); View v = gridView.getChildAt(index -
	 * firstVisible);
	 * 
	 * Animation a = AnimationUtils.loadAnimation(getApplicationContext(),
	 * R.anim.popup); //v.startAnimation(a);
	 * 
	 * v.setBackgroundColor(0xFF8904B1);
	 * 
	 * Log.i("INDEX IS",String.valueOf(firstVisible) + " - " +
	 * String.valueOf(index)); }
	 */

	@Override
	protected void onResume() {
		super.onResume();
		updateInStockItemCount();
	}

	protected void resetRoom() {
		currentRoom.reset();
		adapter.setFilteringMode(VIEW_ALL);
		updateUI();

		Toast.makeText(getApplicationContext(), "Room reset.", Toast.LENGTH_SHORT).show();
	}

	private void launchCamera() {

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			try {
				String input = intent.getStringExtra("SCAN_RESULT");
				String itemID = parser.parseQRCode(input);
				searchList(itemID);
			} catch (SAXException e) {
				showDialog(QR_CODE_FORMAT_NOT_RECOGNIZED);
				e.printStackTrace();
			}

		} else if (resultCode == RESULT_CANCELED) {
			showDialog(READING_QR_CODE_CANCELED);
		}
		viewPager.setCurrentItem(0);
	}

	private void searchList(String searchResultID) {
		int index = 0;
		for (Item i : currentRoom.getContentList()) {
			if (i.getID().equals(searchResultID)) {
				if (!(i.isInStock())) {
					i.putInStock();
					currentItem = i;
					currentItemIndex = index;
					showDialog(ITEM_FOUND);
					return;
				} else {
					showDialog(ITEM_ALREADY_IN_STOCK);
					return;
				}
			}
			index++;
		}
		showDialog(ITEM_NOT_FOUND);

	}
	
	private void addListeners() {
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
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int i, long arg3) {
				Item item = currentRoom.getContentList().get(i);
				if (item.isInStock()) {
					item.removeFromStock();
				} else {
					item.putInStock();
				}
				// updateView(i);
				updateUI();

				return true;
			}
		});
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i, long arg3) {
				vibrator.vibrate(30);
				currentItem = currentRoom.getContentList().get(i);
				showDialog(ITEM_DETAILS);
			}

		});		
	}

	private void initViews() {
		ArrayList<LinearLayout> list = new ArrayList<LinearLayout>();
		LinearLayout inv = (LinearLayout) View.inflate(this, R.layout.room_inventory, null);
		LinearLayout cam = (LinearLayout) View.inflate(this, R.layout.room_camera, null);
		list.add(inv);
		list.add(cam);
		
		viewPager = (ViewPager) findViewById(R.id.roomPager);
		ViewPagerAdapter pageAdapter = new ViewPagerAdapter(getApplicationContext(), list);
		viewPager.setAdapter(pageAdapter);

		ViewPagerIndicator indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);
		
		adapter = new ItemAdapter(getApplicationContext(), R.layout.room_inventory_item, currentRoom.getContentList());

		gridView = (GridView) inv.findViewById(R.id.gridview);
		gridView.setAdapter(adapter);
		
		TextView roomNameTextView = (TextView) inv.findViewById(R.id.roomName);
		roomNameTextView.setText(currentRoom.getName());

		TextView itemCountTotalTextView = (TextView) inv.findViewById(R.id.itemCountTotal);
		itemCountTotalTextView.setText(" / " + currentRoom.getContentList().size());

		itemCountTextView = (TextView) inv.findViewById(R.id.itemCount);
		itemCountTextView.setText("0");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.roominventory_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			launchCamera();
			break;
		case R.id.view:
			showDialog(VIEW_FILTER);
			break;
		case R.id.reset:
			showDialog(RESET_ROOM);
			break;
		case R.id.finish:
			startActivity(new Intent(getApplicationContext(), Result.class));
			break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case ITEM_DETAILS:
			dialog = new AlertDialog.Builder(this).setTitle("Item details").setMessage("")
					.setNeutralButton("Dismiss", null).create();
			break;

		case ITEM_FOUND:
			dialog = new AlertDialog.Builder(this).setTitle("Success").setMessage("")
					.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							gridView.smoothScrollToPosition(currentItemIndex);
							updateUI();
						}
					}).create();
			break;

		case ITEM_NOT_FOUND:
			dialog = new AlertDialog.Builder(this).setTitle("Failed").setMessage("Item not found in this room.")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							launchCamera();
						}

					}).setNegativeButton("Dismiss", null).create();
			break;

		case ITEM_ALREADY_IN_STOCK:
			dialog = new AlertDialog.Builder(this).setTitle("Warning").setMessage("")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							launchCamera();
						}

					}).setNegativeButton("Dismiss", null).create();
			break;

		case QR_CODE_FORMAT_NOT_RECOGNIZED:
			dialog = new AlertDialog.Builder(this).setTitle("Failed")
					.setMessage("QR code content isn't in correct Inventory System format.")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							launchCamera();
						}

					}).setNegativeButton("Dismiss", null).create();
			break;

		case READING_QR_CODE_CANCELED:
			dialog = new AlertDialog.Builder(this).setTitle("Canceled").setMessage("Canceled reading QR code")
					.setNeutralButton("Dismiss", null).create();
			break;

		case RESET_ROOM:
			dialog = new AlertDialog.Builder(this).setTitle("Reset")
					.setMessage("All progress will be lost.\nDo you really want to reset?")
					.setNegativeButton("No", null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							resetRoom();
						}

					}).create();
			break;

		case VIEW_FILTER:
			final CharSequence[] items = { "All", "Missing", "In stock" };
			dialog = new AlertDialog.Builder(this).setTitle("View")
					.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int item) {

							if (item == 0) {
								adapter.setFilteringMode(VIEW_ALL);
							} else if (item == 1) {
								adapter.setFilteringMode(VIEW_MISSING);
							} else if (item == 2) {
								adapter.setFilteringMode(VIEW_IN_STOCK);
							}

							adapter.notifyDataSetChanged();
							dialog.dismiss();
							Toast.makeText(getApplicationContext(), "Viewing '" + items[item] + "'.",
									Toast.LENGTH_SHORT).show();
						}
					}).create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case ITEM_DETAILS:
			((AlertDialog) dialog).setMessage("ID: " + currentItem.getID() + "\nOld ID: " + currentItem.getOldID()
					+ "\nDesc: " + currentItem.getDesc() + "\nQuantity: " + currentItem.getQuantity() + "\nRoom: "
					+ currentItem.getRoom());
			break;
		case ITEM_FOUND:
			((AlertDialog) dialog).setMessage("Item '" + currentItem.getDesc() + "' was found successfully!");
			break;
		case ITEM_ALREADY_IN_STOCK:
			((AlertDialog) dialog).setMessage("Item '" + currentItem.getDesc() + "' was already scanned.");
			break;
		}
	}

}
