package sk.tuke.ursus.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.R;
import sk.tuke.ursus.ResultsReport;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.customViews.ViewPagerIndicator;
import sk.tuke.ursus.entities.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Result extends Activity implements OnTouchListener {

	private static final int CONNECTION_FAILED = 0;
	private static final int WRONG_RESPONSE = 1;
	private static final int EXIT_DIALOG = 2;

	private MyApplication app;
	private Vibrator vibrator;
	
	private Button serverButton;
	private Button storageButton;
	private Button notifyButton;
	private Button viewButton;
	private Button exitButton;
	
	private LinearLayout res;
	private LinearLayout stat;
	
	private ViewPager viewPager;
	private ViewPagerIndicator indicator;
	private ProgressDialog dialog;
	
	private ResultsReport resultsReport;

	private String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((MyApplication) getApplicationContext());
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		this.setContentView(R.layout.finish);

		resultsReport = new ResultsReport(app.getCurrentRoom(), app.getEmailAddresses());

		initViews();
		addListeners();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			vibrator.vibrate(30);
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enlarge));

			switch (v.getId()) {
			case R.id.serverButton:
				exportToServer();
				break;
			case R.id.storageButton:
				exportToSDCard();
				break;
			case R.id.notifyButton:
				notifyViaEmail();
				break;
			case R.id.viewButton:
				viewReportInBrowser();
				break;
			case R.id.exitButton:
				showDialog(EXIT_DIALOG);
				break;
			}
		}
		return false;

	}

	private void viewReportInBrowser() {

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(response));
		startActivity(i);
	}

	private void exportToServer() {

		dialog = new ProgressDialog(this);
		dialog.setTitle("Please,wait");
		dialog.setMessage("Uploading...");
		UploadTask task = new UploadTask();
		task.execute(app.getPhpURL());

	}

	private void exportToSDCard() {

		if (isSDCardAvailable()) {
			try {

				File path = getExternalFilesDir(null);
				File resultsFile = new File(path, resultsReport.getFileName());

				FileWriter writer = new FileWriter(resultsFile);
				writer.write(resultsReport.getReport());
				writer.flush();
				writer.close();

				Toast.makeText(this, "Saved successfully to " + resultsFile.toString() + ".html", Toast.LENGTH_LONG)
						.show();
				storageButton.setEnabled(false);
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Saving failed.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean isSDCardAvailable() {

		boolean isAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			Log.i("MEDIA", "MOUNTED");

			isAvailable = true;

		} else {
			Log.i("MEDIA", "FAIL");
			Toast.makeText(this, "SD-Card not available.", Toast.LENGTH_SHORT).show();
			isAvailable = false;

		}
		return isAvailable;

	}

	private void notifyViaEmail() {

		resultsReport.composeEmailNotification(response);

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, resultsReport.getAddress());
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, resultsReport.getSubject());
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, resultsReport.getEmailMessage());
		emailIntent.setType("text/plain");

		startActivity(Intent.createChooser(emailIntent, "Send email..."));

	}

	private void initViews() {

		ArrayList<LinearLayout> list = new ArrayList<LinearLayout>();
		res = (LinearLayout) View.inflate(this, R.layout.finish_result, null);
		stat = (LinearLayout) View.inflate(this, R.layout.finish_stats, null);
		list.add(res);
		list.add(stat);

		viewPager = (ViewPager) findViewById(R.id.finishPager);
		ViewPagerAdapter pageAdapter = new ViewPagerAdapter(getApplicationContext(), list);
		viewPager.setAdapter(pageAdapter);

		indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);

		serverButton = (Button) res.findViewById(R.id.serverButton);
		storageButton = (Button) res.findViewById(R.id.storageButton);
		notifyButton = (Button) res.findViewById(R.id.notifyButton);
		viewButton = (Button) res.findViewById(R.id.viewButton);
		exitButton = (Button) res.findViewById(R.id.exitButton);

		TextView dateTv = (TextView) stat.findViewById(R.id.dateTv);
		TextView resultTv = (TextView) stat.findViewById(R.id.resultTv);

		Room currentRoom = app.getCurrentRoom();
		dateTv.setText("- results from " + resultsReport.getCurrentDate());
		resultTv.setText("- " + currentRoom.getMissingCount() + " of " + currentRoom.getContentList().size() + " were found missing.");

	}

	private void addListeners() {
		serverButton.setOnTouchListener(this);
		storageButton.setOnTouchListener(this);
		notifyButton.setOnTouchListener(this);
		viewButton.setOnTouchListener(this);
		exitButton.setOnTouchListener(this);

		notifyButton.setEnabled(false);
		viewButton.setEnabled(false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case CONNECTION_FAILED:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Connection failed")
					.setMessage(
							"Please check your internet connection and make sure the URL to .php script is correct.")
					.setNeutralButton("Dismiss", null).create();
			break;
		case WRONG_RESPONSE:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Wrong response")
					.setMessage(
							"Php script is not a valid Inventory System exporting script. Please check source file for errors or provide correct script.")
					.setNeutralButton("Dismiss", null).create();
			break;
		case EXIT_DIALOG:
			dialog = new AlertDialog.Builder(this).setTitle("Exiting to main menu")
					.setMessage("Do you really want to quit?")
					.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(getApplicationContext(), MainMenu.class));
						}

					}).setNegativeButton("Cancel", null).create();
			break;
		}
		return dialog;
	}

	
	private class UploadTask extends AsyncTask<String, Void, String> {

		private Exception e;

		@Override
		protected String doInBackground(String... urls) {

			String response = null;

			for (String source : urls) {
				try {
					String r = resultsReport.getReport();

					URL url = new URL(source);
					URLConnection connection = url.openConnection();

					// POST METHOD
					connection.setDoInput(true);
					connection.setDoOutput(true);

					connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
					connection.setRequestProperty("Content-length", String.valueOf(r.length()));

					OutputStream out = connection.getOutputStream();
					out.write(r.getBytes());
					out.flush();

					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));

					StringBuilder sb = new StringBuilder();
					String line = null;

					while ((line = rd.readLine()) != null) {
						sb.append(line);
					}

					rd.close();

					String tmp = sb.toString();

					if (!tmp.startsWith("inventorysystem:")) {
						throw new SAXException("Wrong response.");
					} else {
						response = tmp.substring("inventorysystem:".length());
					}
				} catch (Exception e) {
					this.e = e;
				}
			}
			return response;
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (e == null) {
				response = result;
				
				serverButton.setEnabled(false);
				viewButton.setEnabled(true);
				notifyButton.setEnabled(true);
				
				Toast.makeText(getApplicationContext(), "Upload successful.", Toast.LENGTH_LONG).show();
			} else {
				if (e instanceof SAXException) {
					showDialog(WRONG_RESPONSE);
				} else if (e instanceof IOException) {
					showDialog(CONNECTION_FAILED);
				}
			}
		}

	}

}
