package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITerraDeath24PerMillion extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraDeath24PerMillion(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraDeath24PerMillion);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraDeath24PerMillion);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Death24/Hundred Thousand");
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Country.Id, Country.FK_Region, Detail.Country, NewDeaths from Detail join Country on Detail.FK_Country = Country.Id group by Detail.Country";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
	cDetail.moveToFirst();
	do {
	  int regionId = cDetail.getInt(cDetail.getColumnIndex("FK_Region"));
	  int countryId = cDetail.getInt(cDetail.getColumnIndex("Id"));
	  
	  String sqlOverview = "select Overview.TotalDeath, Overview.DeathPerMillion from Overview join Country on Overview.Country = Country.Country where Country.Id = #1 group by Overview.Country";
	  sqlOverview = sqlOverview.replace("#1", String.valueOf(countryId));
	  Cursor cOverview = db.rawQuery(sqlOverview, null);
	  cOverview.moveToFirst();
	  int totalDeaths = cOverview.getInt(cOverview.getColumnIndex("TotalDeath"));
	  int deathPerMillion = cOverview.getInt(cOverview.getColumnIndex("DeathPerMillion"));
	  double population = 0.0;
	  if(totalDeaths > 0 && deathPerMillion > 0)
		population = totalDeaths/deathPerMillion*Constants._C;
	  
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cDetail.getString(cDetail.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  int newDeaths = cDetail.getInt(cDetail.getColumnIndex("NewDeaths"));
	  double newDeathPerMillion = 0.0;
	  if(population > 0.0)
	  	newDeathPerMillion = newDeaths/population*Constants._C;
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(newDeathPerMillion));
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
