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
import android.content.pm.*;

public class MainActivity extends Activity {

  public static Stack<UIHistory> stack = new Stack<UIHistory>();
  public static boolean bCallUITerra = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	bCallUITerra = true;
	new CSV(MainActivity.this).getDataFiles(false);
  }

  @Override
  public void onBackPressed() {
	if (stack.size() == 1) {
	  this.moveTaskToBack(true);
	  UIMessage.toast(MainActivity.this, "Spirale - Moved to Background", Toast.LENGTH_LONG);
	} else {
	  stack.pop();
	  UIHistory uiHistory = stack.pop();
	  switch (uiHistory.getUIX()) {
		case Constants.UITerra:
		  bCallUITerra = true;
		  new CSV(MainActivity.this).getDataFiles(false);
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
		case Constants.UITerraTotalCases:
		  new UITerraTotalCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraTotalDeaths:
		  new UITerraTotalDeaths(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
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
		case Constants.UITerraActiveCases:
		  new UITerraActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UIActiveCases:
		  new UIActiveCases(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraActiveCasesPerX:
		  new UITerraActiveCasesPerX(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		case Constants.UITerraRNought:
		  new UITerraRNought(MainActivity.this, uiHistory.getRegionId(), uiHistory.getCountryId());
		  break;
		default:
	  }
	}
  }

  public boolean onTouchEvent(MotionEvent event) {
	int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
	  stack.clear();
	  bCallUITerra = true;
	  new CSV(MainActivity.this).getDataFiles(false);
	}
	return super.onTouchEvent(event);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main_menu, menu);
	return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	String message = null;
	switch (item.getItemId()) {
	  case R.id.about:
		message = "COVID-19 statistical analysis using WHO data.";
		message += "\nProject Spirale started Dec 10 2020";
		try {
		  PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
		  message += "\nVersion " + pInfo.versionName + " " + Constants.beta; // Reading in from Androidmanifest.xml
		} catch (PackageManager.NameNotFoundException e) {
		  Log.d("About", e.toString());
		}
		UIMessage.informationBox(MainActivity.this, message);
		break;
	  case R.id.reinitialise:
		message = "Download and reinitialise current WHO CSV files:";
		message += "\n" + Constants.CsvOverviewURL;
		message += "\n" + Constants.CsvDetailsURL;
		UIMessage.informationBox(MainActivity.this, message);
		Database.deleteDatabase();
		bCallUITerra = true;
		new CSV(MainActivity.this).getDataFiles(true);
		break;
	  case R.id.moi:
		message = "Credits:";
		message += "\nNino Matassa MBCS";
		message += "\nSpirale: Dec 10 2020";
		message += "\nCode available";
		message += "\nhttps://github.com/Nino-Matassa/spirale";
		UIMessage.informationBox(MainActivity.this, message);
		break;
		case R.id.home:
		bCallUITerra = true;
		new CSV(MainActivity.this).getDataFiles(false);
		break;
	  default:
		return super.onOptionsItemSelected(item);
	}
	return true;
  }
}


