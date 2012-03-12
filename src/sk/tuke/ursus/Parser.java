package sk.tuke.ursus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;


public class Parser {

	//private final String URL = "http://brecka.cenkner.eu/source_xml49.xml";
	//private final String URL = "http://vlastimil.brecka.student.cnl.sk/source_test.xml";
	private String source;
	private final String regexRoom = "(<room name=\"(.*?)\">(.*?)</room>)";
	private final String regexItem = "(<item EVID.C.=\"(.*?)\" Stare_C.=\"(.*?)\" Opis=\"(.*?)\" Kusov=\"(.*?)\" Miestnost=\"(.*?)\"/>)";
	private final String regexQR = "(EVID.C.: (........))";

	private ArrayList<Room> roomsList = new ArrayList<Room>();
	private String itemID;

	public Parser() {
	}

	public void downloadXML(String xmlURL) throws IOException {

		StringBuilder sb = new StringBuilder();

		URL url = new URL(xmlURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.connect();
		InputStream is = con.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		this.source = sb.toString();
	}

	public void parseXML() {
		Pattern p = Pattern.compile(regexRoom);
		Pattern pa = Pattern.compile(regexItem);
		Matcher m = p.matcher(source);

		while (m.find()) {
			Room room = new Room(m.group(2));
			roomsList.add(room);
			Matcher ma = pa.matcher(m.group(3));
			while (ma.find()) {
				room.addItem(new Item(ma.group(2), ma.group(3), ma.group(4), ma.group(5), ma.group(6)));
			}
		}
	}

	public boolean parserQR(String input) {
		Pattern p = Pattern.compile(regexQR);
		Matcher m = p.matcher(input);
		if (m.find()) {
			itemID = m.group(2);
			return true;
		} else {
			return false;
		}
	}

	public String getItemID() {
		return itemID;
	}

	public ArrayList<Room> getRoomsList() {
		return roomsList;
	}
}
