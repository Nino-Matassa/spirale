package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITerraRNought14 extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraRNought14(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraRNought14);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraRNought14);
	MainActivity.stack.add(uiHistory);
  }
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
	handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Ro\14");
		}
	  });
  }

  private void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlCountry = "select Id from Country";
	Cursor cCountry = db.rawQuery(sqlCountry, null);
	cCountry.moveToFirst();

	do {
	  countryId = cCountry.getInt(cCountry.getColumnIndex("Id"));
	  String sqlDetail = "select Date, NewCase as CaseX, Country.Id, Country.FK_Region, Country.Country from Detail join Country on Detail.FK_Country = Country.Id where FK_Country = #1 order by Date desc limit 15".replace("#1", String.valueOf(countryId));
	  Cursor cRNought = db.rawQuery(sqlDetail, null);
	  cRNought.moveToFirst();
	  regionId = cRNought.getInt(cRNought.getColumnIndex("FK_Region"));
	  countryId = cRNought.getInt(cRNought.getColumnIndex("Id"));
	  String country = cRNought.getString(cRNought.getColumnIndex("Country"));

	  ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.fourteen);
	  for (RNoughtAverage values: rNoughtAverage) {
		metaField = new MetaField(regionId, countryId, Constants.UICountry);
		metaField.key = country;
		metaField.value = String.valueOf(formatter.format(values.average));
		metaField.underlineKey = true;
		metaFields.add(metaField);
		break;
	  }
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
