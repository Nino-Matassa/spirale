package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.util.*;

public class UIDeathPer_C extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;
  private String region = null;
  private String country = null;

  public UIDeathPer_C(Context context, int regionId, int countryId) {
	super(context, Constants.UIDeathPer_C);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIDeathPer_C);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(region, UIMessage.abbreviate(country, Constants.abbreviate));
		}
	  });
  }

  private void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	Double deathPer_C = 0.0;
	Double population = 0.0;
	String sqlDetail = "select Date, Country, Region, TotalDeath from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
	cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));
	{ // Get population from Overview table
	  String sqlCPM = "select DeathPer_C, TotalDeath from Overview where FK_Country = #1 limit 1";
	  sqlCPM = sqlCPM.replace("#1", String.valueOf(countryId));
	  Cursor cCPM = db.rawQuery(sqlCPM, null);
	  cCPM.moveToFirst();
	  deathPer_C = cCPM.getDouble(cCPM.getColumnIndex("DeathPer_C"));
	  int totalDeath = cCPM.getInt(cCPM.getColumnIndex("TotalDeath"));
	  population = totalDeath/(double)deathPer_C*Constants._C;
	}
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }

	  int totalDeaths = cDetail.getInt(cDetail.getColumnIndex("TotalDeath"));
	  deathPer_C = totalDeaths/population*Constants._C;

	  metaField = new MetaField(regionId, countryId, Constants.UIDeathPer_C);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(deathPer_C));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
	setTableLayout(populateTable(metaFields)); 
  }
}
