package ie.matassa.nino.spirale;

import android.database.*;
import java.util.*;

public class RNoughtCalculation {

  public RNoughtCalculation() {}

  public Double calculate(int current, int previous) {
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
	  value.date = cursor.getString(cursor.getColumnIndex("Date"));
	  value.newCases = cursor.getInt(cursor.getColumnIndex("NewCases"));
	  listRNought.add(value);
	} while(cursor.moveToNext());

	// Populate listRNought rNought values
	for (int outer = 0; outer < listRNought.size()-1; outer++) {
	  int current = listRNought.get(outer).newCases;
	  int previous = listRNought.get(outer+1).newCases;
	  listRNought.get(outer).rNought = calculate(current, previous);
	}
	
	// Populate listRNought average values
	for (int outer = 0; outer < listRNought.size()-1; outer++) {
	  double rNought = 0.0;
	  for(int inner = 0; inner < length; inner++) {
		if(outer + length > listRNought.size())
		  break;
		rNought += listRNought.get(outer + inner).rNought;
	  }
	  listRNought.get(outer).average = rNought/length;
	}
	return listRNought;
  }
}

