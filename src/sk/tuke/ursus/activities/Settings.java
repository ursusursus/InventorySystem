package sk.tuke.ursus.activities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.customViews.ViewPagerIndicator;

import android.R.xml;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import sk.tuke.ursus.R;

public class Settings extends Activity implements OnTouchListener {
	private ArrayList<String> emailAddresses;
	protected int selectedItemIndex;
	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private TextView tv;
	private AlertDialog addItemDialog;
	private Pattern pattern;
	private String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private boolean itemSelected = false;
	private Button xmlButton;
	private Button phpButton;
	private Vibrator vibrator;
	private MyApplication app;
	private ViewPagerIndicator indicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		app = ((MyApplication) getApplication());
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		ArrayList<LinearLayout> pagesList = new ArrayList<LinearLayout>();

		LinearLayout emails = (LinearLayout) View.inflate(getApplicationContext(), R.layout.settings_recipients, null);
		LinearLayout general = (LinearLayout) View.inflate(getApplicationContext(), R.layout.settings_general, null);

		pagesList.add(general);
		pagesList.add(emails);

		ViewPager viewPager = (ViewPager) findViewById(R.id.settingsPager);
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), pagesList);
		viewPager.setAdapter(viewPagerAdapter);

		indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);

		emailAddresses = app.getEmailAddresses();
		if (emailAddresses == null) {
			emailAddresses = new ArrayList<String>();
		}

		listView = (ListView) emails.findViewById(R.id.emailListView);
		listView.setDivider(null);
		listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.settings_recipients_item,
				emailAddresses);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				itemSelected = true;
				selectedItemIndex = index;
				openOptionsMenu();
			}
		});

		tv = (TextView) emails.findViewById(R.id.noEmailsTextView);
		tv.setText("- no emails added yet -");
		if (emailAddresses.size() != 0) {
			tv.setVisibility(TextView.GONE);
		}

		pattern = Pattern.compile(regex);

		xmlButton = (Button) general.findViewById(R.id.xmlButton);
		phpButton = (Button) general.findViewById(R.id.phpButton);
		xmlButton.setOnTouchListener(this);
		phpButton.setOnTouchListener(this);

	}

	private void updateListView() {
		if (emailAddresses.size() == 0) {
			if (tv.getVisibility() == TextView.GONE) {
				tv.setVisibility(TextView.VISIBLE);
			}
		} else {
			tv.setVisibility(TextView.GONE);
		}
		itemSelected = false;
		Collections.sort(emailAddresses);
		listAdapter.notifyDataSetChanged();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.settings_email_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add) {
			createAddItemDialog();
		} else if (item.getItemId() == R.id.remove) {
			createRemoveItemDialog(selectedItemIndex);
		} else if (item.getItemId() == R.id.edit) {
			createEditItemDialog(selectedItemIndex);
		}
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		itemSelected = false;
	}

	private void createEditItemDialog(final int index) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Edit e-mail");
		final EditText editText = new EditText(this);
		editText.setText(emailAddresses.get(index));
		builder.setView(editText);
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String address = editText.getText().toString();
				if (isValidEmailAddress(address)) {
					emailAddresses.remove(index);
					emailAddresses.add(address);
					app.setEmailAddresses(emailAddresses);
					updateListView();
				} else {
					Toast.makeText(getApplicationContext(), "Invalid address.", Toast.LENGTH_SHORT).show();
				}
			}

		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();

	}

	protected boolean isValidEmailAddress(String input) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	private void createAddItemDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add e-mail");
		final EditText editText = new EditText(this);
		builder.setView(editText);
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String address = editText.getText().toString();
				if (isValidEmailAddress(address)) {
					emailAddresses.add(editText.getText().toString());
					app.setEmailAddresses(emailAddresses);
					updateListView();
				} else {
					Toast.makeText(getApplicationContext(), "Invalid address", Toast.LENGTH_SHORT).show();
				}
			}

		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();
	}

	private void createRemoveItemDialog(final int index) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remove e-mail");
		builder.setMessage("Do you really want to remove?");
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				emailAddresses.remove(index);
				app.setEmailAddresses(emailAddresses);
				updateListView();
			}

		});
		builder.setNegativeButton("Cancel", null);
		addItemDialog = builder.create();
		addItemDialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if ((emailAddresses.size() > 0) && (app.getXmlURL() != null) && (app.getPhpURL() != null)) {
			Toast.makeText(getApplicationContext(), "SAVING", Toast.LENGTH_SHORT).show();
			saveAppData();
		} else {
			Toast.makeText(getApplicationContext(), "NOT SAVING", Toast.LENGTH_SHORT).show();
			app.setReadyToStart(false);
		}
	}

	private void saveAppData() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {

			fos = openFileOutput(MyApplication.FILENAME, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(app);

			fos.close();
			oos.close();

			app.setReadyToStart(true);
			Toast.makeText(getApplicationContext(), "Saving successful", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (itemSelected) {
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(true);
			menu.getItem(2).setEnabled(true);
		} else {
			menu.getItem(0).setEnabled(true);
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setEnabled(false);
		}

		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			vibrator.vibrate(30);
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enlarge));
			if (v.getId() == R.id.xmlButton) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Set URL to .xml file");
				final EditText edit = new EditText(this);

				String url = app.getXmlURL();

				if (url != null) {
					edit.setText(url);
				}
				builder.setView(edit);
				builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String url = edit.getText().toString();
						if (!url.equals(""))
							app.setXmlURL(url);
					}
				});
				builder.setNegativeButton("Cancel", null);
				builder.create().show();
			} else if (v.getId() == R.id.phpButton) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Set URL to .php script");
				final EditText edit = new EditText(this);

				String url = app.getPhpURL();

				if (url != null) {
					edit.setText(url);
				}
				builder.setView(edit);
				builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String url = edit.getText().toString();
						if (!url.equals(""))
							app.setPhpURL(url);
					}
				});
				builder.setNegativeButton("Cancel", null);
				builder.create().show();
			}
		}
		return false;
	}
}
