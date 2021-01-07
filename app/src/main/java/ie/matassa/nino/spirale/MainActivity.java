package ie.matassa.nino.spirale;

import android.app.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.sql.*;
import java.util.*;


public class MainActivity extends Activity {
  
  public static Stack<UIHistory> stack = new Stack<UIHistory>();

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
	if(stack.size() == 1) {
	  super.onBackPressed();
	} else {
	  stack.pop();
	  UIHistory uiHistory = stack.pop();
	  switch(uiHistory.getUIX()) {
		case Constants.UITerra:
		  new UITerra(MainActivity.this);
		  break;
		case Constants.UIRegion:
		  new UIRegion(MainActivity.this);
		  break;
		case Constants.UICountry:
		  new UICountry(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		default:
	  }
	}
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
			new GenerateTablesEtc(MainActivity.this);
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
			new UITerra(MainActivity.this);
		  } catch (Exception e) { 
			Log.d("MainActivity.terra", e.toString());
		  }
		  finally {
			UIMessage.notificationMessage(MainActivity.this, null);
		  }
		}     
	  });
  }
}


