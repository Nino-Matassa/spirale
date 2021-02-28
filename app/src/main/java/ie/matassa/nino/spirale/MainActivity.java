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

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	
//	UIMessage.notificationMessage(MainActivity.this, "Initialising...");
//	
//	Handler handler = new Handler();
//	handler.postDelayed(new Runnable() {
//		public void run() {
//		  try {
//			interrogateStack(false);
//		  } catch (Exception e) { Log.d("MainActivity.onCreate", e.toString()); }
//		}
//	  }, 10000);
	new CSV(MainActivity.this).getDataFiles();
	//interrogateStack(false);
  }

  @Override
  public void onBackPressed() {
	if (stack.size() == 1) {
	  this.moveTaskToBack(true);
	  UIMessage.toast(MainActivity.this, "Spirale - Moved to Background", Toast.LENGTH_LONG);
	} else {
	  interrogateStack(true);
	}
  }

  private void interrogateStack(boolean bBackPressed) {
	if (stack.isEmpty() || stack.size() == 1) {
	  new UITerra(MainActivity.this);
	  return;
	}
	if(bBackPressed)
		stack.pop();
	UIHistory uiHistory = stack.pop();
	switch (uiHistory.getUIX()) {
	  case Constants.UITerra:
		new UITerra(MainActivity.this);
		new CSV(MainActivity.this).getDataFiles();
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
	  case Constants.UITerraCasePer_C:
		new UITerraCasePer_C(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraDeathPer_C:
		new UITerraDeathPer_C(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
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
	  case Constants.UITerraCase24Per_C:
		new UITerraCase24Per_C(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraDeath24Per_C:
		new UITerraDeath24Per_C(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraActiveCases:
		new UITerraActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UIActiveCases:
		new UIActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraRNought14:
		new UITerraRNought14(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraRNought7:
		new UITerraRNought7(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  case Constants.UITerraRNought:
		new UITerraRNought(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		break;
	  default:
	}
  }

  public boolean onTouchEvent(MotionEvent event) {
	int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
	  stack.clear();
	  interrogateStack(false);
	  new CSV(MainActivity.this).getDataFiles();
	}
	return super.onTouchEvent(event);
  }
}


