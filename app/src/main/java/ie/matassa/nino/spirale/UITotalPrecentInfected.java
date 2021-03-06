package ie.matassa.nino.spirale;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UITotalPrecentInfected extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private String Region = null;
  private String Country = null;
  private MetaField metaField = null;
  private Double casePer100000 = 0.0;
  private Integer totalCases = 0;
  private Double population = 0.0;
  private Integer case24 = 0;
  private Double percentInfected = 0.0;

  public UITotalPrecentInfected(Context context, int regionId, int countryId) {
	super(context, Constants.UITotalPrecentInfected);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "Estimate of precentage affected.");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITotalPrecentInfected);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(Region, UIMessage.abbreviate(Country, Constants.abbreviate));
		UIMessage.informationBox(context, null);
        }
      }, Constants.delayMilliSeconds);
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, NewCase, Detail.Region, Detail.Country, CasePer100000, Overview.TotalCase from Detail join Overview on Overview.FK_Country = Detail.FK_Country where Overview.FK_Country = #1 order by date asc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	Region = cDetail.getString(cDetail.getColumnIndex("Region"));
	Country = cDetail.getString(cDetail.getColumnIndex("Country"));
	casePer100000 = cDetail.getDouble(cDetail.getColumnIndex("CasePer100000"));
	totalCases = cDetail.getInt(cDetail.getColumnIndex("TotalCase"));
	population = totalCases / casePer100000 * Constants.oneHundredThousand;
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  case24 += cDetail.getInt(cDetail.getColumnIndex("NewCase"));
	  percentInfected = case24 / population * 100;

	  metaField = new MetaField(regionId, countryId, Constants.UICase24Hour);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(percentInfected));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
	Collections.reverse(metaFields);
    setTableLayout(populateTable(metaFields)); 
  }
}
