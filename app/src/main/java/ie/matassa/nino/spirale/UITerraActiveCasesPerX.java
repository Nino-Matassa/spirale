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
	super(context, Constants.UITerraActiveCasesPerX);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "Active cases per 100,000 by country.");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraActiveCasesPerX);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Active Cases/" + Constants.roman100000);
		UIMessage.informationBox(context, null);
        }
      }, Constants.delayMilliSeconds);
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
	String sqlCountry = "select Country, FK_Country, max(Date) Date from Detail group by Country";
	Cursor cCountry = db.rawQuery(sqlCountry, null);
	cCountry.moveToFirst();
	do {
	  countryId = cCountry.getInt(cCountry.getColumnIndex("FK_Country"));
	  String date = cCountry.getString(cCountry.getColumnIndex("Date"));
	  String country = cCountry.getString(cCountry.getColumnIndex("Country"));
	  country = country.replace("'", "''");
	  String sqlDetail = "select Country.Id, Country.FK_Region, Country.Country, Region, sum(NewCase) ActiveCase from Detail join Country on Detail.FK_Country = Country.Id where Country.Id = #1 and date > date('#2', '-27 days') group by Country.Country order by NewCase desc";
	  sqlDetail = sqlDetail.replace("#1", String.valueOf(countryId)).replace("#2", date);
	  Cursor cDetail = db.rawQuery(sqlDetail, null);
	  cDetail.moveToFirst();
	  regionId = cDetail.getInt(cDetail.getColumnIndex("FK_Region"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  int activeCase = cDetail.getInt(cDetail.getColumnIndex("ActiveCase"));
	  Double activeCases_C = activeCase/population*Constants.oneHundredThousand;
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(activeCases_C));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);
	} while(cCountry.moveToNext());
	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
  class sortStats implements Comparator<MetaField> {
	@Override
	public int compare(MetaField mfA, MetaField mfB) {
	  Double dA = Double.parseDouble(mfA.value.replace(",", ""));
	  Double dB = Double.parseDouble(mfB.value.replace(",", ""));
	  return dB.compareTo(dA);
	}
  }
}
  

