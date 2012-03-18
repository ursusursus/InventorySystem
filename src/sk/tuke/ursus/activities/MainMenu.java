package sk.tuke.ursus.activities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Set;

import sk.tuke.ursus.MyApplication;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import sk.tuke.ursus.R;

public class MainMenu extends Activity implements OnTouchListener {

	private ImageButton startButton, settingsButton, helpButton, aboutButton;
	private Vibrator vibrator;
	private ProgressDialog progressDialog;
	final private String FILENAME = "settings.invsys";
	private boolean firstRun = true;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// fullscreen
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
	//	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.main_menu);

		// createLoginDialog();

		startButton = (ImageButton) findViewById(R.id.startButton);
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		helpButton = (ImageButton) findViewById(R.id.helpButton);
		aboutButton = (ImageButton) findViewById(R.id.aboutButton);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		startButton.setOnTouchListener(this);
		settingsButton.setOnTouchListener(this);
		helpButton.setOnTouchListener(this);
		aboutButton.setOnTouchListener(this);

		app = (MyApplication) getApplication();
		loadEmailList();

	}

	private void loadEmailList() {

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = openFileInput(FILENAME);
			ois = new ObjectInputStream(fis);
			
			MyApplication loadedApp = (MyApplication) ois.readObject();

			app.setEmailAddresses(loadedApp.getEmailAddresses());
			app.setXmlURL(loadedApp.getXmlURL());
			app.setPhpURL(loadedApp.getPhpURL());
			app.setReadyToStart(loadedApp.isReadyToStart());

			//Toast.makeText(getApplicationContext(), "Loading successful", Toast.LENGTH_SHORT).show();
			fis.close();
			ois.close();
		} catch (FileNotFoundException e) {
			// Toast.makeText(getApplicationContext(), "FileNotFound",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// Toast.makeText(getApplicationContext(), "Stream corrupt",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			// Toast.makeText(getApplicationContext(), "IO-Exception",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// Toast.makeText(getApplicationContext(), "Class not found",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void writeEmailList() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {

			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(app);
//			Toast.makeText(getApplicationContext(), "Saving successful",Toast.LENGTH_SHORT).show();
			fos.close();
			oos.close();
		} catch (FileNotFoundException e) {
			// Toast.makeText(getApplicationContext(),
			// "FileNotFound",Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			// Toast.makeText(getApplicationContext(), "IO Exception",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (firstRun) {
			firstRun = false;
		} else {
			writeEmailList();
		}
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			vibrator.vibrate(30);
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enlarge));
			switch (v.getId()) {
			case R.id.startButton:
				if (app.isReadyToStart()) {
					//if (firstRun) {
						//progressDialog = ProgressDialog.show(this, "", "Loading content from server", true);
						Log.i("url is",((MyApplication)getApplication()).getXmlURL());
					//}
					//startActivity(new Intent("sk.tuke.ursus.activities.ROOMSELECTION"));
						startActivity(new Intent(getApplicationContext(), RoomSelection.class));
				} else {
					createNoEmailsSetupDialog();
				}
				break;
			case R.id.settingsButton:
				//startActivity(new Intent("sk.tuke.ursus.activities.SETTINGS"));
				startActivity(new Intent(getApplicationContext(),Settings.class));
				break;
			}
		}
		return false;
	}

	private void createNoEmailsSetupDialog() {
		Builder builder = new Builder(this);
		builder.setMessage("There are no email recipients, or URLs set up.\nPlease, set this up in Settings first.");
		builder.setTitle("Warning");
		builder.setNeutralButton("Dismiss", null);
		builder.create().show();

	}

	@Override
	protected void onPause() {
		super.onPause();
	/*	if (progressDialog != null) {
			progressDialog.dismiss();
		}*/
	}

	//asi nebude treba
	private void createLoginDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.login_dialog);
		dialog.setTitle("Please log in to continue");

		final EditText username = (EditText) dialog.findViewById(R.id.usernameEdit);
		final EditText passwd = (EditText) dialog.findViewById(R.id.passwordEdit);
		Button loginButton = (Button) dialog.findViewById(R.id.loginButton);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		final TextView message = (TextView) dialog.findViewById(R.id.messageTextView);

		// cancelButton.setOnClickListener(this);
		// loginButton.setOnClickListener(this);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((username.getText().toString().equals("admin")) && (passwd.getText().toString().equals("admin"))) {
					dialog.cancel();
					Toast.makeText(getApplicationContext(), "Access granted !", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Access denied !", Toast.LENGTH_LONG).show();
					message.setText("Incorrect username or password !");
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});
		dialog.show();

	}

}
