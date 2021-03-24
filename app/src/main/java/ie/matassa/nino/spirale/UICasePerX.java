  package ie.matassa.nino.spirale;

  import android.content.*;
  import android.database.*;
  import android.icu.text.*;
  import android.os.*;
  import java.util.*;
  import android.util.*;

public class UICasePerX extends UI implements IRegisterOnStack {
	private Context context = null;
	private int regionId = 0;
	private int countryId = 0;
	private DecimalFormat formatter = null;
	private UIHistory uiHistory = null;
	private MetaField metaField = null;
	private String region = null;
	private String country = null;

  public UICasePerX(Context context, int regionId, int countryId) {
	super(context, Constants.UICasePerX);
	  this.context = context;
	  this.regionId = regionId;
	  this.countryId = countryId;
	  formatter = new DecimalFormat("#,###.##");
	UIMessage.notificationMessage(context, "Cases per 100,000.");
	  registerOnStack();
	  uiHandler();
	}

	@Override
	public void registerOnStack() {
	  uiHistory = new UIHistory(regionId, countryId, Constants.UICasePerX);
	  MainActivity.stack.add(uiHistory);
	}

	private void uiHandler() {
	  Handler handler = new Handler(Looper.getMainLooper());
	  handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
			populateTable();
			setHeader(region, UIMessage.abbreviate(country, Constants.abbreviate));
			UIMessage.notificationMessage(context, null);
		  }
		}, 500);
	}

	private void populateTable() {
	  ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	  Double casePer100000 = 0.0;
	  Double population = 0.0;
	  String sqlDetail = "select Date, Country, Region, TotalCase from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	  Cursor cDetail = db.rawQuery(sqlDetail, null);
	  cDetail.moveToFirst();
	  region = cDetail.getString(cDetail.getColumnIndex("Region"));
	  country = cDetail.getString(cDetail.getColumnIndex("Country"));
	  { // Get population from Overview table
		String sqlCPM = "select CasePer100000, TotalCase from Overview where country = '#1' limit 1";
		sqlCPM = sqlCPM.replace("#1", country.replace("'", "''"));
		Cursor cCPM = db.rawQuery(sqlCPM, null);
		cCPM.moveToFirst();
		casePer100000 = cCPM.getDouble(cCPM.getColumnIndex("CasePer100000"));
		int totalCase = cCPM.getInt(cCPM.getColumnIndex("TotalCase"));
		population = totalCase/(double)casePer100000*Constants.oneHundredThousand;
	  }
	  do {
		String date = cDetail.getString(cDetail.getColumnIndex("Date"));
		try {
		  date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		  String[] arrDate = date.split(" ");
		  date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
		} catch (Exception e) {
		  Log.d(Constants.UICase24Hour, e.toString());
		}
		
		int totalCase = cDetail.getInt(cDetail.getColumnIndex("TotalCase"));
		casePer100000 = totalCase/population*Constants.oneHundredThousand;

		metaField = new MetaField(regionId, countryId, Constants.UICasePerX);
		metaField.key = date;
		metaField.value = String.valueOf(formatter.format(casePer100000));
		metaFields.add(metaField);
	  } while(cDetail.moveToNext());
	  setTableLayout(populateTable(metaFields)); 
	}
  }
