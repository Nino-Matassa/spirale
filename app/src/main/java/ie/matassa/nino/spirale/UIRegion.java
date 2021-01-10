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
  private int regionId = 0;
  private int countryId = 0;
  private String Region = "Terra";

  public UIRegion(Context context, int regionId, int countryId) {
	super(context);
	this.context = context;
	formatter = new DecimalFormat("#,###.##");
	registerOnStack();

	uiHandler();
  }

  private void uiHandler() {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
		@Override
		public void run() {
		  populateRegion();
		  setHeader(Region, "Case/Million");
		  UIMessage.notificationMessage(context, null);
        }
      });
  }
  
  @Override
  public void registerOnStack() {
	uiHistory = new UIHistory(regionId, countryId, Constants.UIRegion);
	MainActivity.stack.add(uiHistory);
  }
  

  private void populateRegion() {
    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	String sql = "select Region.Id, Region.Region, sum(Overview.CasePerMillion) as CasePerMillion from Region join Overview on Region.id = Overview.FK_Region group by Region.Region order by CasePerMillion";
    Cursor cRegion = db.rawQuery(sql, null);
    cRegion.moveToFirst();
	MetaField metaField = null;
    do {
	  metaField = new MetaField(regionId, countryId, Constants.UICountryByRegion);
	  metaField.key = cRegion.getString(cRegion.getColumnIndex("Region"));
	  metaField.value = String.valueOf(formatter.format(cRegion.getInt(cRegion.getColumnIndex("CasePerMillion"))));
	  metaField.underlineKey = true;
	  metaField.UI = Constants.UICountryByRegion;
	  metaField.regionId = cRegion.getInt(cRegion.getColumnIndex("Id"));
      metaFields.add(metaField);
	} while(cRegion.moveToNext());
    setTableLayout(populateTable(metaFields)); 
  }
}
