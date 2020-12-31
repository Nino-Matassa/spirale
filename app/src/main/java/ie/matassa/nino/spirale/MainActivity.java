package ie.matassa.nino.spirale;

import android.app.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;


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
			getDataFiles(/*whoListener*/);
		  } catch (Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
		}
	  }, 500);
  }

  @Override
  public void onBackPressed() {
	//Database.setInstanceToNull();
	System.exit(0);
	super.onBackPressed();
  }

//  public interface WHOListener { public void WHOThreadFinished(); }
//  WHOListener whoListener = new WHOListener() {
//	@Override
//	public void WHOThreadFinished() {
//	  terra();
//	}
//  };

  private static boolean bDownloadRequest = false;
  private static Thread thread = null;
  public void getDataFiles(/*final WHOListener whoListener*/) {
	//if (thread != null) { return; }
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
		  //whoListener.WHOThreadFinished();
		}
	  });
	thread.start();
	try {
	  thread.join(); 
	  } catch (InterruptedException e) {
		Log.d("getDataFiles", e.toString());
		} finally { // and after the thread has finished....
		  UIMessage.notificationMessage(MainActivity.this, null);
		  new GenerateTables(MainActivity.this);
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
		  } catch (Exception e) { Log.d("MainActivity.terra", e.toString()); }
		}     
	  });
  }
}


