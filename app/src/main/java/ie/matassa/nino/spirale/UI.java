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

public class UI {
  protected Context context = null;
  protected Activity activity = null;
  
  private TableLayout tableLayout = null;
  private TableLayout tableLayoutHeader = null;
  private TableLayout tableLayoutFooter = null;
  protected SQLiteDatabase db = null;
  private ProgressDialog progressDialog = null;
  private Vibrator vibrator = null;
  private String dialogMessage = null;
  
  public UI(Context context, Activity activity, String dialogMessage) {
	this.context = context;
	this.activity = activity;
	this.dialogMessage = dialogMessage;
	progressDialog = new ProgressDialog(activity);
	
	vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
    vibrator.vibrate(80);

	db = Database.getInstance(context);
    ((Activity)context).setContentView(R.layout.table_layout);
    tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
    tableLayoutHeader = (TableLayout)((Activity)context).findViewById(R.id.layoutTableHeader);
    tableLayoutFooter = (TableLayout)((Activity)context).findViewById(R.id.layoutTableFooter);
  }
  protected ArrayList<TableRow> getTableRows(ArrayList<MetaTable> metaTable) {
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
    boolean bColourSwitch = true;
    for(final MetaTable mt: metaTable) {
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
		  public void onClick(View p1) {
			onClickListenerFired(p1, mt);
          }
        });
      textViewValue.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View p1) {
			onClickListenerFired(p1, mt);
          }
        });
      textViewValue.setTextSize(18);
      textViewKey.setLayoutParams(cellParams);
      textViewValue.setLayoutParams(cellParams);
      textViewKey.setText(mt.key);
      textViewValue.setText(mt.value);
      tableRow.addView(textViewKey);
      tableRow.addView(textViewValue);
      if(bColourSwitch) {
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
    for(TableRow tableRow: tableRows) {
      tableLayout.addView(tableRow);
	}
  }

  private void onClickListenerFired(View p1, MetaTable mt) {
  }

  protected void setFooter(String description) {
    TableRow tableRow = new TableRow(context);
    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    tableRow.setLayoutParams(tableRowParams);

    TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
    cellParams.weight = 9;
    TextView textView = new TextView(context);
    textView.setTextSize(18);
    textView.setLayoutParams(cellParams);
    textView.setText(description);
    textView.setTypeface(null, Typeface.BOLD);
    textView.setGravity(Gravity.CENTER);
    tableRow.addView(textView);
    tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
    tableLayoutFooter.addView(tableRow);
  }
  
}
