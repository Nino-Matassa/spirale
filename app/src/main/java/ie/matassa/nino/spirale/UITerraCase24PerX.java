package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;
import android.util.*;

public class UITerraCase24PerX extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraCase24PerX(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraCase24PerX);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "Cases per 100,000 in the last 24 hours.");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraCase24PerX);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Case24H/" + Constants.roman100000);
		  UIMessage.informationBox(context, null);
        }
      }, 500);
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Country.Id, Country.FK_Region, Detail.Country, NewCase from Detail join Country on Detail.FK_Country = Country.Id group by Detail.Country";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
	cDetail.moveToFirst();
	do {
	  int regionId = cDetail.getInt(cDetail.getColumnIndex("FK_Region"));
	  int countryId = cDetail.getInt(cDetail.getColumnIndex("Id"));

	  String sqlOverview = "select Overview.TotalCase, Overview.CasePer100000 from Overview join Country on Overview.Country = Country.Country where Country.Id = #1 group by Overview.Country";
	  sqlOverview = sqlOverview.replace("#1", String.valueOf(countryId));
	  Cursor cOverview = db.rawQuery(sqlOverview, null);
	  cOverview.moveToFirst();
	  int totalCases = cOverview.getInt(cOverview.getColumnIndex("TotalCase"));
	  int CasePer100000 = cOverview.getInt(cOverview.getColumnIndex("CasePer100000"));
	  double population = 0.0;
	  if (totalCases > 0 && CasePer100000 > 0)
		population = totalCases / CasePer100000 * Constants.oneHundredThousand;

	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cDetail.getString(cDetail.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  int newCase = cDetail.getInt(cDetail.getColumnIndex("NewCase"));
	  double newCasePerMillion = 0.0;
	  if (population > 0.0)
		newCasePerMillion = newCase / population * Constants.oneHundredThousand;
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(newCasePerMillion));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);
	  
	} while(cDetail.moveToNext());

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
