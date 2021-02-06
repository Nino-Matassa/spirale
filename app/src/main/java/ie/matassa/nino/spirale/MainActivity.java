package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.util.*;
import java.util.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import android.database.*;
import android.database.sqlite.*;

public class MainActivity extends Activity {

  public static Stack<UIHistory> stack = new Stack<UIHistory>();
  private boolean bOnResume = true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	bOnResume = false;
	
	UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  try {
			new UITerra(MainActivity.this);
		  } catch (Exception e) { Log.d("MainActivity.onCreate", e.toString()); }
		}
	  }, 500);
  }

  @Override
  protected void onResume() {
	if(bOnResume)
		UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	super.onResume();
  }
  
  @Override
  public void onBackPressed() {
	if (stack.size() == 1) {
//	  super.onBackPressed();
	  UIMessage.toast(MainActivity.this, "Press Home To Hide In Background", Toast.LENGTH_LONG);
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
		case Constants.UITerraTotalCases:
		  new UITerraTotalCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraTotalDeaths:
		  new UITerraTotalDeaths(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraCasePerMillion:
		  new UITerraCasePerMillion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraDeathPerMillion:
		  new UITerraDeathPerMillion(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraCase24H:
		  new UITerraCase24H(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;	
		case Constants.UITerraCase7D:
		  new UITerraCase7D(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraDeath24H:
		  new UITerraDeath24H(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;	
		case Constants.UITerraDeath7D:
		  new UITerraDeath7D(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraTotalInfected:
		  new UITerraTotalInfected(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		default:
	  }
	}
  }

  public boolean onTouchEvent(MotionEvent event) {
	// Touching the header or footer
	int action = event.getAction();//MotionEventCompat.getActionMasked(event);
    if (action == MotionEvent.ACTION_DOWN) {
	  UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	  Handler handler = new Handler();
	  handler.postDelayed(new Runnable() {
		  public void run() {
			try {
			  new UITerra(MainActivity.this);
			} catch (Exception e) { Log.d("MainActivity.onTouchEvent", e.toString()); }
		  }
		}, 500);
	}
	return super.onTouchEvent(event);
  }
}

