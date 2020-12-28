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
  DecimalFormat formatter = null;

  public UIOverview(Context context) {
	super(context);
	this.context = context;
	formatter = new DecimalFormat("#,###.##");

	uiHandler();
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateOverview();
		  setHeaderTwoColumns("Overview", "Total Case");
		  setFooter("Order by Total Case");
		  UIMessage.notificationMessage(context, null);
        }
      });
  }

  private void populateOverview() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
    //String sql = "select distinct Country, Case24Hour, TotalCase, Case7Day, Death24Hour, TotalDeath, CasePerMillion from Overview where Country is not 'Global' order by TotalCase desc";
	String sql = "select country, Date, TotalCases, NewCases from detail group by country order by totalcases desc";
    Cursor cOverview = db.rawQuery(sql, null);
    cOverview.moveToFirst();
    MetaField metaField = new MetaField();
	int countryIndex = 1;
    do {
	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
	  metaField.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCases")))) + " : "
	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("NewCases"))));
      metaFields.add(metaField);
      metaField = new MetaField();
	} while(cOverview.moveToNext());
    setTableLayout(populateWithTwoColumns(metaFields)); 
  }
}
