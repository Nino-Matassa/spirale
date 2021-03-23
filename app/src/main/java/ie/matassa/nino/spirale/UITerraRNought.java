package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITerraRNought extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraRNought(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraRNought);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	UIMessage.notificationMessage(context, Constants.rNought + " over calculated 28 days listed per country.");
	registerOnStack();
	uiHandler();
  }
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraRNought);
	MainActivity.stack.add(uiHistory);
  }
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", Constants.rNought);
		  UIMessage.notificationMessage(context, null);
        }
      }, 500);
  }

  private void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlCountry = "select Id from Country";
	Cursor cCountry = db.rawQuery(sqlCountry, null);
	cCountry.moveToFirst();

	do {
	  try {
		countryId = cCountry.getInt(cCountry.getColumnIndex("Id"));
		String sqlDetail = "select Date, NewCase as CaseX, Country.Id, Country.FK_Region, Country.Country from Detail join Country on Detail.FK_Country = Country.Id where FK_Country = #1 and CaseX > 0 order by Date desc limit 29".replace("#1", String.valueOf(countryId));
		Cursor cRNought = db.rawQuery(sqlDetail, null);
		cRNought.moveToFirst();
		if(cRNought.isAfterLast())
		  continue;
		regionId = cRNought.getInt(cRNought.getColumnIndex("FK_Region"));
		countryId = cRNought.getInt(cRNought.getColumnIndex("Id"));
		String country = cRNought.getString(cRNought.getColumnIndex("Country"));

		ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.moonPhase);
		metaField = new MetaField(regionId, countryId, Constants.UICountry);
		metaField.key = country;
		metaField.value = String.valueOf(formatter.format(rNoughtAverage.get(0).average));
		metaField.underlineKey = true;
		metaFields.add(metaField);
	  } catch(Exception e) {
		Log.d("UITerraRNought", e.toString());
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
