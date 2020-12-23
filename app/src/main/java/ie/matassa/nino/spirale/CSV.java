package ie.matassa.nino.spirale;
import android.content.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.util.*;
import android.database.sqlite.*;
import android.app.*;
import java.net.*;
import java.nio.channels.*;
import android.widget.*;
import android.text.format.*;
import android.os.*;
import java.sql.*;

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

  public boolean populateTableOverview() {
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

  public boolean populateTableDetails() {
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

  public boolean downloadUrlRequest(String url, String name) {
	if (!csvIsUpdated(url, name)) 
	  return false;
	toast(context, url, Toast.LENGTH_SHORT);
	notificationMessage(context, url);
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
	if (!csv.exists())
	  return true;
	if (!DateUtils.isToday(csv.lastModified()))
	  return true;
	try { // Only relevant if the files are updated more than once per day
	  URL url = new URL(urlString);
	  URLConnection urlConnection = url.openConnection();
	  urlConnection.connect();
	  Long urlTimeStamp = urlConnection.getDate();
	  Long csvTimeStamp = csv.lastModified();
	  java.util.Date urlTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(urlTimeStamp).toString());
	  java.util.Date csvTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(csvTimeStamp).toString());
	  if (urlTS.after(csvTS)) {
		return true;
	  }
	} catch (Exception e) {
	  Log.d("MainActivity", e.toString());
	}
	return false;
  }

  public void generateDatabaseTable(String nameOfCsvFile) {
	db = Database.getInstance(context);
	switch (nameOfCsvFile) {
	  case Constants.csvOverviewName:
		notificationMessage(context, "Populating Table Overview");
		populateTableOverview();
		//alertDialog.dismiss();
		break;
	  case Constants.csvDetailsName:
		notificationMessage(context, "Populating Table Detail");
		populateTableDetails(); // ignore for now takes too long
		//alertDialog.dismiss();
		break;
	  default:
		break;
	}
  }

  private static void toast(final Context context, final String text, final int length) {
	new Handler(Looper.getMainLooper()).post(new Runnable() {
		@Override
		public void run() {
		  Toast.makeText(context, text, length).show();
		}
	  });
  }

  private static AlertDialog.Builder builder = null;
  private static AlertDialog alertDialog = null;

  public static void notificationMessage(final Context context, final String msg) {
    MainActivity.activity.runOnUiThread(new Runnable() {
		@Override
		public void run() {
		  if(msg == null) {
			alertDialog.dismiss();
			return;
		  }
		  if (builder == null) {
			builder = new AlertDialog.Builder(context);
			alertDialog = builder.create();
		  } else {
			alertDialog.dismiss(); // It's already been run
		  }
		  alertDialog.setMessage(msg);
		  alertDialog.show();
        }
      });
  }
}
