package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import ie.matassa.nino.spirale.*;
import java.util.*;
import android.database.*;
import android.icu.text.*;

public class UIRegion extends UI implements IRegisterOnStack {
  private Context context = null;
  private DecimalFormat formatter = null;
  private UIHistory uiHistory = null;

  public UIRegion(Context context) {
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
		  populateRegion();
		  setHeaderTwoColumns("Region", "Total Case");
		  UIMessage.notificationMessage(context, null);
        }
      });
  }
  
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(context, 0, 0, Constants.UIRegion);
	MainActivity.stack.add(uiHistory);
  }
  

  private void populateRegion() {
//    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
//	String sql = "select country, Date, TotalCases, NewCases from detail group by country order by totalcases desc";
//    Cursor cOverview = db.rawQuery(sql, null);
//    cOverview.moveToFirst();
//    MetaField metaField = new MetaField();
//	int countryIndex = 1;
//    do {
//	  metaField.key = "(" + String.valueOf(countryIndex++) + ") " + cOverview.getString(cOverview.getColumnIndex("Country"));
//	  metaField.value = String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("TotalCases")))) + " : "
//	  	+ String.valueOf(formatter.format(cOverview.getInt(cOverview.getColumnIndex("NewCases"))));
//      metaFields.add(metaField);
//      metaField = new MetaField();
//	} while(cOverview.moveToNext());
//    setTableLayout(populateWithTwoColumns(metaFields)); 
  }
}
