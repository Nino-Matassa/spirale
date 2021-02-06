package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.graphics.*;
import android.icu.text.*;
import android.os.*;
import android.util.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UICase7Day extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private int regionId = 0;
  private int countryId = 0;
  private String Region = null;
  private String Country = null;
  private MetaField metaField = null;

  public UICase7Day(Context context, int regionId, int countryId) {
	super(context, Constants.UICase7Day);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();

	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UICase7Day);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(Region, Country);
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, Country, Region, NewCases from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	Region = cDetail.getString(cDetail.getColumnIndex("Region"));
	Country = cDetail.getString(cDetail.getColumnIndex("Country"));
	
	ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.seven);
	for(CaseRangeTotal fieldTotal: fieldTotals) {
	  metaField = new MetaField(regionId, countryId, Constants.UICase7Day);
	  metaField.key = fieldTotal.date;
	  metaField.value = String.valueOf(formatter.format(fieldTotal.total));
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
}
