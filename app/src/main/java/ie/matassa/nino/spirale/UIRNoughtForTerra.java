package ie.matassa.nino.spirale;

import android.content.*;
import android.database.*;
import android.icu.text.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;

public class UIRNoughtForTerra extends UI implements IRegisterOnStack {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;
  private MetaField metaField = null;
  
  public UIRNoughtForTerra(Context context, int regionId, int countryId) {
	super(context, Constants.UIRNoughtForTerra);
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;

	formatter = new DecimalFormat("#,###.##");
	registerOnStack();
	uiHandler();
  }
  
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRNoughtForTerra);
	MainActivity.stack.add(uiHistory);
  }
  
  private void uiHandler() {
	Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateTable();
		  setHeader("Terra", Constants.rNought);
        }
      });
  }
  
  private void populateTable() {
	ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sqlRNought = "select Date, sum(NewCase) as CaseX from Detail group by Date order by Date desc";
	Cursor cRNought = db.rawQuery(sqlRNought, null);

	ArrayList<RNoughtAverage> rNoughtAverages = new RNoughtCalculation().calculate(cRNought, Constants.moonPhase);
	
	for(RNoughtAverage rNoughtAverage: rNoughtAverages) {
	  metaField = new MetaField(regionId, countryId, Constants.UIRNoughtForTerra);
	  metaField.key = rNoughtAverage.date;
	  metaField.value = String.valueOf(formatter.format(rNoughtAverage.average));
	  metaFields.add(metaField);
	}
	setTableLayout(populateTable(metaFields)); 
  }
}
