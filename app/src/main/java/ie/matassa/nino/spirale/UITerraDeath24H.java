  package ie.matassa.nino.spirale;
  import android.content.*;
  import android.icu.text.*;
  import android.os.*;
  import java.util.*;
  import android.database.*;

public class UITerraDeath24H extends UI implements IRegisterOnStack {

	private Context context = null;
	private int regionId = 0;
	private int countryId = 0;
	private DecimalFormat formatter = null;
	private UIHistory uiHistory = null;
	private MetaField metaField = null;

  public UITerraDeath24H(Context context, int regionId, int countryId) {
	super(context, Constants.UITerraDeath24H);
	  this.context = context;
	  this.regionId = regionId;
	  this.countryId = countryId;
	  formatter = new DecimalFormat("#,###.##");
	UIMessage.informationBox(context, "Deaths in the last 24 hours.");
	  registerOnStack();
	  uiHandler();
	}

	@Override
	public void registerOnStack() {
	  uiHistory = new UIHistory(regionId, countryId, Constants.UITerraDeath24H);
	  MainActivity.stack.add(uiHistory);
	}

	private void uiHandler() {
	  Handler handler = new Handler(Looper.getMainLooper());
	  handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
			populateTable();
			setHeader("Country", "Death24H");
		  UIMessage.informationBox(context, null);
		  }
		}, 500);
	}

	private void populateTable() {
	  ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
	  String sql = "select Country.Id, Country.FK_Region, Detail.Country, NewDeath from Detail join Country on Detail.FK_Country = Country.Id group by Detail.Country";
	  Cursor cTerra = db.rawQuery(sql, null);
	  cTerra.moveToFirst();
	  do {
		int regionId = cTerra.getInt(cTerra.getColumnIndex("FK_Region"));
		int countryId = cTerra.getInt(cTerra.getColumnIndex("Id"));
		metaField = new MetaField(regionId, countryId, Constants.UICountry);
		String country = cTerra.getString(cTerra.getColumnIndex("Country"));

		country = country.replace("'", "''");

		int newDeath = cTerra.getInt(cTerra.getColumnIndex("NewDeath"));
		metaField.key = country;
		metaField.value = String.valueOf(formatter.format(newDeath));
		metaField.underlineKey = true;
		metaFields.add(metaField);


	  } while(cTerra.moveToNext());

	  metaFields.sort(new sortStats());
	  setTableLayout(populateTable(metaFields)); 
	}
	class sortStats implements Comparator<MetaField> {
	  @Override
	  public int compare(MetaField mfA, MetaField mfB) {
		Integer iA = Integer.parseInt(mfA.value.replace(",", ""));
		Integer iB = Integer.parseInt(mfB.value.replace(",", ""));
		return iB.compareTo(iA);
	  }
	}
  }
