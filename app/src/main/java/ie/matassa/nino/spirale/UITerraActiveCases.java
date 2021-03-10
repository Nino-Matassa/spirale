package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UITerraActiveCases extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraActiveCases(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraActiveCases);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraActiveCases);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Active Cases");// + Constants.proportional + " Curve");
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	//String sql = "select Country.Id, Country.FK_Region, Country.Country, Region, sum(NewCase) ActiveCase from Detail join Country on Detail.FK_Country = Country.Id where date > date('now', '-28 days') and date <= date('now') group by Country.Country order by NewCase desc";
	String sql = "select Country.Id, Country.FK_Region, Country.Country, Region from Detail join Country on Detail.FK_Country = Country.Id group by Country.Country";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  String sqlActiveCases = "select distinct Date, Country, Region, NewCase as CaseX from Detail where FK_Country = #1 order by date desc limit 29".replace("#1", String.valueOf(countryId));
	  Cursor cActiveCases = db.rawQuery(sqlActiveCases, null);
	  ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cActiveCases, Constants.moonPhase);
	  int activeCase = fieldTotals.get(0).total;
	  
	  
	  //int activeCase = cTerra.getInt(cTerra.getColumnIndex("ActiveCase"));
	  metaField.key = country;
	  //metaField.value = String.valueOf(formatter.format(activeCase)) + " " + Constants.proportional + " " + String.valueOf(formatter.format(activeCase == 0 ? 0:Math.log(activeCase)));
	  metaField.value = String.valueOf(formatter.format(activeCase));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);
	} while(cTerra.moveToNext());
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


