package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UIRHSTerraActiveCases extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UIRHSTerraActiveCases(Context context, int regionId, int countryId) {
	super(context, Constants.UIRHSTerraActiveCases);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "History of active cases");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRHSTerraActiveCases);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Terra", "Active Cases");
		UIMessage.informationBox(context, null);
        }
      }, 500);
  }
  
  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, sum(NewCase) as CaseX from Detail group by date order by date desc";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();

	ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.moonPhase);
	for(CaseRangeTotal fieldTotal: fieldTotals) {
	  metaField = new MetaField(regionId, countryId, Constants.UIRHSTerraActiveCases);
	  metaField.key = fieldTotal.date;
	  //metaField.value = String.valueOf(formatter.format(fieldTotal.total == 0 ? 0:Math.log(fieldTotal.total)));
	  //String curve = " " + Constants.proportional + " " + String.valueOf(formatter.format(fieldTotal.total == 0 ? 0:Math.log(fieldTotal.total)));
	  metaField.value = String.valueOf(formatter.format(fieldTotal.total));// + curve;
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
}
