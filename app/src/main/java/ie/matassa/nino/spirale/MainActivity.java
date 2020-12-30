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
	
//	Handler handler = new Handler();
//	handler.postDelayed(new Runnable() {
//		public void run() {
//		  try {
//			funnel();
//		  } catch (Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
//		}
//	  }, 500);
	funnel();
  }

  @Override
  protected void onResume() {
	super.onResume();
	Database.setInstanceToNull();
	funnel();
  }

  @Override
  public void onBackPressed() {
	//Database.setInstanceToNull();
	System.exit(0);
	super.onBackPressed();
  }

  private void funnel() {
	Database.setInstanceToNull();
	getDataFiles(whoListener);
  }

  public interface WHOListener { public void WHOThreadFinished(); }
  WHOListener whoListener = new WHOListener() {
	@Override
	public void WHOThreadFinished() {
	  terra();
	}
  };

  private static boolean bDownloadRequest = false;
  private static Thread thread = null;
  public void getDataFiles(final WHOListener whoListener) {
	if (thread != null) { return; }
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
		  whoListener.WHOThreadFinished();
		}
	  });
	thread.start();
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


