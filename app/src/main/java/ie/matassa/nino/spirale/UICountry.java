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
  private String lastUpdated = null;
  private double Population = 0.0;
  private int TotalCase = 0;
  private double CasePerMillion = 0.0;
  private int Case7Day = 0;
  private int Case24Hour = 0;
  private int TotalDeath = 0;
  private double DeathPerMillion = 0.0;
  private int Death7Day = 0;
  private int Death24Hour = 0;
  private String Source = null;

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
	String sqlDetail = "select Date, Country from Detail where FK_Country = #1 order by Date desc limit 1".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	lastUpdated = cDetail.getString(cDetail.getColumnIndex("Date"));
	try {
      lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
      String[] arrDate = lastUpdated.split(" ");
      lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	} catch (Exception e) {
      Log.d(Constants.UICountry, e.toString());
	}
	
	Country = cDetail.getString(cDetail.getColumnIndex("Country"));


	String sqlOverview = "select Region, Country, TotalCase, max(CasePerMillion) as CasePerMillion, Case7Day, Case24Hour, TotalDeath, DeathPerMillion, Death7Day, Death24Hour, Source from Overview where Country = '#1'".replace("#1", Country); // Ireland, FK_Region = 3, FK_Country = 76
	Cursor cOverview = db.rawQuery(sqlOverview, null);
	cOverview.moveToFirst();
	
	Region = cOverview.getString(cOverview.getColumnIndex("Region"));
	TotalCase = cOverview.getInt(cOverview.getColumnIndex("TotalCase"));
	CasePerMillion = cOverview.getDouble(cOverview.getColumnIndex("CasePerMillion"));
	Case7Day = cOverview.getInt(cOverview.getColumnIndex("Case7Day"));
	Case24Hour = cOverview.getInt(cOverview.getColumnIndex("Case24Hour"));
	TotalDeath = cOverview.getInt(cOverview.getColumnIndex("TotalDeath"));
	DeathPerMillion = cOverview.getDouble(cOverview.getColumnIndex("DeathPerMillion"));
	Death7Day = cOverview.getInt(cOverview.getColumnIndex("Death7Day"));
	Death24Hour = cOverview.getInt(cOverview.getColumnIndex("Death24Hour"));
	Source = cOverview.getString(cOverview.getColumnIndex("Source"));
	Population = TotalCase / CasePerMillion * Constants.oneMillion;

	MetaField metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Last Updated";
	metaField.value = lastUpdated;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(Population));
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Total Cases";
	metaField.value = String.valueOf(formatter.format(TotalCase));
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Case/Million";
	metaField.value = String.valueOf(formatter.format(CasePerMillion));
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Case7Day";
	metaField.value = String.valueOf(formatter.format(Case7Day));
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICase24Hour);
	metaField.key = "Case24Hour";
	metaField.value = String.valueOf(formatter.format(Case24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(TotalDeath));
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Death/Million";
	metaField.value = String.valueOf(formatter.format(DeathPerMillion));
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Death7Day";
	metaField.value = String.valueOf(formatter.format(Death7Day));
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UIDeath24Hour);
	metaField.key = "Death24Hour";
	metaField.value = String.valueOf(formatter.format(Death24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Source";
	metaField.value = Source;
	metaFields.add(metaField);
	
    setTableLayout(populateTable(metaFields)); 
  }
}

