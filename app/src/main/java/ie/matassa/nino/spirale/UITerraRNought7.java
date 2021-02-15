  package ie.matassa.nino.spirale;

  import android.content.*;
  import android.database.*;
  import android.icu.text.*;
  import android.os.*;
  import android.util.*;
  import ie.matassa.nino.spirale.*;
  import java.util.*;

  public class UITerraRNought7 extends UI implements IRegisterOnStack {
	private Context context = null;
	private int regionId = 0;
	private int countryId = 0;
	private DecimalFormat formatter = null;
	private UIHistory uiHistory = null;
	private MetaField metaField = null;

	public UITerraRNought7(Context context, int regionId, int countryId) {
	  super(context, Constants.UITerraRNought7);
	  this.context = context;
	  this.regionId = regionId;
	  this.countryId = countryId;

	  formatter = new DecimalFormat("#,###.##");
	  registerOnStack();
	  uiHandler();
	}
	@Override
	public void registerOnStack() {
	  uiHistory = new UIHistory(regionId, countryId, Constants.UITerraRNought7);
	  MainActivity.stack.add(uiHistory);
	}
	private void uiHandler() {
	  Handler handler = new Handler(Looper.getMainLooper());
	  handler.post(new Runnable() {
		  @Override
		  public void run() {
			populateTable();
			setHeader("Date", "Terra");
		  }
		});
	}

	private void populateTable() {
	  ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	  String sqlDetail = "select distinct Date, sum(NewCase) as NewCase from Detail group by date order by date desc";
	  Cursor cRNought = db.rawQuery(sqlDetail, null);

	  ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.seven);
	  for (RNoughtAverage values: rNoughtAverage) {
		metaField = new MetaField(regionId, countryId, Constants.UITerraRNought7);
		metaField.key = values.date;
		metaField.value = String.valueOf(formatter.format(values.average));
		metaFields.add(metaField);
	  }
	  setTableLayout(populateTable(metaFields)); 
	}

  }
