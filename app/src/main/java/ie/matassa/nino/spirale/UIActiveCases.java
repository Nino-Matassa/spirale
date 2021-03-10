package ie.matassa.nino.spirale;
import android.content.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;
import android.database.*;

public class UIActiveCases extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;
  private String region = null;
  private String country = null;

  public UIActiveCases(Context context, int regionId, int countryId) {
	super(context, Constants.UIActiveCases);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIActiveCases);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader(region, UIMessage.abbreviate(country, Constants.abbreviate));
        }
      });
  }

  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select distinct Date, Country, Region, NewCase as CaseX from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();
	region = cDetail.getString(cDetail.getColumnIndex("Region"));
	country = cDetail.getString(cDetail.getColumnIndex("Country"));

	ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.moonPhase);
	for(CaseRangeTotal fieldTotal: fieldTotals) {
	  metaField = new MetaField(regionId, countryId, Constants.UIActiveCases);
	  metaField.key = fieldTotal.date;
	  metaField.value = String.valueOf(formatter.format(fieldTotal.total));
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
}

