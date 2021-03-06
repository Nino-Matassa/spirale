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
  private Double casePer100000 = 0.0;
  private Integer case7Day = 0;
  private Integer case24Hour = 0;
  private Integer totalDeath = 0;
  private Double deathPer100000 = 0.0;
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

    String sql = "select distinct Country, TotalCase, CasePer100000, Case7Day, Case24Hour, TotalDeath, DeathPer100000, Death7Day, Death24Hour, Source from overview where region = 'Terra'";
	Cursor cTerra = db.rawQuery(sql, null);
    cTerra.moveToFirst();

	country = cTerra.getString(cTerra.getColumnIndex("Country"));
	totalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	casePer100000 = cTerra.getDouble(cTerra.getColumnIndex("CasePer100000"));
	case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
	case24Hour = cTerra.getInt(cTerra.getColumnIndex("Case24Hour"));
	totalDeath = cTerra.getInt(cTerra.getColumnIndex("TotalDeath"));
	deathPer100000 = cTerra.getDouble(cTerra.getColumnIndex("DeathPer100000"));
	death7Day = cTerra.getInt(cTerra.getColumnIndex("Death7Day"));
	death24Hour = cTerra.getInt(cTerra.getColumnIndex("Death24Hour"));
	population = totalCase / casePer100000 * Constants.oneHundredThousand;
	precentInfected = totalCase / population * 100;
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
	String sqlActiveCases = "select sum(NewCase) NewCase from detail where date >= date('now', '-28 days')";
	Cursor cActiveCases = db.rawQuery(sqlActiveCases, null);
    cActiveCases.moveToFirst();
	int activeCases = cActiveCases.getInt(cActiveCases.getColumnIndex("NewCase"));
	metaField.value = String.valueOf(formatter.format(activeCases));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraActiveCasesX);
	metaField.key = "Active Cases/" + Constants.roman100000;
	Double activeCases_C = activeCases/population*Constants.oneHundredThousand;
	metaField.value = String.valueOf(formatter.format(activeCases_C));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCase24PerX);
	metaField.key = "Case24H/" + Constants.roman100000;
	double case24Per100000 = casePer100000 / population * Constants.oneHundredThousand;
	metaField.value = String.valueOf(formatter.format(case24Per100000));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCasePerX);
	metaField.key = "Case/" + Constants.roman100000;
	metaField.value = String.valueOf(formatter.format(casePer100000));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCase24H);
	metaField.key = "Case24H";
	metaField.value = String.valueOf(formatter.format(case24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraCase7D);
	metaField.key = "Case7D";
	metaField.value = String.valueOf(formatter.format(case7Day));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraTotalDeaths);
	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(totalDeath));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath24PerX);
	metaField.key = "Death24H/" + Constants.roman100000;
	double death24Per100000 = deathPer100000 / population * Constants.oneHundredThousand;
	metaField.value = String.valueOf(formatter.format(death24Per100000));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeathPerX);
	metaField.key = "Death/" + Constants.roman100000;
	metaField.value = String.valueOf(formatter.format(deathPer100000));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath24H);
	metaField.key = "Death24H";
	metaField.value = String.valueOf(formatter.format(death24Hour));
	metaField.underlineKey = true;
	metaFields.add(metaField);

	metaField = new MetaField(0, 0, Constants.UITerraDeath7D);
	metaField.key = "Death7D";
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

	String sqlRNought = "select Date, sum(NewCase) as CaseX from Detail group by Date order by Date desc limit 29";
	Cursor cRNought = db.rawQuery(sqlRNought, null);

	ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.moonPhase);
	Double rNought = rNoughtAverage.get(0).rNought;
	metaField = new MetaField(0, 0, Constants.UITerraRNought);
	metaField.key = Constants.rNought;
	metaField.value = String.valueOf(formatter.format(rNought));
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
