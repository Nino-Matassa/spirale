package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;
import android.util.*;
import android.app.*;

public class UICountryByRegion extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = null;
  private String Country = null;

  public UICountryByRegion(Context context, int regionId, int countryId) {
	super(context, Constants.UICountryByRegion);
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
		  setHeader(Region, "Infections Curve");
		  ((Activity)context).setTitle("Spirale - " + Region);
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
	String sqlCountryRegion = "select Country.Id, Country, FK_Region, Region from Country join Region on Country.FK_Region = Region.Id where Country.FK_Region = #1 order by Country";
	sqlCountryRegion = sqlCountryRegion.replace("#1", String.valueOf(regionId));
    Cursor cRegion = db.rawQuery(sqlCountryRegion, null);
    cRegion.moveToFirst();
	Region = cRegion.getString(cRegion.getColumnIndex("Region"));
	Cursor cCPM = null;
    MetaField metaField = null;
	try {
	  do {
		String sqlCPM = "select distinct Case24Hour from Overview where FK_Country = #1".replace("#1", String.valueOf(cRegion.getLong(cRegion.getColumnIndex("Id"))));
		cCPM = db.rawQuery(sqlCPM, null);
		cCPM.moveToFirst();
		metaField = new MetaField(regionId, countryId, Constants.UICountryByRegion);
		metaField.key = cRegion.getString(cRegion.getColumnIndex("Country"));
		int case24Hour = cCPM.getInt(cCPM.getColumnIndex("Case24Hour"));
		double log = case24Hour == 0 ? 0:Math.log(case24Hour);
		metaField.value = String.valueOf(formatter.format(log));
		metaField.underlineKey = true;
		metaField.UI = Constants.UICountry;
		metaField.regionId = regionId;
		metaField.countryId = cRegion.getInt(cRegion.getColumnIndex("Id"));
		metaFields.add(metaField);
	  } while(cRegion.moveToNext());
	} catch (Exception e) {
	  Log.d("UICountryByRegion", e.toString());
	}
	//metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
}

class sortStats implements Comparator<MetaField> {
  @Override
  public int compare(MetaField mfA, MetaField mfB) {
	Double dA = Double.parseDouble(mfA.value.replace(",", ""));
	Double dB = Double.parseDouble(mfB.value.replace(",", ""));
	return dB.compareTo(dA);
  }
}
