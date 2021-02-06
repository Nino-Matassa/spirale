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
  private String region = null;
  private String Country = null;
  private String lastUpdated = null;
  private double population = 0.0;
  private int totalCases = 0;
  private double casePerMillion = 0.0;
  private int case7Day = 0;
  private int case24Hour = 0;
  private int totalDeath = 0;
  private double deathPerMillion = 0.0;
  private int death7Day = 0;
  private int death24Hour = 0;
  private String source = null;
  private Double precentInfected = 0.0;
  private Double infectionsCurve = 0.0;

  public UICountry(Context context, int regionId, int countryId) {
	super(context, Constants.UICountry);
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
		  setHeader(region, Country);
		  //UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateCountry() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select max(Date) as Date, Country from Detail where FK_Country = #1".replace("#1", String.valueOf(countryId));
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
	
	Country = cDetail.getString(cDetail.getColumnIndex("Country")).replace("'", "''");


	String sqlOverview = "select Region, Country, TotalCase, max(CasePerMillion) as CasePerMillion, Case7Day, Case24Hour, TotalDeath, DeathPerMillion, Death7Day, Death24Hour, Source from Overview where Country = '#1'".replace("#1", Country); // Ireland, FK_Region = 3, FK_Country = 76
	Cursor cOverview = db.rawQuery(sqlOverview, null);
	cOverview.moveToFirst();
	
	region = cOverview.getString(cOverview.getColumnIndex("Region"));
	totalCases = cOverview.getInt(cOverview.getColumnIndex("TotalCase"));
	casePerMillion = cOverview.getDouble(cOverview.getColumnIndex("CasePerMillion"));
	case7Day = cOverview.getInt(cOverview.getColumnIndex("Case7Day"));
	case24Hour = cOverview.getInt(cOverview.getColumnIndex("Case24Hour"));
	totalDeath = cOverview.getInt(cOverview.getColumnIndex("TotalDeath"));
	deathPerMillion = cOverview.getDouble(cOverview.getColumnIndex("DeathPerMillion"));
	death7Day = cOverview.getInt(cOverview.getColumnIndex("Death7Day"));
	death24Hour = cOverview.getInt(cOverview.getColumnIndex("Death24Hour"));
	source = cOverview.getString(cOverview.getColumnIndex("Source"));
	population = totalCases / casePerMillion * Constants.oneMillion;
	precentInfected = totalCases == 0 ? 0:totalCases/population*100;
	infectionsCurve = totalCases == 0 ? 0:Math.log((double)case24Hour);

	MetaField metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Last Updated";
	metaField.value = lastUpdated;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(population));
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UITotalCase);
	metaField.key = "Total Cases";
	metaField.value = String.valueOf(formatter.format(totalCases));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICasePerMillion);
	metaField.key = "Case/Million";
	metaField.value = String.valueOf(formatter.format(casePerMillion));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICase7Day);
	metaField.key = "Case7Day";
	metaField.value = String.valueOf(formatter.format(case7Day));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICase24Hour);
	metaField.key = "Case24Hour";
	metaField.value = String.valueOf(formatter.format(case24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UITotalDeath);
	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(totalDeath));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UIDeathPerMillion);
	metaField.key = "Death/Million";
	metaField.value = String.valueOf(formatter.format(deathPerMillion));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UIDeath7Day);
	metaField.key = "Death7Day";
	metaField.value = String.valueOf(formatter.format(death7Day));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(regionId, countryId, Constants.UIDeath24Hour);
	metaField.key = "Death24Hour";
	metaField.value = String.valueOf(formatter.format(death24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UICountry);
	metaField.key = "Source";
	metaField.value = source;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UITotalPrecentInfected);
	metaField.key = "Total Infected";
	metaField.value = String.valueOf(formatter.format(precentInfected)) + "%";
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(regionId, countryId, Constants.UIInfectionsCurve);
	metaField.key = "Infections Curve";
	metaField.value = String.valueOf(formatter.format(infectionsCurve));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	String sqlRNought = "select Date, NewCases from Detail Where FK_Country = " + countryId + " order by Date desc";
	Cursor cRNought = db.rawQuery(sqlRNought, null);

	ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought = rNoughtAverage.get(0).rNought;
	metaField = new MetaField(regionId, countryId, Constants.UIRNought);
	metaField.key = "rNought";
	metaField.value = String.valueOf(formatter.format(rNought));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	ArrayList<RNoughtAverage> rNoughtAverage7 = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought7 = rNoughtAverage7.get(0).average;
	metaField = new MetaField(regionId, countryId, Constants.UIRNought7);
	metaField.key = "rNought/7day";
	metaField.value = String.valueOf(formatter.format(rNought7));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	ArrayList<RNoughtAverage> rNoughtAverage14 = new RNoughtCalculation().calculate(cRNought, Constants.fourteen);
	Double rNought14 = rNoughtAverage14.get(0).average;
	metaField = new MetaField(regionId, countryId, Constants.UIRNought14);
	metaField.key = "rNought/14day";
	metaField.value = String.valueOf(formatter.format(rNought14));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
    setTableLayout(populateTable(metaFields)); 
  }
}

