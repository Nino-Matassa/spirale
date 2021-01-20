package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.util.*;
import java.util.*;
import android.widget.*;
import android.content.*;


public class MainActivity extends Activity {

  public static Stack<UIHistory> stack = new Stack<UIHistory>();
  public static Activity activity = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	activity = this;
	//setTitle("Spirale");

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

  //https://developer.android.com/guide/components/activities/activity-lifecycle

  @Override
  public void onBackPressed() {
	if (stack.size() == 1) {
	  super.onBackPressed();
	} else {
	  stack.pop();
	  UIHistory uiHistory = stack.pop();
	  switch (uiHistory.getUIX()) {
		case Constants.UITerra:
		  UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
		  Handler handler = new Handler();
		  handler.postDelayed(new Runnable() {
			  public void run() {
				try {
				  new UITerra(MainActivity.this);
				} catch (Exception e) { Log.d("MainActivity.onBackPressed", e.toString()); }
			  }
			}, 500);
		  break;
		case Constants.UIRegion:
		  new UIRegion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UICountry:
		  new UICountry(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UICountryByRegion:
		  new UICountryByRegion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraInfectionsCurve:
		  new UITerraInfectionsCurve(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		default:
	  }
	}
  }
}

