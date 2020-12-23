package ie.matassa.nino.spirale;

import android.app.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.widget.*;


public class MainActivity extends Activity {

  public static Activity activity = null;
  private TextView view = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	activity = this;
	setContentView(R.layout.main);
	view = findViewById(R.id.mainTextID);
	//String displayText = (String) view.getText();
	//view.setText(displayText);

	CSV.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource + " For Updates");
	
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  try {
			getDataFiles(whoListener);
		  } catch(Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
		}
	  }, 500);
  }

  public interface WHOListener { public void WHOThreadFinished(); }
  WHOListener whoListener = new WHOListener() {
	@Override
	public void WHOThreadFinished() {
	  CSV.notificationMessage(MainActivity.this, null);
//	  SQLiteDatabase db = Database.getInstance(MainActivity.this);
//	  Cursor overview = db.rawQuery("select Country from overview where id = 1", null);
//	  overview.moveToFirst();
//	  Cursor details = db.rawQuery("select Date from detail where code = 'IE' order by date desc limit 1", null);
//	  details.moveToFirst();
//	  //long currentRegionId = cRegion.getInt(cRegion.getColumnIndex("ID")); // ?
//	  String Country = overview.getString(overview.getColumnIndex("Country")); // ?
//	  String Date = details.getString(details.getColumnIndex("Date"));
//	  String s = Date + Country;
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
		  whoListener.WHOThreadFinished();
		}
	  });
	thread.start();
  }

  @Override
  protected void onResume() {
	super.onResume();
  }
}


