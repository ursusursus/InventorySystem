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
import sk.tuke.ursus.Parser;
import sk.tuke.ursus.R;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.customViews.ViewPagerIndicator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

/**
 * Aktivita nastaveni
 * @author Vlastimil Brecka
 *
 */
public class Settings extends Activity implements OnTouchListener {
	
	/**
	 * Konstanta dialogu pridania e-mailu
	 */
	private static final int ADD_EMAIL = 0;
	
	/**
	 * Konstanta dialogu editacie e-mailu
	 */
	private static final int EDIT_EMAIL = 1;
	
	/**
	 * Konstanta dialogu zmazania e-mailu
	 */
	private static final int REMOVE_EMAIL = 2;
	
	/**
	 * Konstanta dialogu pridania cesty k .xml zdrojovemu suboru
	 */
	private static final int XML_URL = 3;
	
	/**
	 * Konstanta dialogu pridania cesty k .php vystupnemu skriptu,
	 * ktory zapise .html spravu na server
	 */
	private static final int PHP_URL = 4;
	
	
	/**
	 * Premenna aplikacie, drzi globalne premenne 
	 */
	private MyApplication app;
	
	/**
	 * Premenna vibratoru
	 */
	private Vibrator vibrator;
	
	
	/**
	 * Pattern e-mailu
	 */
	private Pattern patternEmail;
	
	/**
	 * Pattern xml URL
	 */
	private Pattern patternXml;
	
	/**
	 * Pattern php URL
	 */
	private Pattern patternPhp;
	
	
	/**
	 * ListView
	 */
	private ListView listView;
	
	/**
	 * TextView
	 */
	private TextView textView;
	
	/**
	 * Tlacidlo pridania .xml URL
	 */
	private Button xmlButton;
	
	/**
	 * Tlacidlo pridania .php URL
	 */
	private Button phpButton;
	
	/**
	 * List e-mailovych adries
	 */
	private ArrayList<String> emailAddresses;
	
	/**
	 * Adapter listu
	 */
	private ArrayAdapter<String> listAdapter;
	
	/**
	 * Index oznacenej polozky v liste
	 */
	private int selectedItemIndex;
	
	/**
	 * Ci je nejaka polozka oznacena
	 */
	private boolean itemSelected = false;
	
	
	/**
	 * ViewPagerIndicator 
	 */
	private ViewPagerIndicator indicator;

	
	/**
	 * Metoda onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		
		app = ((MyApplication) getApplication());
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		emailAddresses = app.getEmailAddresses();
		if (emailAddresses == null) {
			emailAddresses = new ArrayList<String>();
		}
		
		initViews();
		addListeners();
	}

	
	/**
	 * Prida listenery
	 */
	private void addListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				itemSelected = true;
				selectedItemIndex = index;
				openOptionsMenu();
			}
		});
		
		xmlButton.setOnTouchListener(this);
		phpButton.setOnTouchListener(this);
	}

	/**
	 * Inicializuje views
	 */
	private void initViews() {
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

		listView = (ListView) emails.findViewById(R.id.emailListView);
		listView.setDivider(null);
		listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.settings_recipients_item,
				emailAddresses);
		listView.setAdapter(listAdapter);

		textView = (TextView) emails.findViewById(R.id.noEmailsTextView);
		textView.setText("- no emails added yet -");
		if (emailAddresses.size() != 0) {
			textView.setVisibility(TextView.GONE);
		}

		xmlButton = (Button) general.findViewById(R.id.xmlButton);
		phpButton = (Button) general.findViewById(R.id.phpButton);		
	}

	
	/**
	 * Obnovi listView
	 */
	private void updateListView() {
		if (emailAddresses.size() == 0) {
			if (textView.getVisibility() == TextView.GONE) {
				textView.setVisibility(TextView.VISIBLE);
			}
		} else {
			textView.setVisibility(TextView.GONE);
		}
		itemSelected = false;
		Collections.sort(emailAddresses);
		listAdapter.notifyDataSetChanged();
	}

	/**
	 * Skontroluje ci je dany vstup validna e-mailova adresa
	 * @param vstupny string
	 * @return True ak je to korektna e-mailova adresa, False ak nie
	 */
	private boolean isValidEmailAddress(String input) {
		if(patternEmail == null) {
			patternEmail = Pattern.compile(Parser.regexEmail);
		}
		
		Matcher matcher = patternEmail.matcher(input);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Skontroluje ci je dany vstup validna .xml cesta
	 * @param vstupny string
	 * @return True ak je to korektna .xml cesta, False ak nie
	 */
	private boolean isValidXmlUrl(String input) {	
		if(patternXml == null) {
			patternXml = Pattern.compile(Parser.regexXML);
		}
		
		Matcher matcher = patternXml.matcher(input);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Skontroluje ci je dany vstup validna .php cesta
	 * @param vstupny string
	 * @return True ak je to korektna .php cesta, False ak nie
	 */
	private boolean isValidPhpUrl(String input) {		
		if(patternPhp == null) {
			patternPhp = Pattern.compile(Parser.regexPHP);
		}
		
		Matcher matcher = patternPhp.matcher(input);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Metoda onPause
	 */
	@Override
	protected void onPause() {
		super.onPause();

		if ((emailAddresses.size() > 0) && (app.getXmlURL() != null) && (app.getPhpURL() != null)) {
			//Toast.makeText(getApplicationContext(), "SAVING", Toast.LENGTH_SHORT).show();
			saveAppData();
		} else {
			//Toast.makeText(getApplicationContext(), "NOT SAVING", Toast.LENGTH_SHORT).show();
			app.setReadyToStart(false);
		}
	}

	/**
	 * Ulozi objekt aplikacie na disk
	 */
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
			//Toast.makeText(getApplicationContext(), "Saving successful", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda onTouch
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			vibrator.vibrate(30);
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));
			
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enlarge));
			
			if (v.getId() == R.id.xmlButton) {
				showDialog(XML_URL);
			} else if (v.getId() == R.id.phpButton) {
				showDialog(PHP_URL);
			}
		}
		return false;
	}
	
	/**
	 * Vytvori menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.settings_email_menu, menu);
		return true;
	}

	/**
	 * Reaguje na stlacenie menu tlacidla
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add) {
			showDialog(ADD_EMAIL);
		} else if (item.getItemId() == R.id.remove) {
			showDialog(REMOVE_EMAIL);
		} else if (item.getItemId() == R.id.edit) {
			showDialog(EDIT_EMAIL);
		}
		return true;
	}

	/**
	 * Reaguje na zatvorenie menu
	 */
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		itemSelected = false;
	}

	
	/**
	 * Vytvara menu ktore sa dynamicky meni.
	 * Ak je polozka oznacena, je mozne stlacit len editaciu a jej vymazanie,
	 * ak nie je, je mozne pridat novu polozku
	 */
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

	/**
	 * Vytvori dialogy
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		switch(id) {
		case ADD_EMAIL:
			final EditText editText = new EditText(this);
			editText.setText("");
			dialog = new AlertDialog.Builder(this)
					.setTitle("Add e-mail")
					.setView(editText)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String address = editText.getText().toString();
							if (isValidEmailAddress(address)) {
								emailAddresses.add(editText.getText().toString());
								app.setEmailAddresses(emailAddresses);
								updateListView();
							} else {
								Toast.makeText(getApplicationContext(), "Invalid address.", Toast.LENGTH_SHORT).show();
							}
						}

					}).setNegativeButton("Cancel", null)
					.create();
			break;
			
		case EDIT_EMAIL:
			final EditText editText1 = new EditText(this);
			editText1.setText(emailAddresses.get(selectedItemIndex));
			dialog = new AlertDialog.Builder(this)
					.setTitle("Edit e-mail")
					.setView(editText1)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String address = editText1.getText().toString();
							if (isValidEmailAddress(address)) {
								emailAddresses.remove(selectedItemIndex);
								emailAddresses.add(address);
								app.setEmailAddresses(emailAddresses);
								updateListView();
							} else {
								Toast.makeText(getApplicationContext(), "Invalid address.", Toast.LENGTH_SHORT).show();
							}
						} 	

					}).setNegativeButton("Cancel", null)
					.create();
			break;
			
		case REMOVE_EMAIL:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Remove e-mail")
					.setMessage("Do you really want to remove?")
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							emailAddresses.remove(selectedItemIndex);
							app.setEmailAddresses(emailAddresses);
							updateListView();
						}

					}).setNegativeButton("Cancel", null)
					.create();
			break;
			
		case XML_URL:
			final EditText editText2 = new EditText(this);
			String url = app.getXmlURL();
			if (url != null) {
				editText2.setText(url);
			}
			dialog = new AlertDialog.Builder(this)
					.setTitle("Set URL to .xml file")
					.setView(editText2)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String url = editText2.getText().toString();
							if (isValidXmlUrl(url)) {
								app.setXmlURL(url);
							} else {
								Toast.makeText(getApplicationContext(), "Not a valid .xml URL", Toast.LENGTH_SHORT).show();
							}
						}
					}).setNegativeButton("Cancel", null)
					.create();
			break;
			
		case PHP_URL:
			
			final EditText editText3 = new EditText(this);

			String url2 = app.getPhpURL();

			if (url2 != null) {
				editText3.setText(url2);
			}

			dialog = new AlertDialog.Builder(this)
					.setTitle("Set URL to .php script")
					.setView(editText3)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String url2 = editText3.getText().toString();
							if (isValidPhpUrl(url2)) {
								app.setPhpURL(url2);
							} else {
								Toast.makeText(getApplicationContext(), "Not a valid .php URL", Toast.LENGTH_SHORT).show();
							}
						}
					}).setNegativeButton("Cancel", null)
					.create();
			break;
		}
		return dialog;
	}
	
}
