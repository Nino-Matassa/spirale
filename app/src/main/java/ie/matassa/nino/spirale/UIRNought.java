package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UIRNought extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private String region = null;
  private String country = null;
  private MetaField metaField = null;

  public UIRNought(Context context, int regionId, int countryId) {
	super(context, Constants.UIRNought);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRNought);
	MainActivity.stack.add(uiHistory);
  }
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(region, country);
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select distinct Date, NewCase, Detail.Region, Detail.Country from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cRNought = db.rawQuery(sqlDetail, null);
	cRNought.moveToFirst();
	region = cRNought.getString(cRNought.getColumnIndex("Region"));
	country = cRNought.getString(cRNought.getColumnIndex("Country"));

	ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	for (RNoughtAverage values: rNoughtAverage) {
	  metaField = new MetaField(regionId, countryId, Constants.UIRNought);
	  metaField.key = values.date;
	  metaField.value = String.valueOf(formatter.format(values.rNought));
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
}
