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

	private ImageButton startButton, settingsButton, aboutButton;
	private Vibrator vibrator;
	private ProgressDialog progressDialog;
	private boolean firstRun = true;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main_menu);

		// createLoginDialog();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		startButton = (ImageButton) findViewById(R.id.startButton);
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		aboutButton = (ImageButton) findViewById(R.id.aboutButton);

		startButton.setOnTouchListener(this);
		settingsButton.setOnTouchListener(this);
		aboutButton.setOnTouchListener(this);

		app = (MyApplication) getApplication();
		loadAppData();

	}

	private void loadAppData() {

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = openFileInput(MyApplication.FILENAME);
			ois = new ObjectInputStream(fis);

			MyApplication loadedApp = (MyApplication) ois.readObject();

			app.setEmailAddresses(loadedApp.getEmailAddresses());
			app.setXmlURL(loadedApp.getXmlURL());
			app.setPhpURL(loadedApp.getPhpURL());
			app.setReadyToStart(loadedApp.isReadyToStart());

			fis.close();
			ois.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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
					
					//debug
					//app.setReadyToStart(true);
					
					if (app.isReadyToStart()) {
						startActivity(new Intent(getApplicationContext(), RoomSelection.class));
					} else {
						appDataNotSetupDialog();
					}
					break;
				case R.id.settingsButton:
					startActivity(new Intent(getApplicationContext(), Settings.class));
					break;
				case R.id.aboutButton:
					startActivity(new Intent(getApplicationContext(), About.class));
					break;
			}
		}
		return false;
	}

	private void appDataNotSetupDialog() {
		Builder builder = new Builder(this);
		builder.setMessage("There are no email recipients, or URLs set up. Please, set this up in Settings first.");
		builder.setTitle("Warning");
		builder.setNeutralButton("Dismiss", null);
		builder.create().show();

	}

	@Override
	protected void onPause() {
		super.onPause();
		/*
		 * if (progressDialog != null) { progressDialog.dismiss(); }
		 */
	}

	// asi nebude treba
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
