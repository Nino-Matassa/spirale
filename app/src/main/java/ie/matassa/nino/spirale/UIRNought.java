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
  private Integer today = 0;
  private Integer yesterday = 0;
  private Double rNought = 0.0;

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
	String sqlDetail = "select distinct Date, NewCases, Detail.Region, Detail.Country from Detail join Overview on Overview.Country = Detail.Country where FK_Country = #1 order by date asc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));
	today = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	cDetail.moveToNext();
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  yesterday = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	  rNought = 1 - Math.log(today/(double)yesterday);
	  
	  metaField = new MetaField(regionId, countryId, Constants.UIRNought);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(rNought));
	  metaFields.add(metaField);
	  
	  today = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	} while(cDetail.moveToNext());
	Collections.reverse(metaFields);
    setTableLayout(populateTable(metaFields)); 
  }
  
}
