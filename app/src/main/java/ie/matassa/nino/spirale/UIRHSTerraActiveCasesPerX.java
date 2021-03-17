package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import java.util.*;

public class UIRHSTerraActiveCasesPerX extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;

  public UIRHSTerraActiveCasesPerX(Context context, int regionId, int countryId) {
	super(context, Constants.UIRHSTerraActiveCasesPerX);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }

  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRHSTerraActiveCasesPerX);
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
	double population = 0.0;
	{ // calculate population
	  String sql = "select TotalCase, CasePer100000 from overview where region = 'Terra'";
	  Cursor cTerra = db.rawQuery(sql, null);
	  cTerra.moveToFirst();

	  int totalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
	  double CasePer100000 = cTerra.getDouble(cTerra.getColumnIndex("CasePer100000"));
	  population = totalCase / CasePer100000 * Constants.oneHundredThousand;
	}
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlDetail = "select Date, sum(NewCase) as CaseX from Detail group by date order by date desc";
	Cursor cDetail = db.rawQuery(sqlDetail, null);
    cDetail.moveToFirst();

	ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cDetail, Constants.moonPhase);
	for(CaseRangeTotal fieldTotal: fieldTotals) {
	  metaField = new MetaField(regionId, countryId, Constants.UIRHSTerraActiveCasesPerX);
	  metaField.key = fieldTotal.date;
	  Double activeCases_C = fieldTotal.total/population*Constants.oneHundredThousand;
	  metaField.value = String.valueOf(formatter.format(activeCases_C));
	  metaFields.add(metaField);
	}
    setTableLayout(populateTable(metaFields)); 
  }
  
}
