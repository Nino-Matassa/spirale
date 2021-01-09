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
  protected Context context = null;

  private TableLayout tableLayout = null;
  private TableLayout tableLayoutHeader = null;
  private TableLayout tableLayoutFooter = null;
  protected SQLiteDatabase db = null;
  private Vibrator vibrator = null;

  public UI(Context context) {
	this.context = context;

	getDataFiles();

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
			  UIMessage.notificationMessage(context, "Busy");
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
			  if(metaField.UI.equals(Constants.UIDeath24Hour)) {
				new UIDeath24Hour(context, metaField.regionId, metaField.countryId);
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

  private static boolean bDownloadRequest = false;
  private static Thread thread = null;
  public void getDataFiles() {
	thread = new Thread(new Runnable() {
		@Override 
		public void run() {
		  CSV csv = new CSV(context);
		  for (int queue = 0; queue < Constants.Urls.length; queue++) {
			bDownloadRequest = csv.downloadUrlRequest(Constants.Urls[queue], Constants.Names[queue]);
			if (bDownloadRequest && Constants.Urls[queue].equals(Constants.CsvOverviewURL)) {
			  csv.generateDatabaseTable(Constants.csvOverviewName);  
			}
			if (bDownloadRequest && Constants.Urls[queue].equals(Constants.CsvDetailsURL)) {
			  csv.generateDatabaseTable(Constants.csvDetailsName);
			}
		  }
		  if (!Database.databaseExists()) {
			csv.generateDatabaseTable(Constants.csvOverviewName); 
			csv.generateDatabaseTable(Constants.csvDetailsName);
			new GenerateTablesEtc(context);
		  }
		}
	  });
	thread.start();
	try {
	  thread.join(); 
	} catch (InterruptedException e) {
	  Log.d("getDataFiles", e.toString());
	} finally {
	  UIMessage.notificationMessage(context, null);
	}
  }
}
