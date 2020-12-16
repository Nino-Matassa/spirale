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


public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {
		getDataFiles(interfaceQueue);
		super.onResume();
	}
	
	private void apresDownload() {
		toast(MainActivity.this, "Apres Download", Toast.LENGTH_SHORT);
	}

	private int queue = 0;
	private interface FileQueue { void fileInQueue(); }
	private FileQueue interfaceQueue = new FileQueue() {
		@Override
		public void fileInQueue() {
			if (queue == 1) {
				getDataFiles(this);
			} else {
				apresDownload();
			}
		}
	};

	private Thread thread = null;
	private void getDataFiles(final FileQueue fileQueueListener) {
		if (thread != null) { return; }
		thread = new Thread(new Runnable() {
				@Override 
				public void run() {
					downloadUrlRequest(Constants.Urls[queue], Constants.Names[queue]);
					queue++;
					if (queue == 1) thread = null;
					fileQueueListener.fileInQueue();
				}
			});
		thread.start();
	}

	private boolean downloadUrlRequest(String url, String name) {
		if(!csvIsUpdated(url, name)) 
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
		catch (IOException e) { Log.d("MainActivity", e.toString()); }
		return true;
	}
	
	private boolean csvIsUpdated(String urlString, String name) {
		String filePath = getFilesDir().getPath().toString() + "/" + name;
		File csv = new File(filePath);
		if(!DateUtils.isToday(csv.lastModified()))
			return true;
		if(!csv.exists())
			return true;
		try {
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			Long urlTimeStamp = urlConnection.getDate();
			Long csvTimeStamp = csv.lastModified();
			java.util.Date urlTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(urlTimeStamp).toString());
			java.util.Date csvTS = new SimpleDateFormat("yyyy-MM-dd").parse(new Timestamp(csvTimeStamp).toString());
			if(urlTS.after(csvTS)) {
				return true;
			}
		}
		catch (Exception e) {
			Log.d("MainActivity", e.toString());
		}
		return false;
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


