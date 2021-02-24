package ie.matassa.nino.spirale;
import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.util.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.sql.*;
import java.util.*;
import android.widget.*;
import android.os.*;
import java.util.stream.*;


public class CSV {
  private Context context = null;
  private SQLiteDatabase db = null;
  private HashMap<String, Long> hmRegionList = null;
  private HashMap<String, Long> hmCountryList = null;

  public CSV(Context context) {
	this.context = context;
  }

  private List readCSV(String filePath) {
	List rows = new ArrayList<String>();
	String line = null;
	try {
	  BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

	  while ((line = bufferedReader.readLine()) != null) {
		String[] row = line.split(",");
		rows.add(row);
	  }
	  bufferedReader.close();
	} catch (IOException e) {  Log.d("readCSV", e.toString());}
	return rows;
  }

  private boolean populateTableOverview() {
	boolean firstRowRead = false;
	boolean secondRowRead = false;
	String Region = null;
	String Country = null;
	Integer TotalCase = 0;
	Double CasePer_C = 0.0;
	Integer Case7Day = 0;
	//Double Case7DayPer_C = 0.0; // Global
	Integer Case24Hour = 0;
	Integer TotalDeath = 0;
	Double DeathPer_C = 0.0;
	Integer Death7Day = 0;
	//Double Death7DayPer_C = 0.0; // Global
	Integer Death24Hour = 0;
	String Source = null;
	List rows = null;

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvOverviewName;
	rows = readCSV(filePath);
	UIMessage.notificationMessage(context, "Building Overview " + rows.size() + " rows");
	int rowsbuilt = 0;
	try {
	  for (String[] row: rows) {
		// Ignore the first row
		if (!firstRowRead) {
		  firstRowRead = true;
		  continue;
		}
		int index = 0;
		Country = row[index++];
		if (Country.indexOf("\"") > -1)
		  Country += ", " + row[index++];
		Region = row[index++];
		if (Region.equals("Other")) continue;
		TotalCase = Integer.parseInt(row[index++]);
		CasePer_C = Double.parseDouble(row[index++]);
		Case7Day = Integer.parseInt(row[index++]);
		index++; // Case7DayPer_C, but for global record only
		Case24Hour = Integer.parseInt(row[index++]);
		TotalDeath = Integer.parseInt(row[index++]);
		DeathPer_C = Double.parseDouble(row[index++]);
		Death7Day = Integer.parseInt(row[index++]);
		index++; // Death7DayPer_C, but for global record only
		Death24Hour = Integer.parseInt(row[index++]);
		Source = null;
		if (secondRowRead) Source = row[index];
		// 2nd row populate country as Terra
		if (!secondRowRead && firstRowRead) {
		  secondRowRead = true;
		  Source = "N/A";
		  Region = "Terra";
		}
		ContentValues values = new ContentValues();
		values.put("Country", Country);
		values.put("Region", Region);
		Long FK_Country = hmCountryList.get(Country);
		Long FK_Region = hmRegionList.get(Region);
		if (FK_Region != null) {
		  values.put("FK_Country", FK_Country);
		  values.put("FK_Region", FK_Region);
		}
		values.put("TotalCase", TotalCase);
		values.put("CasePer_C", CasePer_C);
		values.put("Case7Day", Case7Day);
		values.put("Case24Hour", Case24Hour);
		values.put("TotalDeath", TotalDeath);
		values.put("DeathPer_C", DeathPer_C);
		values.put("Death7Day", Death7Day);
		values.put("Death24Hour", Death24Hour);
		values.put("Source", Source);
		Long Id = db.insert("Overview", null, values);
		UIMessage.notificationMessage(context, "Building Overview " + rowsbuilt++ + " of " + rows.size() + " built");
	  }
	} catch (NumberFormatException e) {
	  Log.d("populateTableOverview", e.toString());
	  return false;
	}
	return true;
  }

  private boolean populateTableDetails() {
	boolean firstRowRead = false;
	String Date = null;
	String Code = null;
	String Country = null;
	String Region = null;
	Integer NewCase = 0;
	Integer TotalCase = 0;
	Integer NewDeath = 0;
	Integer TotalDeath = 0;
	List rows = null;

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvDetailsName;
	rows = readCSV(filePath);
	UIMessage.notificationMessage(context, "Building Detail " + rows.size() + " rows");
	int rowsbuilt = 0;

	try {
	  for (String[] row: rows) {
		// Ignore the first row
		if (!firstRowRead) {
		  firstRowRead = true;
		  continue;
		}
		int index = 0;
		Date = row[index++];
		Code = row[index++];
		Country = row[index++];
		if (Country.indexOf("\"") > -1)
		  Country += ", " + row[index++];
		Region = row[index++];
		NewCase = Integer.parseInt(row[index++]);
		TotalCase = Integer.parseInt(row[index++]);
		NewDeath = Integer.parseInt(row[index++]);
		TotalDeath = Integer.parseInt(row[index++]);
		ContentValues values = new ContentValues();
		values.put("Date", Date);
		values.put("Code", Code);
		values.put("Country", Country);
		values.put("Region", Region);
		values.put("NewCase", NewCase);
		values.put("TotalCase", TotalCase);
		values.put("NewDeath", NewDeath);
		values.put("TotalDeath", TotalDeath);
		Long FK_Country = hmCountryList.get(Country);
		values.put("FK_Country", FK_Country);
		Long Id = db.insert("Detail", null, values);
		UIMessage.notificationMessage(context, "Building Detail " + rowsbuilt++ + " of " + rows.size() + " built");
	  }
	} catch (NumberFormatException e) {
	  Log.d("populateTableDetails", e.toString());
	  return false;
	}
	return true;
  }

  private boolean downloadUrlRequest(String url, String name) {

	if (!csvIsUpdated(url, name)) 
	  return false;

	UIMessage.notificationMessage(context, "Downloading... " + url + "/" + name);

	String filePath = context.getFilesDir().getPath().toString() + "/" + name;
	File file = new File(filePath);
	if (file.exists()) file.delete();
	ReadableByteChannel readChannel = null;

	try {
	  readChannel = Channels.newChannel(new URL(url).openStream());
	  FileOutputStream fileOS = new FileOutputStream(filePath);
	  FileChannel writeChannel = fileOS.getChannel();
	  writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
	  writeChannel.close();
	  readChannel.close();
	} catch (IOException e) { Log.d("downloadUrlRequest", e.toString()); }
	return true;
  }

  private boolean csvIsUpdated(String urlString, String name) {
	String filePath = context.getFilesDir().getPath().toString() + "/" + name;
	File csv = new File(filePath);
	if (!csv.exists()) {
	  Database.deleteDatabase();
	  return true;
	}
	try {
	  URL url = new URL(urlString);
	  HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

	  Timestamp urlTimeStamp = new Timestamp(httpCon.getLastModified());
	  Timestamp csvTimeStamp = new Timestamp(csv.lastModified());

	  if (urlTimeStamp.after(csvTimeStamp)) {
		Database.deleteDatabase();
		return true;
	  }
	} catch (Exception e) {
	  Log.d("MainActivity", e.toString());
	}
	return false;
  }

  private static Thread thread = null;
  public void getDataFiles() {
	thread = new Thread(new Runnable() {
		@Override 
		public void run() {
		  for (int queue = 0; queue < Constants.Urls.length; queue++) {
			downloadUrlRequest(Constants.Urls[queue], Constants.Names[queue]);
		  }
		  if (!Database.databaseExists()) {
			MainActivity.stack.clear();
			db = Database.getInstance(context);
			populateRegionAndCountry();
			populateTableOverview();
			populateTableDetails();
			andFinally();
		  }
		}
		private void andFinally() {
		  UIMessage.notificationMessage(context, null);
		}
	  });
	thread.start();
  }

  private boolean populateRegionAndCountry() {
	boolean firstRowRead = false;
	boolean secondRowRead = false;
	String Region = null;
	String Country = null;
	List rows = null;

	ArrayList<ORC> orcList = new ArrayList<ORC>();
	hmRegionList = new HashMap<String, Long>();
	hmCountryList = new HashMap<String, Long>();

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvOverviewName;
	rows = readCSV(filePath);
	UIMessage.notificationMessage(context, "Building Overview " + rows.size() + " rows");
	int rowsbuilt = 0;

	for (String[] row: rows) {
	  // Ignore the first row
	  if (!firstRowRead) {
		firstRowRead = true;
		continue;
	  }
	  int index = 0;
	  Country = row[index++];
	  if (Country.indexOf("\"") > -1)
		Country += ", " + row[index++];
	  Region = row[index++];
	  if (Region.equals("Other")) continue;
	  // 2nd row populate country as Terra
	  if (!secondRowRead && firstRowRead) {
		secondRowRead = true;
		Region = "Terra";
	  }
	  if (Country.equals("Global")) continue;
	  ORC orc = new ORC();
	  orc.Region = Region;
	  orc.Country = Country;
	  orcList.add(orc);
	}
	// List of regions....
	ArrayList<String> regionList = new ArrayList<String>();
	for (ORC orc: orcList) {
	  String region = orc.Region;
	  if (region.equals("Terra"))
		continue;
	  if (!regionList.contains(region))
		regionList.add(region);
	}
	// populate table Region
	for (String region: regionList) {
	  ContentValues values = new ContentValues();
	  values.put("Region", region);
	  Long Id = db.insert("Region", null, values);
	  hmRegionList.put(region, Id);
	}
	// populate table Country
	for (ORC orc: orcList) {
	  ContentValues values = new ContentValues();
	  values.put("Country", orc.Country);
	  Long FK_Region = hmRegionList.get(orc.Region);
	  values.put("FK_Region", FK_Region);
	  Long Id = db.insert("Country", null, values);
	  hmCountryList.put(orc.Country, Id);
	}
	return true;
  }
  class ORC { //Overview: Region & Country
	public String Region = null;
	public String Country = null;
  }  
}



