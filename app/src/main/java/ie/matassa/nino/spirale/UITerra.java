package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import java.util.*;
import android.os.*;
import java.io.*;
import android.net.*;
import android.util.*;
import android.database.*;
import java.security.*;

public class UITerra extends UI implements IRegisterOnStack {

  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;

  private String country = null;
  private Integer totalCases = null;
  private Double casePerMillion = 0.0;
  private Integer case7Day = 0;
  private Integer case24Hour = 0;
  private Integer totalDeath = 0;
  private Double deathPerMillion = 0.0;
  private Integer death7Day = 0;
  private Integer death24Hour = 0;
  private String lastUpdated = null;
  private Double precentInfected = 0.0;
  private Double population = 0.0;
  private Double infectionsCurve = 0.0;
  
  
  ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
  public UITerra(Context context) {
	super(context, Constants.UITerra);
	this.context = context;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();

	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(0, 0, Constants.UITerra);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTerra();
		  setHeader("Terra", "General");
        }
      });
  }

  private void populateTerra() {
	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvDetailsName;
	File csv = new File(filePath);
	lastUpdated = new Date(csv.lastModified()).toString();
	String[] arrDate = lastUpdated.split(" ");
	lastUpdated = arrDate[0] + " " + arrDate[2] + " " + arrDate[3] + " " + arrDate[5];

    String sql = "select distinct Country, TotalCase, CasePerMillion, Case7Day, Case24Hour, TotalDeath, DeathPerMillion, Death7Day, Death24Hour, Source from overview where region = 'Terra'";
	Cursor cTerra = db.rawQuery(sql, null);
    cTerra.moveToFirst();

	country = cTerra.getString(cTerra.getColumnIndex("Country"));
	totalCases = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	casePerMillion = cTerra.getDouble(cTerra.getColumnIndex("CasePerMillion"));
	case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
	case24Hour = cTerra.getInt(cTerra.getColumnIndex("Case24Hour"));
	totalDeath = cTerra.getInt(cTerra.getColumnIndex("TotalDeath"));
	deathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("DeathPerMillion"));
	death7Day = cTerra.getInt(cTerra.getColumnIndex("Death7Day"));
	death24Hour = cTerra.getInt(cTerra.getColumnIndex("Death24Hour"));
	population = totalCases/casePerMillion*Constants.oneMillion;
	precentInfected = totalCases/population*100;
	infectionsCurve = Math.log((double)case24Hour);

	MetaField metaField = new MetaField();
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(population));
	metaField.underlineKey = true;
	metaField.UI = Constants.UIRegion;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalCases);
	metaField.key = "Total Cases";
	metaField.value = String.valueOf(formatter.format(totalCases));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField();
	metaField.key = "Case/Million";
	metaField.value = String.valueOf(formatter.format(casePerMillion));
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Case/24";
	metaField.value = String.valueOf(formatter.format(case24Hour));
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Case/7D";
	metaField.value = String.valueOf(formatter.format(case7Day));
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalDeaths);
	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(totalDeath));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Death/Million";
	metaField.value = String.valueOf(formatter.format(deathPerMillion));
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Death/24";
	metaField.value = String.valueOf(formatter.format(death24Hour));
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Death/7D";
	metaField.value = String.valueOf(formatter.format(death7Day));
	metaFields.add(metaField);

	metaField = new MetaField();
	metaField.key = "Total Infected";
	metaField.value = String.valueOf(formatter.format(precentInfected)) + "%";
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraInfectionsCurve);
	metaField.key = "Infections Curve";
	metaField.value = String.valueOf(formatter.format(infectionsCurve));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	String sqlRNought = "select Date, sum(NewCases) as NewCases from Detail group by Date order by Date desc";
	Cursor cRNought = db.rawQuery(sqlRNought, null);

	ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought = rNoughtAverage.get(0).rNought;
	metaField = new MetaField(0, 0, Constants.UITerraRNought);
	metaField.key = "rNought";
	metaField.value = String.valueOf(formatter.format(rNought));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	ArrayList<RNoughtAverage> rNoughtAverage7 = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought7 = rNoughtAverage7.get(0).average;
	metaField = new MetaField(0, 0, Constants.UITerraRNought7);
	metaField.key = "rNought/7";
	metaField.value = String.valueOf(formatter.format(rNought7));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	ArrayList<RNoughtAverage> rNoughtAverage14 = new RNoughtCalculation().calculate(cRNought, Constants.fourteen);
	Double rNought14 = rNoughtAverage14.get(0).average;
	metaField = new MetaField(0, 0, Constants.UITerraRNought14);
	metaField.key = "rNought/14";
	metaField.value = String.valueOf(formatter.format(rNought14));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	metaField.key = "";
	metaField.value = "";
	metaFields.add(metaField);
	metaField = new MetaField();
	
//	metaField.key = "Country";
//	metaField.value = "Infections Curve";
//	metaFields.add(metaField);
//	metaField = new MetaField();
//	
//	int index = 0;
//	sql = "select Country.Id, Country.FK_Region, Detail.Country, NewCases from Detail join Country on Detail.FK_Country = Country.Id group by Detail.Country order by NewCases desc";
//	cTerra = db.rawQuery(sql, null);
//	cTerra.moveToFirst();
//	do {
//	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));
//	  int newCases = cTerra.getInt(cTerra.getColumnIndex("NewCases"));
//	  double log = newCases == 0 ? 0:Math.log(newCases);
//	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
//	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
//	  
//	  metaField.key = "(" + String.valueOf(++index) + ") " + country;
//	  metaField.value = String.valueOf(formatter.format(log));
//	  metaField.regionId = regionId;
//	  metaField.countryId = countryId;
//	  metaField.underlineKey = true;
//	  metaField.UI = Constants.UICountry;
//	  metaFields.add(metaField);
//	  metaField = new MetaField();
//	  
//	} while(cTerra.moveToNext());
	
	setTableLayout(populateTable(metaFields));
  }

}
