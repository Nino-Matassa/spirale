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
  private Integer totalCase = null;
  private Double casePer_C = 0.0;
  private Integer case7Day = 0;
  private Integer case24Hour = 0;
  private Integer totalDeath = 0;
  private Double deathPer_C = 0.0;
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

    String sql = "select distinct Country, TotalCase, CasePer_C, Case7Day, Case24Hour, TotalDeath, DeathPer_C, Death7Day, Death24Hour, Source from overview where region = 'Terra'";
	Cursor cTerra = db.rawQuery(sql, null);
    cTerra.moveToFirst();

	country = cTerra.getString(cTerra.getColumnIndex("Country"));
	totalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	casePer_C = cTerra.getDouble(cTerra.getColumnIndex("CasePer_C"));
	case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
	case24Hour = cTerra.getInt(cTerra.getColumnIndex("Case24Hour"));
	totalDeath = cTerra.getInt(cTerra.getColumnIndex("TotalDeath"));
	deathPer_C = cTerra.getDouble(cTerra.getColumnIndex("DeathPer_C"));
	death7Day = cTerra.getInt(cTerra.getColumnIndex("Death7Day"));
	death24Hour = cTerra.getInt(cTerra.getColumnIndex("Death24Hour"));
	population = totalCase/casePer_C*Constants._C;
	precentInfected = totalCase/population*100;
	infectionsCurve = Math.log((double)case24Hour);

	MetaField metaField = new MetaField();
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(population));
	metaField.underlineKey = true;
	metaField.UI = Constants.UIRegion;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalCases);
	metaField.key = "Total Cases";
	metaField.value = String.valueOf(formatter.format(totalCase));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(0, 0, Constants.UITerraActiveCases);
	metaField.key = "Active Cases";
	String sqlDetail = "select sum(NewCase) NewCase from detail where date >= date('now', '-28 days')";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	int activeCases = cDetail.getInt(cDetail.getColumnIndex("NewCase"));
	metaField.value = String.valueOf(formatter.format(activeCases));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(0, 0, Constants.UITerraCase24Per_C);
	metaField.key = "Case24/CM";
	double case24PerMillion = casePer_C/population*Constants._C;
	metaField.value = String.valueOf(formatter.format(case24PerMillion));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(0, 0, Constants.UITerraCasePer_C);
	metaField.key = "Case/CM";
	metaField.value = String.valueOf(formatter.format(casePer_C));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCase24H);
	metaField.key = "Case/24";
	metaField.value = String.valueOf(formatter.format(case24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCase7D);
	metaField.key = "Case/7D";
	metaField.value = String.valueOf(formatter.format(case7Day));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalDeaths);
	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(totalDeath));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath24Per_C);
	metaField.key = "Death24/CM";
	double death24PerMillion = deathPer_C/population*Constants._C;
	metaField.value = String.valueOf(formatter.format(death24PerMillion));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	
	metaField = new MetaField(0, 0, Constants.UITerraDeathPer_C);
	metaField.key = "Death/CM";
	metaField.value = String.valueOf(formatter.format(deathPer_C));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath24H);
	metaField.key = "Death/24";
	metaField.value = String.valueOf(formatter.format(death24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath7D);
	metaField.key = "Death/7D";
	metaField.value = String.valueOf(formatter.format(death7Day));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalInfected);
	metaField.key = "Total Infected";
	metaField.value = String.valueOf(formatter.format(precentInfected)) + "%";
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraInfectionsCurve);
	metaField.key = "Infections Curve";
	metaField.value = String.valueOf(formatter.format(infectionsCurve));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	String sqlRNought = "select Date, sum(NewCase) as NewCase from Detail group by Date order by Date desc";
	Cursor cRNought = db.rawQuery(sqlRNought, null);

	ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought = rNoughtAverage.get(0).rNought;
	metaField = new MetaField(0, 0, Constants.UITerraRNought);
	metaField.key = "Ro";
	metaField.value = String.valueOf(formatter.format(rNought));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	ArrayList<RNoughtAverage> rNoughtAverage7 = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	Double rNought7 = rNoughtAverage7.get(0).average;
	metaField = new MetaField(0, 0, Constants.UITerraRNought7);
	metaField.key = "Ro/7";
	metaField.value = String.valueOf(formatter.format(rNought7));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	ArrayList<RNoughtAverage> rNoughtAverage14 = new RNoughtCalculation().calculate(cRNought, Constants.fourteen);
	Double rNought14 = rNoughtAverage14.get(0).average;
	metaField = new MetaField(0, 0, Constants.UITerraRNought14);
	metaField.key = "Ro/14";
	metaField.value = String.valueOf(formatter.format(rNought14));
	metaField.underlineKey = true;
	metaFields.add(metaField);
	metaField = new MetaField();
	
	metaField.key = "";
	metaField.value = "";
	metaFields.add(metaField);
	metaField = new MetaField();
	
	setTableLayout(populateTable(metaFields));
  }

}
