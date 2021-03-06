package ie.matassa.nino.spirale;


  import android.content.*;
  import android.database.*;
  import android.graphics.*;
  import android.icu.text.*;
  import android.os.*;
  import android.util.*;
  import ie.matassa.nino.spirale.*;
  import java.util.*;

public class UIDeath7Day extends UI implements IRegisterOnStack {
	private Context context = null;
	private DecimalFormat formatter = null;
	private UIHistory uiHistory = null;
	private int regionId = 0;
	private int countryId = 0;
	private String Region = null;
	private String Country = null;
	private MetaField metaField = null;

  public UIDeath7Day(Context context, int regionId, int countryId) {
	super(context, Constants.UIDeath7Day);
	  this.context = context;
	  this.regionId = regionId;
	  this.countryId = countryId;
	  formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "History of 7 day death rate.");
	  registerOnStack();

	  uiHandler();
	}

	@Override
	public void registerOnStack() {
	  uiHistory = new UIHistory(regionId, countryId, Constants.UIDeath7Day);
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
	  String sqlDetail = "select Date, Country, Region, NewDeath as CaseX from Detail where FK_Country = #1 order by date desc".replace("#1", String.valueOf(countryId));
	  Cursor cDetail = db.rawQuery(sqlDetail, null);
	  cDetail.moveToFirst();
	  Region = cDetail.getString(cDetail.getColumnIndex("Region"));
	  Country = cDetail.getString(cDetail.getColumnIndex("Country"));

	  ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.seven);
	  for(CaseRangeTotal fieldTotal: fieldTotals) {
		metaField = new MetaField(regionId, countryId, Constants.UIDeath7Day);
		metaField.key = fieldTotal.date;
		metaField.value = String.valueOf(formatter.format(fieldTotal.total));
		metaFields.add(metaField);
	  }
	  setTableLayout(populateTable(metaFields)); 
	}
  }
