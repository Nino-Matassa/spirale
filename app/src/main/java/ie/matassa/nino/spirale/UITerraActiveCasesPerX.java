package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UITerraActiveCasesPerX extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraActiveCasesPerX(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraActiveCasesX);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraActiveCasesX);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Active Cases/" + Constants.roman100000);
        }
      });
  }

  private void populateTable() {
	double population = 0.0;
	{ // calculate population
	  String sql = "select TotalCase, CasePer100000 from overview where region = 'Terra'";
	  Cursor cTerra = db.rawQuery(sql, null);
	  cTerra.moveToFirst();

	  int totalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	  double CasePer100000 = cTerra.getDouble(cTerra.getColumnIndex("CasePer100000"));
	  population = totalCase / CasePer100000 * Constants.oneHundredThousand;
	  
	}
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select Country.Id, Country.FK_Region, Country.Country, Region, sum(NewCase) ActiveCase from Detail join Country on Detail.FK_Country = Country.Id where date >= date('now', '-28 days') group by Country.Country order by NewCase desc";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  int activeCase = cTerra.getInt(cTerra.getColumnIndex("ActiveCase"));
	  Double activeCases_C = activeCase/population*Constants.oneHundredThousand;
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(activeCases_C));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);


	} while(cTerra.moveToNext());

	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
}

