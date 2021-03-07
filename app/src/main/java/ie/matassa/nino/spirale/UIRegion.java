package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;
import android.database.*;
import android.icu.text.*;

public class UIRegion extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = "Terra";

  public UIRegion(Context context, int regionId, int countryId) {
	super(context, Constants.UIRegion);
	this.context = context;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();

	uiHandler();
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateRegion();
		  setHeader(Region, "Population");
        }
      });
  }
  
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRegion);
	MainActivity.stack.add(uiHistory);
  }
  
  private void populateRegion() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlRegion = "select Id, Region from Region";
    Cursor cRegion = db.rawQuery(sqlRegion, null);
    cRegion.moveToFirst();
	MetaField metaField = null;
    do {
	  metaField = new MetaField(regionId, countryId, Constants.UICountryByRegion);
	  metaField.key = cRegion.getString(cRegion.getColumnIndex("Region"));
	  metaField.underlineKey = true;
	  metaField.UI = Constants.UICountryByRegion;
	  metaField.regionId = cRegion.getInt(cRegion.getColumnIndex("Id"));
	  String sqlOverview = "select count(Id) as N, sum(TotalCase) as TotalCase, sum(CasePer100000) as CasePer100000 from Overview where FK_Region = #1".replace("#1", String.valueOf(metaField.regionId));
	  Cursor cOverview = db.rawQuery(sqlOverview, null);
	  cOverview.moveToFirst();
	  int N = cOverview.getInt(cOverview.getColumnIndex("N"));
	  int totalCase = cOverview.getInt(cOverview.getColumnIndex("TotalCase"));
	  double casePer100000 = cOverview.getDouble(cOverview.getColumnIndex("CasePer100000"));
	  casePer100000 = casePer100000/N;
	  double population = totalCase/casePer100000*Constants.oneHundredThousand;
	  metaField.value = String.valueOf(formatter.format(Math.round(population)));
      metaFields.add(metaField);
	} while(cRegion.moveToNext());
	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
}

class sortStats implements Comparator<MetaField> {
  @Override
  public int compare(MetaField mfA, MetaField mfB) {
	Double dA = Double.parseDouble(mfA.value.replace(",", ""));
	Double dB = Double.parseDouble(mfB.value.replace(",", ""));
	return dB.compareTo(dA);
  }
}
