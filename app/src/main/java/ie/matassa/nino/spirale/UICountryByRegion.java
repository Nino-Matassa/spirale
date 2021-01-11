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
	String sqlCountryRegion = "select Country.Id, Country, FK_Region, Region from Country join Region on Country.FK_Region = Region.Id where Country.FK_Region = #1 order by Country";
	sqlCountryRegion = sqlCountryRegion.replace("#1", String.valueOf(regionId));
    Cursor cRegion = db.rawQuery(sqlCountryRegion, null);
    cRegion.moveToFirst();
	Region = cRegion.getString(cRegion.getColumnIndex("Region"));
    MetaField metaField = null;
    do {
	  String sqlCPM = "select max(CasePerMillion) as CasePerMillion from Overview where FK_Region = #1 and Country = '#2'";
	  sqlCPM = sqlCPM.replace("#1", String.valueOf(regionId));
	  sqlCPM = sqlCPM.replace("#2", cRegion.getString(cRegion.getColumnIndex("Country")));
	  Cursor cCPM = db.rawQuery(sqlCPM, null);
	  cCPM.moveToFirst();
	  double casePerMillion = cCPM.getDouble(cCPM.getColumnIndex("CasePerMillion"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountryByRegion);
	  metaField.key = cRegion.getString(cRegion.getColumnIndex("Country"));
	  metaField.value = String.valueOf(formatter.format(casePerMillion));
	  metaField.underlineKey = true;
	  metaField.UI = Constants.UICountry;
	  metaField.regionId = regionId;
	  metaField.countryId = cRegion.getInt(cRegion.getColumnIndex("Id"));
      metaFields.add(metaField);
	} while(cRegion.moveToNext());
	metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
}

class sortStats implements Comparator<MetaField>
{
  @Override
  public int compare(MetaField mfA, MetaField mfB) {
	// TODO: Implement this method
	Double dA = Double.parseDouble(mfA.value.replace(",", ""));
	Double dB = Double.parseDouble(mfB.value.replace(",", ""));
	return dA.compareTo(dB);
  }
}
