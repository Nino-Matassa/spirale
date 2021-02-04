package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UITerraCase7D extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UITerraCase7D(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraCase7D);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UITerraCase7D);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Country", "Case/7D");
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select distinct Country.Id, Country.FK_Region, Country.Country, Overview.Case7Day from Country join Overview on Country.Country = Overview.Country order by Overview.Case7Day desc";
	Cursor cTerra = db.rawQuery(sql, null);
	cTerra.moveToFirst();
	do {
	  int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
	  int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
	  metaField = new MetaField(regionId, countryId, Constants.UICountry);
	  String country = cTerra.getString(cTerra.getColumnIndex("Country"));

	  country = country.replace("'", "''");

	  int case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
	  metaField.key = country;
	  metaField.value = String.valueOf(formatter.format(case7Day));
	  metaField.underlineKey = true;
	  metaFields.add(metaField);


	} while(cTerra.moveToNext());

	//metaFields.sort(new sortStats());
    setTableLayout(populateTable(metaFields)); 
  }
  class sortStats implements Comparator<MetaField> {
    @Override
    public int compare(MetaField mfA, MetaField mfB) {
	  Integer iA = Integer.parseInt(mfA.value.replace(",", ""));
      Integer iB = Integer.parseInt(mfB.value.replace(",", ""));
      return iB.compareTo(iA);
	}
  }
}
