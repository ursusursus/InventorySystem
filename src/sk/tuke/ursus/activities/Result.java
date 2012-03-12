package sk.tuke.ursus.activities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sk.tuke.ursus.MyApplication;
import sk.tuke.ursus.R;
import sk.tuke.ursus.ResultsReport;
import sk.tuke.ursus.adapters.ViewPagerAdapter;
import sk.tuke.ursus.customViews.PieChartView;
import sk.tuke.ursus.entities.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Result extends Activity implements OnTouchListener {

	private float percentage;
	private TextView textView;
	private ResultsReport report;
	private int size = 40;
	private Animation shrink;
	private Animation enlarge;
	private Vibrator vibrator;
	private Button exitButton;
	private Room currentRoom;
	private ViewPager viewPager;
	private String responseURL;
	private Button serverButton;
	private Button storageButton;
	private Button viewButton;
	private boolean serverButtonClicked = false;
	private MyApplication app;
	private Button notifyButton;
	private LinearLayout res;
	private LinearLayout stat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// LinearLayout l = (LinearLayout) View.inflate(this, R.layout.result,
		// null);
		// l.addView(new PieChartView(this, percentage));
		// setContentView(l);

		this.setContentView(R.layout.finish);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		//shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
		//enlarge = AnimationUtils.loadAnimation(this, R.anim.enlarge);

		/*
		 * textView = (TextView) findViewById(R.id.textView);
		 * textView.setText(percentage + "% / " + (100 - percentage) + "%");
		 */

		initLayouts();
		initViews();


		stat.addView(new PieChartView(this, percentage));

	}

	private void calculatePercentage(float totalCount, float missingCount) {
		percentage = 100 - ((missingCount * 100) / totalCount);
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
				createExitDialog();
				break;
			}
		}
		return false;

	}

	private void viewReportInBrowser() {
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(report.getURLResponse()));
		startActivity(i);
	}

	private void exportToServer() {
		
		try {

			report.exportToServer(app.getPhpURL());
			Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
			Log.i("URL", report.getURLResponse());

			viewButton.setEnabled(true);
			notifyButton.setEnabled(true);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"Upload failed, please make surethe URL\nto .php script is correct", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void createExitDialog() {
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("Exiting to main menu");
		builder.setMessage("Do you really want to quit?");
		builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(getApplicationContext(), MainMenu.class));
			}

		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();

	}

	private void exportToSDCard() {

		if (isSDCardAvailable()) {
			try {

				File path = getExternalFilesDir(null);
				File resultsFile = new File(path, report.getFileName());

				FileWriter writer = new FileWriter(resultsFile);
				writer.write(report.getReport());
				writer.flush();
				writer.close();

				Toast.makeText(this, "Saved successfully to " + resultsFile.toString() + ".html", Toast.LENGTH_LONG)
						.show();
				storageButton.setEnabled(false);
				storageButton.setText("exported to SD-card");
			} catch (IOException e) {
				Toast.makeText(this, "Saving failed", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "SD-Card not available", Toast.LENGTH_SHORT).show();
			isAvailable = false;

		}
		return isAvailable;

	}

	private void notifyViaEmail() {

		report.composeEmailNotification();

		Toast.makeText(this, "Sending results via e-mail", Toast.LENGTH_LONG).show();
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, report.getAddress());
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, report.getSubject());
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, report.getEmailMessage());
		emailIntent.setType("text/plain");

		startActivity(Intent.createChooser(emailIntent, "Send email..."));

	}

	private void initViews() {
		
		app = ((MyApplication) getApplicationContext());
		currentRoom = app.getCurrentRoom();
		ArrayList<String> addressess = app.getEmailAddresses();
		report = new ResultsReport(currentRoom, addressess);

		serverButton = (Button) res.findViewById(R.id.serverButton);
		storageButton = (Button) res.findViewById(R.id.storageButton);
		notifyButton = (Button) res.findViewById(R.id.notifyButton);
		viewButton = (Button) res.findViewById(R.id.viewButton);
		exitButton = (Button) res.findViewById(R.id.exitButton);
		serverButton.setOnTouchListener(this);
		storageButton.setOnTouchListener(this);
		notifyButton.setOnTouchListener(this);
		viewButton.setOnTouchListener(this);
		exitButton.setOnTouchListener(this);
		notifyButton.setEnabled(false);
		viewButton.setEnabled(false);
		
		TextView dateTv = (TextView) stat.findViewById(R.id.dateTv);
		TextView resultTv = (TextView) stat.findViewById(R.id.resultTv);
		
		float totalCount = getIntent().getExtras().getFloat("total");
		float missingCount = getIntent().getExtras().getFloat("missing");

		calculatePercentage(totalCount, missingCount);
		
		dateTv.setText("- results from " + report.getCurrentDate());
		resultTv.setText("- " + (int) missingCount + " of " + (int) totalCount + " were found missing");

	}

	private void initLayouts() {
		ArrayList<LinearLayout> list = new ArrayList<LinearLayout>();
		res = (LinearLayout) View.inflate(this, R.layout.finish_result, null);
		stat = (LinearLayout) View.inflate(this, R.layout.finish_stats, null);
		list.add(res);
		list.add(stat);

		viewPager = (ViewPager) findViewById(R.id.finishPager);
		ViewPagerAdapter pageAdapter = new ViewPagerAdapter(getApplicationContext(), list);
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

		});

	}

}
