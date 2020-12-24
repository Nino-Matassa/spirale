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
		  setHeader("Overview", "Total Case / New Cases/24h");
		  setFooter("Overview of Total Case / New Cases/24h");
        }
      });
  }
  
  private void populateOverview() {
    ArrayList<MetaTable> tkvs = new ArrayList<MetaTable>();
    String sql = "select distinct Country, Case24Hour, TotalCase from Overview where Country is not 'Global' order by TotalCase desc";
    Cursor cOverview = db.rawQuery(sql, null);
    cOverview.moveToFirst();
    MetaTable mt = new MetaTable();
	int countryIndex = 1;
    do {
	  mt.key = String.valueOf(countryIndex++) + " " + cOverview.getString(cOverview.getColumnIndex("Country"));
	  mt.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCase")))) + " / " + String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("Case24Hour"))));
      tkvs.add(mt);
      mt = new MetaTable();
	} while(cOverview.moveToNext());
    setTableLayout(getTableRows(tkvs));
  }
}
