package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.util.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.sql.*;
import java.util.*;

import java.util.Date;
import android.widget.*;


public class CSV {
  private Context context = null;
  private SQLiteDatabase db = null;

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
	Double CasePerMillion = 0.0;
	Integer Case7Day = 0;
	Integer Case24Hour = 0;
	Integer TotalDeath = 0;
	Double DeathPerMillion = 0.0;
	Integer Death7Day = 0;
	Integer Death24Hour = 0;
	String Source = null;
	List rows = null;

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvOverviewName;
	rows = readCSV(filePath);
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
		CasePerMillion = Double.parseDouble(row[index++]);
		Case7Day = Integer.parseInt(row[index++]);
		Case24Hour = Integer.parseInt(row[index++]);
		TotalDeath = Integer.parseInt(row[index++]);
		DeathPerMillion = Double.parseDouble(row[index++]);
		Death7Day = Integer.parseInt(row[index++]);
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
		values.put("TotalCase", TotalCase);
		values.put("CasePerMillion", CasePerMillion);
		values.put("Case7Day", Case7Day);
		values.put("Case24Hour", Case24Hour);
		values.put("TotalDeath", TotalDeath);
		values.put("DeathPerMillion", DeathPerMillion);
		values.put("Death7Day", Death7Day);
		values.put("Death24Hour", Death24Hour);
		values.put("Source", Source);
		Long Id = db.insert("Overview", null, values);
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
	Integer NewCases = 0;
	Integer TotalCases = 0;
	Integer NewDeaths = 0;
	Integer TotalDeaths = 0;
	List rows = null;

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvDetailsName;
	rows = readCSV(filePath);

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
		NewCases = Integer.parseInt(row[index++]);
		TotalCases = Integer.parseInt(row[index++]);
		NewDeaths = Integer.parseInt(row[index++]);
		TotalDeaths = Integer.parseInt(row[index++]);
		ContentValues values = new ContentValues();
		values.put("Date", Date);
		values.put("Code", Code);
		values.put("Country", Country);
		values.put("Region", Region);
		values.put("NewCases", NewCases);
		values.put("TotalCases", TotalCases);
		values.put("NewDeaths", NewDeaths);
		values.put("TotalDeaths", TotalDeaths);
		Long Id = db.insert("Detail", null, values);
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

  private void generateDatabaseTable(String nameOfCsvFile) {
	db = Database.getInstance(context);
	switch (nameOfCsvFile) {
	  case Constants.csvOverviewName:
		populateTableOverview();
		break;
	  case Constants.csvDetailsName:
		populateTableDetails(); // ignore for now takes too long
		break;
	  default:
		break;
	}
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
			generateDatabaseTable(Constants.csvOverviewName); 
			generateDatabaseTable(Constants.csvDetailsName);
			new GenerateTablesEtc(context);
		  }
		}
	  });
	thread.start();
	try {
	  thread.join(); 
	} catch (InterruptedException e) {
	  Log.d("getDataFiles", e.toString());
	}
  }
}
