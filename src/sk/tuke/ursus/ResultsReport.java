package sk.tuke.ursus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.tuke.ursus.entities.Item;
import sk.tuke.ursus.entities.Room;

/**
 * Vysledna sprava vysledkov inventury
 * @author Vlastimil Brecka
 *
 */
public class ResultsReport {

	/**
	 * Zoznam prijmatelov notifikacie
	 */
	private ArrayList<String> recipients;
	
	/**
	 * Obsah miestnosti
	 */
	private List<Item> contentList;
	
	/**
	 * Nazov suboru
	 */
	private String fileName;
	
	/**
	 * Aktualny datum
	 */
	private String currentDate;
	
	/**
	 * Aktualny datum ktory moze byt URL
	 */
	private String printableDate;
	
	/**
	 * Predmet
	 */
	private String subject;
	
	/**
	 * Samotna sprava
	 */
	private String report;
	
	/**
	 * Obsah spravy e-mailovej notifikacie
	 */
	private String emailMessage;
	
	/**
	 * Nazov miestnosti
	 */
	private String roomName;
	
	/**
	 * Pocet vsetkych poloziek
	 */
	private int total;
	
	/**
	 * Pocet chybajucich poloziek
	 */
	private int missing;

	/**
	 * Konstruktor
	 * @param currentRoom Aktualna miestnost
	 * @param recipients Zoznam prijmatelov e-mailovej notifikacie
	 */
	public ResultsReport(Room currentRoom, ArrayList<String> recipients) {
		this.subject = "Results of inventory lookup";
		this.contentList = currentRoom.getContentList();
		this.recipients = recipients;
		this.roomName = currentRoom.getName();
		this.total = currentRoom.getContentList().size();
		this.missing = currentRoom.getMissingCount();
		
		initCurrentDate();
		composeFileName();
		composeHtmlReport();
	}
	
	/**
	 * Inicializuje aktualny datum
	 */
	private void initCurrentDate() {
		currentDate = new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(new Date());
		printableDate = new SimpleDateFormat("dd-MM-yyyy-HH.mm").format(new Date());
	}

	/**
	 * Zostroji nazov suboru
	 */
	private void composeFileName() {
		this.fileName = roomName.toLowerCase().replace(" ", "_") + "_-_" + printableDate;
	}

	/**
	 * Zostroji e-mailovu notifikaciu
	 * @param response Odpoved serva, URL na ulozenu .html spravu
	 */
	public void composeEmailNotification(String response) {
		StringBuilder sb = new StringBuilder();

		sb.append("Dear Sir,\n\ninventory lookup in room '");
		sb.append(roomName);
		sb.append("' was completed on ");
		sb.append(currentDate);
		sb.append(". View results here:\n\n");
		sb.append(response);
		sb.append("\n\nThank you");

		emailMessage = sb.toString();
	}

	/**
	 * Zostroji .html spravu
	 */
	private void composeHtmlReport() {

		StringBuilder sb = new StringBuilder();
		StringBuilder header = new StringBuilder();
		StringBuilder main = new StringBuilder();
		StringBuilder all = new StringBuilder();
		StringBuilder miss = new StringBuilder();
		StringBuilder ins = new StringBuilder();
		StringBuilder footer = new StringBuilder();

		header.append("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"/mystyle.css\"/></head><body>");

		main.append("<div><table><tr class=\"header\"><td rowspan=\"2\"><img src=\"/inv_icon_small.png\"/></td><td>Miestnost</td><td>Vykonane</td><td>Pocet chybajucich</td><td>Zobraz</td></tr><tr><td>");
		main.append(roomName);
		main.append("</td><td>");
		main.append(currentDate);
		main.append("</td><td>");
		main.append(missing);
		main.append(" / ");
		main.append(total);
		main.append("</td><td class=\"menu\"><a href=\"all.html\" target=\"content\">Vsetky</a><br><a href=\"missing.html\" target=\"content\">Chybajuce</a><br><a href=\"instock.html\" target=\"content\">Na sklade</a><br></td></tr></table>");
		main.append("<iframe name=\"content\" src=\"all.html\"><p>Your browser does not support iframes.</p></iframe></div></body></html>");

		all.append("<table class=\"header\"><td>EVID.C.</td><td>Stare.C.</td><td>Opis</td><td>Kusov</td><td>Miestnost</td><td>Na sklade</td></tr>");
		miss.append(all);
		ins.append(all);

		for (Item i : contentList) {
			if (!(i.isInStock())) {
				StringBuilder tmp = new StringBuilder();
				tmp.append("<tr class=\"notinstock\"><td>");
				tmp.append(i.getID());
				tmp.append("</td><td>");
				tmp.append(i.getOldID());
				tmp.append("</td><td>");
				tmp.append(i.getDesc());
				tmp.append("</td><td>");
				tmp.append(i.getQuantity());
				tmp.append("</td><td>");
				tmp.append(i.getRoom());
				tmp.append("</td><td>");
				tmp.append("Nie");
				tmp.append("</td></tr>");

				miss.append(tmp);
				all.append(tmp);

			} else {
				StringBuilder tmp = new StringBuilder();
				tmp.append("<tr class=\"instock\"><td>");
				tmp.append(i.getID());
				tmp.append("</td><td>");
				tmp.append(i.getOldID());
				tmp.append("</td><td>");
				tmp.append(i.getDesc());
				tmp.append("</td><td>");
				tmp.append(i.getQuantity());
				tmp.append("</td><td>");
				tmp.append(i.getRoom());
				tmp.append("</td><td>");
				tmp.append("Ano");
				tmp.append("</td></tr>");

				ins.append(tmp);
				all.append(tmp);
			}
		}

		footer.append("</table></body></html>");

		sb.append("filename=");
		sb.append(fileName);

		sb.append("&main=");
		sb.append(header);
		sb.append(main);

		sb.append("&all=");
		sb.append(header);
		sb.append(all);
		sb.append(footer);

		sb.append("&miss=");
		sb.append(header);
		sb.append(miss);
		sb.append(footer);

		sb.append("&ins=");
		sb.append(header);
		sb.append(ins);
		sb.append(footer);

		this.report = sb.toString();

	}
	
	/**
	 * Vrati e-mailovu notifikaciu
	 * @return E-mailovu notifikaciu
	 */
	public String getEmailMessage() {
		return emailMessage;
	}

	/**
	 * Vrati spravu
	 * @return Sprava
	 */
	public String getReport() {
		return report;
	}

	/**
	 * Vrati nazov suboru
	 * @return Nazov suboru
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Vrati e-mailove adresy
	 * @return E-mailove adresy
	 */
	public String[] getAddress() {
		return recipients.toArray(new String[recipients.size()]);
	}

	/**
	 * Vrati predmet spravy
	 * @return Predmet spravy
	 */
	public String getSubject() {
		return subject;

	}
	
	/**
	 * Vrati datum ktory moze ist do URL
	 * @return datum
	 */
	public String getPrintableDate() {
		return printableDate;
	}

	/**
	 * Vrati aktualny datum
	 * @return Aktualny Datum
	 */
	public String getCurrentDate() {
		return currentDate;
	}

}
