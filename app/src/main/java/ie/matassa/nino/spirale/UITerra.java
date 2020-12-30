package ie.matassa.nino.spirale;
import android.content.*;
import android.app.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;
import android.net.*;
import android.util.*;

public class UITerra extends UI {
  protected Context context = null;
  DecimalFormat formatter = null;
  
  String Country = null;
  Integer TotalCase = null;
  Double CasePerMillion = 0.0;
  Integer Case7Day = 0;
  Integer Case24Hour = 0;
  Integer TotalDeath = 0;
  Double DeathPerMillion = 0.0;
  Integer Death7Day = 0;
  Integer Death24Hour = 0;
  String lastUpdated = null;
  
  ArrayList<MetaField> metaFields = new ArrayList<MetaField>();

  public UITerra(Context context) {
	super(context);
	this.context = context;
	formatter = new DecimalFormat("#,###.##");

	uiHandler();
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTerra();
		  populateOverview();
		  setTableLayout(populateWithTwoColumns(metaFields));
		  setHeaderTwoColumns("Terra", lastUpdated);
		  setFooter("Terra");
		  UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateTerra() {
    //ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
    String sql = "select Country, TotalCase, CasePerMillion, Case7Day, Case24Hour, TotalDeath, DeathPerMillion, Death7Day, Death24Hour, Source from overview where region = 'Terra'";
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
	
    try { 
	  Date date = new Date();
      //lastUpdated = new SimpleDateFormat("yyyy-MM-dd").parse(lastUpdated).toString();
	  SimpleDateFormat formatter = new SimpleDateFormat();
	  lastUpdated = formatter.format(date);
      String[] arrDate = lastUpdated.split(" ");
      lastUpdated = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	} catch(Exception e) {
      Log.d("UITerra", e.toString());
	}
	
	MetaField metaField = new MetaField();
	metaField.key = "Population";
	metaField.value = String.valueOf(formatter.format(TotalCase * CasePerMillion));
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
	
	metaField.key = "";
	metaField.value = "";
	metaFields.add(metaField);
	metaField = new MetaField();
	
	metaField.key = "Country";
	metaField.value = "Cases + New";
	metaFields.add(metaField);

    //setTableLayout(populateWithTwoColumns(metaFields)); 
  }
  
  private void populateOverview() {
    //ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
    //String sql = "select distinct Country, Case24Hour, TotalCase, Case7Day, Death24Hour, TotalDeath, CasePerMillion from Overview where Country is not 'Global' order by TotalCase desc";
	String sql = "select country, Date, TotalCases, NewCases from detail group by country order by totalcases desc";
    Cursor cOverview = db.rawQuery(sql, null);
    cOverview.moveToFirst();
    MetaField metaField = new MetaField();
	int countryIndex = 1;
    do {
	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
	  metaField.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCases")))) + " : "
	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("NewCases"))));
      metaFields.add(metaField);
      metaField = new MetaField();
	} while(cOverview.moveToNext());
	metaFields.add(metaField);
    //setTableLayout(populateWithTwoColumns(metaFields)); 
  }
}
