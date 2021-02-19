package ie.matassa.nino.spirale;

import android.database.*;
import java.util.*;
import android.icu.text.*;
import android.util.*;

public class RNoughtCalculation {

  public RNoughtCalculation() {}

  private Double calculate(int current, int previous) {
	Double rNought = 0.0;

	if (current > 0 && previous > 0) {
	  rNought = current / (double)previous;
	} else if (current > 0 && previous == 0) {
	  current = 1;
	} else {
	  rNought = 0.0;
	} 

	return rNought;
  }

  public ArrayList<RNoughtAverage> calculate(Cursor cursor, int length) {
	ArrayList<RNoughtAverage> listRNought = new ArrayList<RNoughtAverage>();

	// Populate listRNought
	cursor.moveToFirst();
	do {
	  RNoughtAverage value = new RNoughtAverage();
	  String date = cursor.getString(cursor.getColumnIndex("Date"));
	  try {
		date = new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
		String[] arrDate = date.split(" ");
		date = arrDate[0] + " " + arrDate[1] + " " + arrDate[2] + " " + arrDate[5];
	  } catch (Exception e) {
		Log.d(Constants.UICase24Hour, e.toString());
	  }
	  value.date = date;
	  value.newCase = cursor.getInt(cursor.getColumnIndex("CaseX"));
	  listRNought.add(value);
	} while(cursor.moveToNext());

	// Populate listRNought rNought values
	for (int outer = 0; outer < listRNought.size() - 1; outer++) {
	  int current = listRNought.get(outer).newCase;
	  int previous = listRNought.get(outer + 1).newCase;
	  listRNought.get(outer).rNought = calculate(current, previous);
	}

	// Populate listRNought average values
	for (int outer = 0; outer < listRNought.size() - 1; outer++) {
	  double rNought = 0.0;
	  for (int inner = 0; inner < length; inner++) {
		if (outer + length > listRNought.size())
		  break;
		rNought += listRNought.get(outer + inner).rNought;
	  }
	  listRNought.get(outer).average = rNought / length;
	}
	return listRNought;
  }
}

