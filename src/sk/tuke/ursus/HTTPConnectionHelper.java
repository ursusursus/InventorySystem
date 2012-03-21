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

import android.content.Context;
import android.net.ConnectivityManager;

public class HTTPConnectionHelper {
	
	public static String response;

	public static String download() throws Exception {
		
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
	
	public static void upload(String report) throws IOException {

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
