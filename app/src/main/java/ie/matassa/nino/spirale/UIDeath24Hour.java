package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import java.util.*;
import android.database.*;
import android.util.*;
import android.os.*;

public class UIDeath24Hour extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = null;
  private String Country = null;
  private MetaField metaField = null;

  public UIDeath24Hour(Context context, int regionId, int countryId) {
	super(context, Constants.UIDeath24Hour);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();

	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UICase24Hour);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(Region, Country);
		  UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, Country, Region, NewDeaths from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	Region = cDetail.getString(cDetail.getColumnIndex("Region"));
	Country = cDetail.getString(cDetail.getColumnIndex("Country"));
	do {
	  String date = cDetail.getString(cDetail.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UIDeath24Hour, e.toString());
	  }
	  int death24 = cDetail.getInt(cDetail.getColumnIndex("NewDeaths"));

	  metaField = new MetaField(regionId, countryId, Constants.UIDeath24Hour);
	  metaField.key = date;
	  metaField.value = String.valueOf(formatter.format(death24));
	  metaFields.add(metaField);
	} while(cDetail.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }
}
