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

  private String Country = null;
  private Integer TotalCase = null;
  private Double CasePerMillion = 0.0;
  private Integer Case7Day = 0;
  private Integer Case24Hour = 0;
  private Integer TotalDeath = 0;
  private Double DeathPerMillion = 0.0;
  private Integer Death7Day = 0;
  private Integer Death24Hour = 0;
  private String lastUpdated = null;
  private Double precentInfected = 0.0;
  private Double reproductionRate = 0.0;
  private Double population = 0.0;
  private Double infectionRate = 0.0;
  
  
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
		  UIMessage.notificationMessage(context, null);
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

	Country = cTerra.getString(cTerra.getColumnIndex("Country"));
	TotalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	CasePerMillion = cTerra.getDouble(cTerra.getColumnIndex("CasePerMillion"));
	Case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
	Case24Hour = cTerra.getInt(cTerra.getColumnIndex("Case24Hour"));
	TotalDeath = cTerra.getInt(cTerra.getColumnIndex("TotalDeath"));
	DeathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("DeathPerMillion"));
	Death7Day = cTerra.getInt(cTerra.getColumnIndex("Death7Day"));
	Death24Hour = cTerra.getInt(cTerra.getColumnIndex("Death24Hour"));
	population = TotalCase/CasePerMillion*Constants.oneMillion;
	precentInfected = TotalCase/population*100;
	infectionRate = (double)TotalCase/(TotalCase-Case24Hour);

	MetaField metaField = new MetaField();
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(population));
	metaField.underlineKey = true;
	metaField.UI = Constants.UIRegion;
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Total Cases";
	metaField.value = String.valueOf(formatter.format(TotalCase));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Case/Million";
	metaField.value = String.valueOf(formatter.format(CasePerMillion));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Case/24";
	metaField.value = String.valueOf(formatter.format(Case24Hour));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Case/7D";
	metaField.value = String.valueOf(formatter.format(Case7Day));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Total Deaths";
	metaField.value = String.valueOf(formatter.format(TotalDeath));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Death/Million";
	metaField.value = String.valueOf(formatter.format(DeathPerMillion));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Death/24";
	metaField.value = String.valueOf(formatter.format(Death24Hour));
	metaFields.add(metaField);
	metaField = new MetaField();

	metaField.key = "Death/7D";
	metaField.value = String.valueOf(formatter.format(Death7Day));
	metaFields.add(metaField);
	metaField = new MetaField();
	
	metaField.key = "Total Infected";
	metaField.value = String.valueOf(formatter.format(precentInfected)) + "%";
	metaFields.add(metaField);
	metaField = new MetaField();
	
	metaField.key = "Infection Rate";
	metaField.value = String.valueOf(formatter.format(infectionRate));
	metaFields.add(metaField);
	metaField = new MetaField();
		
	metaField.key = "";
	metaField.value = "";
	metaFields.add(metaField);
	setTableLayout(populateTable(metaFields));
  }

}
