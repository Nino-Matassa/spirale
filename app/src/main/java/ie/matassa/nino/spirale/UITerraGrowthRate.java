package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITerraGrowthRate extends UI implements IRegisterOnStack {
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
  private Double growthRate = 0.0;

  public UITerraGrowthRate(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraGrowthRate);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraGrowthRate);
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
	  if(previous > today) {
		growthRate = previous/(double)today;
	  } else if(today > previous) {
		growthRate = today/(double)previous;
	  } else if(today == previous) {
		growthRate = 1.0;
	  } else {
		growthRate = 0.0;
	  }

	  if(Double.isInfinite(growthRate) || Double.isNaN(growthRate))
		continue;


	  metaField = new MetaField(regionId, countryId, Constants.UITerraGrowthRate);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(Math.log(growthRate)));
	  metaFields.add(metaField);

	  today = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	} while(cDetail.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }

}
