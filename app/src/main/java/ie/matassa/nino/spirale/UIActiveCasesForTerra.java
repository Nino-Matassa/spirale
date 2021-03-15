package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UIActiveCasesForTerra extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UIActiveCasesForTerra(Context context, int regionId, int countryId) {
	super(context, Constants.UIActiveCasesForTerra);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIActiveCasesForTerra);
	MainActivity.stack.add(uiHistory);
  }

  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Terra", "Active Cases");
        }
      });
  }
  
  private void populateTable() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, sum(NewCase) as CaseX from Detail group by date order by date desc";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();

	ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.moonPhase);
	for(CaseRangeTotal fieldTotal: fieldTotals) {
	  metaField = new MetaField(regionId, countryId, Constants.UIActiveCasesForTerra);
	  metaField.key = fieldTotal.date;
	  //metaField.value = String.valueOf(formatter.format(fieldTotal.total == 0 ? 0:Math.log(fieldTotal.total)));
	  //String curve = " " + Constants.proportional + " " + String.valueOf(formatter.format(fieldTotal.total == 0 ? 0:Math.log(fieldTotal.total)));
	  metaField.value = String.valueOf(formatter.format(fieldTotal.total));// + curve;
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
}
