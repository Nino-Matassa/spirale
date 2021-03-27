package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.util.*;

public class UITotalDeath extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;
  private String region = null;
  private String country = null;

  public UITotalDeath(Context context, int regionId, int countryId) {
	super(context, Constants.UITotalDeath);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "History of total deaths.");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITotalDeath);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(region, UIMessage.abbreviate(country, Constants.abbreviate));
		UIMessage.informationBox(context, null);
		}
	  }, Constants.delayMilliSeconds);
  }

  private void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, Country, Region, TotalDeath from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));
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

	  metaField = new MetaField(regionId, countryId, Constants.UITotalCase);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(totalDeaths));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }
}
