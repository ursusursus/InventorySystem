package sk.tuke.ursus.activities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class MainMenu extends Activity implements OnTouchListener {

	private static final int APP_DATA_NOT_SETUP = 0;
	
	private MyApplication app;
	private Vibrator vibrator;
	
	private ImageButton startButton;
	private ImageButton settingsButton;
	private ImageButton aboutButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main_menu);
		
		app = (MyApplication) getApplication();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				
		initViews();
		addListeners();

		loadAppData();
	}

	private void addListeners() {
		startButton.setOnTouchListener(this);
		settingsButton.setOnTouchListener(this);
		aboutButton.setOnTouchListener(this);
	}

	private void initViews() {
		startButton = (ImageButton) findViewById(R.id.startButton);
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		aboutButton = (ImageButton) findViewById(R.id.aboutButton);
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
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			vibrator.vibrate(30);
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));
			
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enlarge));
			
			switch (v.getId()) {
				case R.id.startButton:
					
					if (app.isReadyToStart()) {
						startActivity(new Intent(getApplicationContext(), RoomSelection.class));
					} else {
						showDialog(APP_DATA_NOT_SETUP);
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if(id == APP_DATA_NOT_SETUP) {
			dialog = new AlertDialog.Builder(this)
			.setMessage("There are no email recipients, or URLs set up. Please, set this up in Settings first.")
			.setTitle("Warning")
			.setNeutralButton("Dismiss", null)
			.create();
		}
		return dialog;

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
