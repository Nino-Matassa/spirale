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
//  private interface UIListener { void uiListenerFinished(); }
//  UIListener uiListener = new UIListener() {
//	@Override
//	public void uiListenerFinished() {
//	  UIMessage.notificationMessage(context, activity, null);
//	}
//  };
//
//  private Thread thread = null;
//  private void uiHandler(final UIListener uiListener) {
//	if (thread != null) { return; }
//	thread = new Thread(new Runnable() {
//		@Override 
//		public void run() {
//		  UIMessage.notificationMessage(context, activity, "Generating UI");
//		  populateOverview();
//		  setHeader("Overview", "Total Case");
//		  setFooter("Order by Total Case");
//		  uiListener.uiListenerFinished();
//		}
//	  });
//	thread.start();
//  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateOverview();
		  setHeader("Overview", "Total Case");
		  setFooter("Order by Total Case");
		  UIMessage.notificationMessage(context, activity, null);
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
//	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
//	  metaField.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCase")))) + " : "
//	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("Case24Hour"))));
	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
	  metaField.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCases")))) + " : "
	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("NewCases"))));
      metaFields.add(metaField);
      metaField = new MetaField();
	} while(cOverview.moveToNext());
    setTableLayout(getTableRows(metaFields)); 
  }
}
