package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import java.io.*;
import java.nio.channels.*;
import java.net.*;
import java.text.*;
import java.util.*;
import org.apache.http.impl.client.*;
import java.sql.*;
import android.text.format.*;
import android.database.sqlite.*;


public class MainActivity extends Activity {

	private TextView view = null;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		view = findViewById(R.id.mainTextID);
	}

	@Override
	protected void onResume() {
		getDataFiles();
		super.onResume();
	}
	
	private Thread thread = null;
	private void getDataFiles() {
		if (thread != null) { return; }
		thread = new Thread(new Runnable() {
				@Override 
				public void run() {
					for(int queue = 0; queue < Constants.Urls.length; queue++) {
						downloadUrlRequest(Constants.Urls[queue], Constants.Names[queue]);	
					}
				}
			});
		thread.start();
		try {
			thread.join();
			postDownload();
		} catch (InterruptedException e) { Log.d("getDataFiles", e.toString()); }
	}

	private boolean downloadUrlRequest(String url, String name) {
		if (!csvIsUpdated(url, name)) 
			return false;
		toast(MainActivity.this, url, Toast.LENGTH_SHORT);
		String filePath = getFilesDir().getPath().toString() + "/" + name;
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
		}
		catch (IOException e) { Log.d("downloadUrlRequest", e.toString()); }
		return true;
	}

	private boolean csvIsUpdated(String urlString, String name) {
		String filePath = getFilesDir().getPath().toString() + "/" + name;
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
		}
		catch (Exception e) {
			Log.d("MainActivity", e.toString());
		}
		return false;
	}
	
	private void postDownload() { // Effective callback
		for(String name: Constants.Names) {
			String text = (String)view.getText();
			text += "\nReading " + name;
			view.setText(text);
			switch(name) {
				case Constants.csvOverviewName:
					 new CSV(MainActivity.this, name).populateTableOverview();
					break;
				case Constants.csvDetailsName:
					new CSV(MainActivity.this, name).populateTableDetails();
					break;
				default:
					break;
			}
		}
		
		try {
			SQLiteDatabase db = Database.getInstance(MainActivity.this);
		} catch(Exception e) {
			Log.d("postDownload", e.toString());
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
}


