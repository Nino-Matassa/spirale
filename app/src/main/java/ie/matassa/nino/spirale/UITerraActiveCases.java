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
  private String region = null;
  private String country = null;

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
	String sql = "select Country.Id, Country.FK_Region, Country.Country, Region, sum(NewCase) ActiveCase from Detail join Country on Detail.Country = Country.Country where date >= date('now', '-28 days') group by Country.Country order by NewCase desc";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  int activeCase = cTerra.getInt(cTerra.getColumnIndex("ActiveCase"));
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(activeCase));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);


	} while(cTerra.moveToNext());

	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
}

