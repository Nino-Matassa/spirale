package ie.matassa.nino.spirale;

import android.app.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.sql.*;


public class MainActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
  }

  @Override
  protected void onResume() {
	super.onResume();
	UIMessage.notificationMessage(MainActivity.this, "Checking For Updates");
	Database.setInstanceToNull();
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  try {
			Database.setInstanceToNull();
			getDataFiles();
		  } catch (Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
		}
	  }, 500);
  }

  @Override
  public void onBackPressed() {
	System.exit(0);
	super.onBackPressed();
  }

  private static boolean bDownloadRequest = false;
  private static Thread thread = null;
  public void getDataFiles() {
	thread = new Thread(new Runnable() {
		@Override 
		public void run() {
		  CSV csv = new CSV(MainActivity.this);
		  for (int queue = 0; queue < Constants.Urls.length; queue++) {
			bDownloadRequest = csv.downloadUrlRequest(Constants.Urls[queue], Constants.Names[queue]);
			if (bDownloadRequest && Constants.Urls[queue].equals(Constants.CsvOverviewURL)) {
			  Database.setInstanceToNull();
			  csv.generateDatabaseTable(Constants.csvOverviewName);  
			}
			if (bDownloadRequest && Constants.Urls[queue].equals(Constants.CsvDetailsURL)) {
			  csv.generateDatabaseTable(Constants.csvDetailsName);
			}
		  }
		  if (!Database.databaseExists()) {
			csv.generateDatabaseTable(Constants.csvOverviewName); 
			csv.generateDatabaseTable(Constants.csvDetailsName);
		  }
		}
	  });
	thread.start();
	try {
	  thread.join(); 
	} catch (InterruptedException e) {
	  Log.d("getDataFiles", e.toString());
	}
	finally { // and after the thread has finished....
	  terra();
	}
  }

  private void terra() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.post(new Runnable() {
		@Override
		public void run() {
		  try {
			new GenerateTables(MainActivity.this);
			new UITerra(MainActivity.this);
		  } catch (Exception e) { 
		  Log.d("MainActivity.terra", e.toString());
		  } finally {
			UIMessage.notificationMessage(MainActivity.this, null);
		  }
		}     
	  });
  }
}


