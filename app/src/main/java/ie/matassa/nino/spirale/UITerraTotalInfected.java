package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UITerraTotalInfected extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraTotalInfected(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraTotalInfected);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	
	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "Estimate of precentage affected.");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraTotalInfected);
	MainActivity.stack.add(uiHistory);
  }
  
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "% Infected");
		UIMessage.informationBox(context, null);
		}
	  }, 500);
  }
  
  public void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select Country.Id, Country.FK_Region, Country.Country, TotalCase, CasePer100000 from Overview join Country on Overview.FK_Country = Country.Id group by Country.Country";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));
	  int totalCases = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	  int casePer100000 = cTerra.getInt(cTerra.getColumnIndex("CasePer100000"));
	  Double population = totalCases/(double)casePer100000*Constants.oneHundredThousand;
	  Double percentInfected = totalCases/population*100;
	  
	  if(percentInfected.isNaN())
		percentInfected = 0.0;
	  
	  country = country.replace("'", "''");
	  
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(percentInfected));
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
