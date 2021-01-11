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
	
	Database.deleteDatabase();
	UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  try {
			new UITerra(MainActivity.this);
		  } catch (Exception e) { Log.d("MainActivity.getDataFiles", e.toString()); }
		}
	  }, 500);
  }
  
  @Override
  public void onBackPressed() {
	if (stack.size() == 1) {
	  super.onBackPressed();
	} else {
	  UIMessage.notificationMessage(MainActivity.this, "Busy");
	  stack.pop();
	  UIHistory uiHistory = stack.pop();
	  switch (uiHistory.getUIX()) {
		case Constants.UITerra:
		  new UITerra(MainActivity.this);
		  break;
		case Constants.UIRegion:
		  new UIRegion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UICountry:
		  new UICountry(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UICase24Hour:
		  new UICase24Hour(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UICountryByRegion:
		  new UICountryByRegion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		default:
	  }
	}
  }
}


