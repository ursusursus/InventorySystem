package sk.tuke.ursus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class HTTPConnectionHelper {

	public static String response;
	public static ConnectivityManager cm;

	public static String download() throws IOException {

		if (!isOnline()) {
			throw new IOException("No internet connection");
		} else {

			StringBuilder sb = new StringBuilder();

			URL url = new URL("http://vlastimil.brecka.student.cnl.sk/source.xml");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			InputStream is = con.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			return sb.toString();
		}
	}

	public static void upload(String report) throws IOException {

		if (!isOnline()) {
			throw new IOException("No internet connection");
		} else {

			URL url = new URL("http://vlastimil.brecka.student.cnl.sk/upload_reports.php");
			URLConnection connection = url.openConnection();

			// POST METHOD
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-length", String.valueOf(report.length()));

			OutputStream out = connection.getOutputStream();
			out.write(report.getBytes());
			out.flush();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuilder sb = new StringBuilder();
			String line = null;

			// server response
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			response = sb.toString();

			rd.close();
		}

	}

	public static void setConnectivityManager(ConnectivityManager cm) {
		HTTPConnectionHelper.cm = cm;
	}

	
	private static boolean isOnline() {
		
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
		
	}

}
