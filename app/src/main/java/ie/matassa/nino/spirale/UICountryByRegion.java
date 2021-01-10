package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UICountryByRegion extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = null;
  private String Country = null;

  public UICountryByRegion(Context context, int regionId, int countryId) {
	super(context);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");

	uiHandler();
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateRegion();
		  setHeader(Region, "Case/Million");
		  UIMessage.notificationMessage(context, null);
		  registerOnStack();
        }
      });
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UICountryByRegion);
	MainActivity.stack.add(uiHistory);
  }


  private void populateRegion() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select Id, Country, FK_Region from Country where Country.FK_Region = #1 order by Country";
	sql = sql.replace("#1", String.valueOf(regionId));
    Cursor cRegion = db.rawQuery(sql, null);
    cRegion.moveToFirst();
	Region = "TODO";//cRegion.getString(cRegion.getColumnIndex("Region"));
    MetaField metaField = null;
    do {
	  metaField = new MetaField(regionId, countryId, Constants.UICountryByRegion);
	  metaField.key = cRegion.getString(cRegion.getColumnIndex("Country"));
	  metaField.value = "TODO";//String.valueOf(formatter.format(cRegion.getInt(cRegion.getColumnIndex("CasePerMillion"))));
	  metaField.underlineKey = true;
	  metaField.UI = Constants.UICountry;
	  metaField.regionId = regionId;
	  metaField.countryId = cRegion.getInt(cRegion.getColumnIndex("Id"));
      metaFields.add(metaField);
	} while(cRegion.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }
}
