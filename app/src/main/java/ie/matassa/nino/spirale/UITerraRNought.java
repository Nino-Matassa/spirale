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
  private String region = null;
  private String country = null;
  private MetaField metaField = null;
  private Integer today = 0;
  private Integer previous = 0;
  private Double rNought = 0.0;

  public UITerraRNought(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraRNought);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
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
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Date", "Terra");
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select distinct Date, sum(NewCases) as NewCases from Detail group by date order by date desc";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
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
	  
	  previous = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	  rNought = new RNoughtCalculation(today, previous).calculate();

	  metaField = new MetaField(regionId, countryId, Constants.UITerraRNought);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(rNought));
	  metaFields.add(metaField);

	  today = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	} while(cDetail.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }

}
