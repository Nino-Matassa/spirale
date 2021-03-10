//package ie.matassa.nino.spirale;
//
//import android.content.*;
//import android.database.*;
//import android.icu.text.*;
//import android.os.*;
//import android.util.*;
//import ie.matassa.nino.spirale.*;
//import java.util.*;
//
//public class UITerraInfectionsCurve extends UI implements IRegisterOnStack {
//  private Context context = null;
//  private int regionId = 0;
//  private int countryId = 0;
//  private DecimalFormat formatter = null;
//  private UIHistory uiHistory = null;
//  private MetaField metaField = null;
//  private Integer case24 = 0;
//  private Double infectionsCurve = 0.0;
//
//  public UITerraInfectionsCurve(Context context, int regionId, int countryId) {
//	super(context, Constants.UITerraInfectionsCurve);
//	this.context = context;
//	this.regionId = regionId;
//	this.countryId = countryId;
//	
//	formatter = new DecimalFormat("#,###.##");
//	registerOnStack();
//	uiHandler();
//  }
//  
//  @Override
//  public void registerOnStack() {
//	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraInfectionsCurve);
//	MainActivity.stack.add(uiHistory);
//  }
//  private void uiHandler() {
//	Handler handler = new Handler(Looper.getMainLooper());
//    handler.post(new Runnable() {
//		@Override
//		public void run() {
//		  populateTable();
//		  setHeader("Date", "Terra");
//        }
//      });
//  }
//  
//  private void populateTable() {
//    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
//	String sqlDetail = "select distinct Date, NewCase, Country.Country, Country.FK_Region, Country.Id from Detail join Country on Detail.FK_Country = Country.Id group by Country.Country order by Date, NewCase desc";
//	Cursor cDetail = db.rawQuery(sqlDetail, null);
//    cDetail.moveToFirst();
//	do {
//	  case24 = cDetail.getInt(cDetail.getColumnIndex("NewCase"));
//	  infectionsCurve = Math.log((double)case24);
//	  
//	  if(infectionsCurve.isNaN() || infectionsCurve.isInfinite())
//		infectionsCurve = 0.0;
//	  
//	  regionId = cDetail.getInt(cDetail.getColumnIndex("FK_Region"));
//	  countryId = cDetail.getInt(cDetail.getColumnIndex("Id"));
//
//	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
//	  metaField.key = cDetail.getString(cDetail.getColumnIndex("Country"));
//	  metaField.value = String.valueOf(formatter.format(infectionsCurve));
//	  metaField.underlineKey = true;
//	  metaFields.add(metaField);
//	} while(cDetail.moveToNext());
//    setTableLayout(populateTable(metaFields)); 
//  }
//}
