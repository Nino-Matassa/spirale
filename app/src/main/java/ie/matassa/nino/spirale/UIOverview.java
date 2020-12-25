package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;
import android.database.*;
import android.icu.text.*;

public class UIOverview extends UI {
  protected Context context = null;
  protected Activity activity = null;
  private String dialogMessage = null;
  DecimalFormat formatter = null;

  public UIOverview(Context context, Activity activity, String dialogMessage) {
	super(context, activity, dialogMessage);
	this.context = context;
	this.activity = activity;
	this.dialogMessage = dialogMessage;
	formatter = new DecimalFormat("#,###.##");
	
	uiHandler();
  }
  
  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateOverview();
		  setHeader("Overview", "Total Case");
		  setFooter("Order by Total Case");
        }
      });
  }
  
  private void populateOverview() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
    String sql = "select distinct Country, Case24Hour, TotalCase, Case7Day, Death24Hour, TotalDeath, CasePerMillion from Overview where Country is not 'Global' order by TotalCase desc";
    Cursor cOverview = db.rawQuery(sql, null);
    cOverview.moveToFirst();
    MetaField metaField = new MetaField();
	int countryIndex = 1;
    do {
	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
	  metaField.key += "\nCases\nCases24H\nCase7Day\nDeath24H\nDeaths\nCasePerMillion";
	  metaField.value = "\n" + String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCase")))) + "\n"
	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("Case24Hour")))) + "\n"
		+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("Case7Day")))) + "\n"
		+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("Death24Hour")))) + "\n"
		+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalDeath")))) + "\n"
		+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("CasePerMillion"))));
      metaFields.add(metaField);
      metaField = new MetaField();
	} while(cOverview.moveToNext());
    setTableLayout(getTableRows(metaFields)); 
  }
}
