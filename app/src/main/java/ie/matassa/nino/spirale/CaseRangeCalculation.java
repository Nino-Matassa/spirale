package ie.matassa.nino.spirale;
import java.util.*;
import android.database.*;
import android.icu.text.*;
import android.util.*;

public class CaseRangeCalculation {
  public ArrayList<CaseRangeTotal> calculate(Cursor cursor, int length) {
	ArrayList<CaseRangeTotal> listCaseTotal = new ArrayList<CaseRangeTotal>();

	// Populate listCaseTotal
	cursor.moveToFirst();
	do {
	  CaseRangeTotal value = new CaseRangeTotal();
	  String date = cursor.getString(cursor.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  value.date = date;
	  value.value = cursor.getInt(cursor.getColumnIndex("CaseX"));
	  listCaseTotal.add(value);
	} while(cursor.moveToNext());

	// Populate listCaseTotal total values
	for (int outer = 0; outer < listCaseTotal.size() - 1; outer++) {
	  int total = 0;
	  for (int inner = 0; inner < length; inner++) {
		if (outer + length > listCaseTotal.size())
		  break;
		total += listCaseTotal.get(outer + inner).value;
	  }
	  listCaseTotal.get(outer).total = total;
	}
	return listCaseTotal;
  }
  
}
