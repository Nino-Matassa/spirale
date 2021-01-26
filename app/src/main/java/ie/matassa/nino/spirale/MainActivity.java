package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.util.*;
import java.util.*;
import android.widget.*;
import android.content.*;
import android.view.*;

public class MainActivity extends Activity {

  public static Stack<UIHistory> stack = new Stack<UIHistory>();
  public static Activity activity = null;


  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	activity = this;

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
	if(stack.size() == 0) { // 0 = called from onCreate no stack yet
	  UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	} else {
	  UIMessage.notificationMessage(MainActivity.this, "Hit the grey area to continue when screen is available.");
	}
	super.onResume();
  }

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

  public boolean onTouchEvent(MotionEvent event) {
	// Touching the header or footer
	int action = event.getAction();//MotionEventCompat.getActionMasked(event);
    if (action == MotionEvent.ACTION_DOWN) {
	  UIMessage.notificationMessage(MainActivity.this, "Checking " + Constants.DataSource);
	  stack.clear();
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

