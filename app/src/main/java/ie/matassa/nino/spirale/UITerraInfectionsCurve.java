package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITerraInfectionsCurve extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private String region = null;
  private String country = null;
  private MetaField metaField = null;
  private Double casePerMillion = 0.0;
  private Integer totalCases = 0;
  private Double population = 0.0;
  private Integer case24 = 0;
  private Double infectionsCurve = 0.0;

  public UITerraInfectionsCurve(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraInfectionsCurve);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }
  
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraInfectionsCurve);
	MainActivity.stack.add(uiHistory);
  }
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Date", "Terra");
		  //UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select distinct Date, sum(NewCases) as NewCases, Detail.Region, Detail.Country, CasePerMillion, TotalCase from Detail join Overview on Overview.Country = Detail.Country group by date order by date asc";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));
	casePerMillion = cDetail.getDouble(cDetail.getColumnIndex("CasePerMillion"));
	totalCases = cDetail.getInt(cDetail.getColumnIndex("TotalCase"));
	population = totalCases / casePerMillion * Constants.oneMillion;
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  case24 = cDetail.getInt(cDetail.getColumnIndex("NewCases"));
	  infectionsCurve = Math.log((double)case24);

	  if(!(case24 > 0)) continue;

	  metaField = new MetaField(regionId, countryId, Constants.UITerraInfectionsCurve);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(infectionsCurve));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
	Collections.reverse(metaFields);
    setTableLayout(populateTable(metaFields)); 
  }
}
