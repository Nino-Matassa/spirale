package ie.matassa.nino.spirale;

import android.app.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;


public class MainActivity extends Activity {

  private Activity activity = null;
  private TextView view = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	activity = this;
	setContentView(R.layout.main);
	view = findViewById(R.id.mainTextID);
  }

  @Override
  protected void onResume() {
	UIMessage.notificationMessage(MainActivity.this, activity, "Checking " + Constants.DataSource + " For Updates");

	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  try {
			getDataFiles(whoListener);
		  } catch(Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
		}
	  }, 500);
	super.onResume();
	UIMessage.notificationMessage(MainActivity.this, activity, null);
  }

  public interface WHOListener { public void WHOThreadFinished(); }
  WHOListener whoListener = new WHOListener() {
	@Override
	public void WHOThreadFinished() {
	  UIMessage.notificationMessage(MainActivity.this, activity, null);
	  overview();
	}
  };

  private static boolean bDownloadRequest = false;
  private static Thread thread = null;
  public void getDataFiles(final WHOListener whoListener) {
	if (thread != null) { return; }
	thread = new Thread(new Runnable() {
		@Override 
		public void run() {
		  CSV csv = new CSV(MainActivity.this, activity);
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
		  if(!Database.databaseExists()) {
			csv.generateDatabaseTable(Constants.csvOverviewName); 
			csv.generateDatabaseTable(Constants.csvDetailsName);
		  }
		  whoListener.WHOThreadFinished();
		}
	  });
	thread.start();
  }

  private void overview() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.post(new Runnable() {
		@Override
		public void run() {
		  try {
			new UIOverview(MainActivity.this, activity, "");
		  } catch(Exception e) { Log.d("MainActivity.openTerra", e.toString()); }
		}     
	  });
  }
}


