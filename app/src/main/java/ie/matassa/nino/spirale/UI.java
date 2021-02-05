package ie.matassa.nino.spirale;
import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import ie.matassa.nino.spirale.*;
import java.util.*;
import java.io.*;

public class UI {

  private Context context = null;
  private String UIX = null;

  private TableLayout tableLayout = null;
  private TableLayout tableLayoutHeader = null;
  private TableLayout tableLayoutFooter = null;
  protected SQLiteDatabase db = null;
  private Vibrator vibrator = null;

  public UI(Context context, String UIX) {
	this.context = context;
	this.UIX = UIX;

	setTitlebar();
	if (UIX.equals(Constants.UITerra)) {
	  new CSV(context).getDataFiles();
	}

	vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
    vibrator.vibrate(80);

	db = Database.getInstance(context);
	((Activity)context).setContentView(R.layout.table_layout);

	tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
	tableLayoutHeader = (TableLayout)((Activity)context).findViewById(R.id.layoutTableHeader);
	tableLayoutFooter = (TableLayout)((Activity)context).findViewById(R.id.layoutTableFooter);

	String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.csvDetailsName;
	File csv = new File(filePath);
	String lastUpdated = new Date(csv.lastModified()).toString();
	String[] arrDate = lastUpdated.split(" ");
	lastUpdated = arrDate[0] + " " + arrDate[2] + " " + arrDate[3] + " " + arrDate[5];
	setFooter(lastUpdated);
  }

  private void setTitlebar() {
	switch (UIX) {
	  case Constants.UITerra:
		((Activity)context).setTitle("Spirale - Terra");
		break;
	  case Constants.UIRegion:
		((Activity)context).setTitle("Spirale - Regions");
		break;
	  case Constants.UICountryByRegion:
		((Activity)context).setTitle("Spirale - Country by Region");
		break;
	  case Constants.UICountry:
		((Activity)context).setTitle("Spirale - Country");
		break;
	  case Constants.UICase24Hour:
		((Activity)context).setTitle("Spirale - Cases 24 Hours");
		break;
	  case Constants.UIDeath24Hour:
		((Activity)context).setTitle("Spirale - Deaths 24 Hours");
		break;
	  case Constants.UITotalPrecentInfected:
		((Activity)context).setTitle("Spirale - Precentage Infected");
		break;
	  case Constants.UIInfectionsCurve:
		((Activity)context).setTitle("Spirale - Infections Curve");
		break;
	  case Constants.UITerraInfectionsCurve:
		((Activity)context).setTitle("Spirale - Terra Infections Curve");
		break;
	  case Constants.UIRNought:
		((Activity)context).setTitle("Spirale - rNought");
		break;
	  case Constants.UIRNought7:
		((Activity)context).setTitle("Spirale - rNought/7");
		break;
	  case Constants.UIRNought14:
		((Activity)context).setTitle("Spirale - rNought/14");
		break;
	  case Constants.UITerraRNought:
		((Activity)context).setTitle("Spirale - Terra rNought");
		break;
	  case Constants.UITerraRNought7:
		((Activity)context).setTitle("Spirale - Terra rNought/7");
		break;
	  case Constants.UITerraRNought14:
		((Activity)context).setTitle("Spirale - Terra rNought/14");
		break;
	  case Constants.UITerraTotalCases:
		((Activity)context).setTitle("Spirale - Terra Total Cases");
		break;
	  case Constants.UITerraTotalDeaths:
		((Activity)context).setTitle("Spirale - Terra Total Deaths");
		break;
	  case Constants.UITerraCasePerMillion:
		((Activity)context).setTitle("Spirale - Terra Case/Million");
		break;
	  case Constants.UITerraDeathPerMillion:
		((Activity)context).setTitle("Spirale - Terra Death/Million");
		break;
	  case Constants.UITerraCase24H:
		((Activity)context).setTitle("Spirale - Terra Case/24H");
		break;
	  case Constants.UITerraCase7D:
		((Activity)context).setTitle("Spirale - Terra Case/7D");
		break;
	  case Constants.UITerraDeath24H:
		((Activity)context).setTitle("Spirale - Terra Death/24H");
		break;
	  case Constants.UITerraDeath7D:
		((Activity)context).setTitle("Spirale - Terra Death/7D");
		break;
	  case Constants.UITerraTotalInfected:
		((Activity)context).setTitle("Spirale - Terra Total Infected");
		break;
	  case Constants.UITotalCase:
		((Activity)context).setTitle("Spirale - Total Cases");
		break;
	  case Constants.UITotalDeath:
		((Activity)context).setTitle("Spirale - Total Deaths");
		break;
	  case Constants.UICasePerMillion:
		((Activity)context).setTitle("Spirale - Case/Million");
		break;
	  case Constants.UIDeathPerMillion:
		((Activity)context).setTitle("Spirale - Death/Million");
		break;
	}
  }

  protected ArrayList<TableRow> populateTable(ArrayList<MetaField> metaFields) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    boolean bColourSwitch = true;
    for (final MetaField metaField: metaFields) {
      TableRow tableRow = new TableRow(context);
      LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      tableRow.setLayoutParams(tableRowParams);

      TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
      cellParams.weight = 9;
      TextView textViewKey = new TextView(context);
      textViewKey.setTextSize(18);
	  TextView textViewValue = new TextView(context);
      textViewKey.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View view) {
			if (metaField.underlineKey) {
			  if (metaField.UI.equals(Constants.UIRegion)) {
				new UIRegion(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UICountryByRegion)) {
				new UICountryByRegion(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UICountry)) {
				new UICountry(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UICase24Hour)) {
				new UICase24Hour(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIDeath24Hour)) {
				new UIDeath24Hour(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITotalPrecentInfected)) {
				new UITotalPrecentInfected(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIInfectionsCurve)) {
				new UIInfectionsCurve(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraInfectionsCurve)) {
				new UITerraInfectionsCurve(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIRNought)) {
				new UIRNought(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIRNought7)) {
				new UIRNought7(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIRNought14)) {
				new UIRNought14(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraRNought)) {
				new UITerraRNought(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraRNought7)) {
				new UITerraRNought7(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraRNought14)) {
				new UITerraRNought14(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraTotalCases)) {
				new UITerraTotalCases(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraTotalDeaths)) {
				new UITerraTotalDeaths(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraCasePerMillion)) {
				new UITerraCasePerMillion(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraDeathPerMillion)) {
				new UITerraDeathPerMillion(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraCase24H)) {
				new UITerraCase24H(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraCase7D)) {
				new UITerraCase7D(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraDeath24H)) {
				new UITerraDeath24H(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraDeath7D)) {
				new UITerraDeath7D(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITerraTotalInfected)) {
				new UITerraTotalInfected(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITotalCase)) {
				new UITotalCase(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UITotalDeath)) {
				new UITotalDeath(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UICasePerMillion)) {
				new UICasePerMillion(context, metaField.regionId, metaField.countryId);
			  }
			  if (metaField.UI.equals(Constants.UIDeathPerMillion)) {
				new UIDeathPerMillion(context, metaField.regionId, metaField.countryId);
			  }
			}
          }
        });
      textViewValue.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View view) {
			if (metaField.underlineValue) {

			}
          }
        });
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(metaField.key);
      textViewValue.setText(metaField.value);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);

	  if (metaField.underlineKey)
		textViewKey.setPaintFlags(textViewKey.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	  if (metaField.underlineValue)
		textViewValue.setPaintFlags(textViewValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

	  if (bColourSwitch) {
        bColourSwitch = !bColourSwitch; 
        tableRow.setBackgroundColor(Color.parseColor("#F7FAFD"));
	  } else {
        bColourSwitch = !bColourSwitch;
		tableRow.setBackgroundColor(Color.parseColor("#ECF8F6"));
	  }
      tableRows.add(tableRow);
	}
    return tableRows;
  }

  protected void setHeader(String keyDescription, String valueDescription) {
    TableRow tableRow = new TableRow(context);
    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    tableRow.setLayoutParams(tableRowParams);

    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 9;
    TextView textViewL = new TextView(context);
    TextView textViewR = new TextView(context);
    textViewL.setTextSize(18);
    textViewR.setTextSize(18);
    textViewL.setLayoutParams(cellParams);
    textViewR.setLayoutParams(cellParams);
    textViewL.setText(keyDescription);
    textViewL.setTypeface(null, Typeface.BOLD);
    textViewR.setText(valueDescription);
    textViewR.setTypeface(null, Typeface.BOLD);
    tableRow.addView(textViewL);
    tableRow.addView(textViewR);
    tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
    tableLayoutHeader.addView(tableRow);
  }

  protected void setTableLayout(ArrayList<TableRow> tableRows) {
    for (TableRow tableRow: tableRows) {
      tableLayout.addView(tableRow);
	}
  }
  private void setFooter(String lastUpdated) {
    TableRow tableRow = new TableRow(context);
    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    tableRow.setLayoutParams(tableRowParams);

    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 9;
    cellParams.gravity = Gravity.CENTER;
    TextView textView = new TextView(context);
    textView.setTextSize(18);
    textView.setLayoutParams(cellParams);
    textView.setText(lastUpdated);
    textView.setTypeface(null, Typeface.BOLD);
    textView.setGravity(Gravity.CENTER);
    tableRow.addView(textView);
    tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
    tableLayoutFooter.addView(tableRow);
  }
}
