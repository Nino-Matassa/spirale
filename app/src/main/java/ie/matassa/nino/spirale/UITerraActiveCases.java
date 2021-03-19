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
		  setHeader("Country", "Active Cases");
        }
      });
  }
  
  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlCountry = "select Country, FK_Country, max(Date) Date from Detail group by Country";
	Cursor cCountry = db.rawQuery(sqlCountry, null);
	cCountry.moveToFirst();
	do {
	  countryId = cCountry.getInt(cCountry.getColumnIndex("FK_Country"));
	  String date = cCountry.getString(cCountry.getColumnIndex("Date"));
	  String country = cCountry.getString(cCountry.getColumnIndex("Country"));
	  country = country.replace("'", "''");
	  String sqlDetail = "select Country.Id, Country.FK_Region, Country.Country, Region, sum(NewCase) ActiveCase from Detail join Country on Detail.FK_Country = Country.Id where Country.Id = #1 and date > date('#2', '-28 days') group by Country.Country order by NewCase desc";
	  sqlDetail = sqlDetail.replace("#1", String.valueOf(countryId)).replace("#2", date);
	  Cursor cDetail = db.rawQuery(sqlDetail, null);
	  cDetail.moveToFirst();
	  regionId = cDetail.getInt(cDetail.getColumnIndex("FK_Region"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  int activeCase = cDetail.getInt(cDetail.getColumnIndex("ActiveCase"));
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(activeCase));
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


