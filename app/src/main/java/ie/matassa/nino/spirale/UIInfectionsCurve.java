package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.graphics.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UIInfectionsCurve extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private String region = null;
  private String country = null;
  private MetaField metaField = null;
//  private Double casePer_C = 0.0;
//  private Integer totalCases = 0;
//  private Double population = 0.0;
  private Integer case24 = 0;
  private Double infectionsCurve = 0.0;

  public UIInfectionsCurve(Context context, int regionId, int countryId) {
	super(context, Constants.UIInfectionsCurve);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIInfectionsCurve);
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
	String sqlDetail = "select distinct Date, NewCase, Detail.Region, Detail.Country from Detail join Overview on Overview.FK_Country = Detail.FK_Country where Overview.FK_Country = #1 order by date asc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));
//	casePer_C = cDetail.getDouble(cDetail.getColumnIndex("CasePer_C"));
//	totalCases = cDetail.getInt(cDetail.getColumnIndex("TotalCase"));
//	population = totalCases / casePer_C * Constants._C;
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  case24 = cDetail.getInt(cDetail.getColumnIndex("NewCase"));
	  infectionsCurve = Math.log((double)case24);
	  
	  if(!(case24 > 0)) continue;

	  metaField = new MetaField(regionId, countryId, Constants.UIInfectionsCurve);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(infectionsCurve));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
	Collections.reverse(metaFields);
    setTableLayout(populateTable(metaFields)); 
  }
}
