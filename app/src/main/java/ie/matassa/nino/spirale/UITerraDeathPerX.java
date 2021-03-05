package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;

public class UITerraDeathPerX extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraDeathPerX(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraDeathPerX);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraDeathPerX);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Death/" + Constants.roman100000);
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select Country.Id, Country.FK_Region, Detail.Country from Detail join Country on Detail.FK_Country = Country.Id group by Detail.Country";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));
	  metaField.key = country;
	  
	  country = country.replace("'", "''");

	  String sDeathPerMillion = "select max(DeathPer100000) as DeathPer100000 from Overview where Country = '#1'".replace("#1", country);
	  Cursor cDeathPerMillion = db.rawQuery(sDeathPerMillion, null);
	  cDeathPerMillion.moveToFirst();
	  Double ceathPer100000 = cDeathPerMillion.getDouble(cDeathPerMillion.getColumnIndex("DeathPer100000"));

	  metaField.value = String.valueOf(formatter.format(ceathPer100000));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);


	} while(cTerra.moveToNext());

	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
  class sortStats implements Comparator<MetaField>
  {
    @Override
    public int compare(MetaField mfA, MetaField mfB) {
      // TODO: Implement this method
      Double dA = Double.parseDouble(mfA.value.replace(",", ""));
      Double dB = Double.parseDouble(mfB.value.replace(",", ""));
      return dB.compareTo(dA);
	}
  }
}
