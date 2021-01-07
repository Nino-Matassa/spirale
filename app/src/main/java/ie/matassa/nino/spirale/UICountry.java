package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.net.*;
import android.os.*;
import android.util.*;
import java.util.*;

public class UICountry extends UI implements IRegisterOnStack {

  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = null;
  private String Country = null;

  public UICountry(Context context, int regionId, int countryId) {
	super(context);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UICountry);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateCountry();
		  setHeader(Region, Country);
		  UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateCountry() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	MetaField metaField = new MetaField();
	String sql = "select Region.Region, Country.Country, Detail.Date, Overview.TotalCase, Overview.CasePerMillion, Overview.Case7Day, Overview.Case24Hour, Overview.TotalDeath, Overview.DeathPerMillion, Overview.Death7Day, Overview.Death24Hour, Source from Overview join Region on Overview.FK_Region = Region.Id join Country on Region.Id = Country.FK_Region join Detail on Country.Id = Detail.FK_Country where Region.Id = #1 and Country.Id = #2 order by date desc limit 1"; // Ireland, FK_Region = 3, FK_Country = 76
	sql = sql.replace("#1", String.valueOf(regionId)).replace("#2", String.valueOf(countryId));
    Cursor cCountry = db.rawQuery(sql, null);
    cCountry.moveToFirst();
	Region = cCountry.getString(cCountry.getColumnIndex("Region"));
	Country = cCountry.getString(cCountry.getColumnIndex("Country"));
	// Date
	metaField = new MetaField();
	metaField.regionId = regionId;
	metaField.countryId = countryId;

	String lastUpdated = cCountry.getString(cCountry.getColumnIndex("Date"));
    try {
      lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
      String[] arrDate = lastUpdated.split(" ");
      lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	} catch(Exception e) {
      Log.d(Constants.UICountry, e.toString());
	}

	metaField.key = "Last Updated";
	metaField.value = lastUpdated;
	metaFields.add(metaField);
	// Total Cases
	metaField = new MetaField();
	metaField.regionId = regionId;
	metaField.countryId = countryId;
	metaField.key = "TotalCase";
	metaField.value = String.valueOf(cCountry.getInt(cCountry.getColumnIndex("TotalCase")));
	metaFields.add(metaField);
	
    setTableLayout(populateTable(metaFields)); 
  }
}

/*

	TotalCase
	CasePerMillion
	Case7Day
	Case24Hour
	TotalDeath
	DeathPerMillion
	Death7Day
	Death24Hour
	Source
	
	TotalCase, CasePerMillion, Case7Day, Case24Hour, TotalDeath, DeathPerMillion, Death7Day, Death24Hour, Source

*/
