package ie.matassa.nino.spirale;
import android.content.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.util.*;
import android.database.sqlite.*;

public class CSV {
	private String filePath = null;
	//private Context context = null;
	private List rows = null;
	private SQLiteDatabase db = null;
	private boolean firstRowRead = false;
	private boolean secondRowRead = false;

	public CSV(Context context, String csvFileName) {
		//this.context = context;
		db = Database.getInstance(context);
		filePath = context.getFilesDir().getPath().toString() + "/" + csvFileName;
		rows = new ArrayList<String>();
		readCSV();
	}

	private void readCSV() {
		String line = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

			while ((line = bufferedReader.readLine()) != null) {
				String[] row = line.split(",");
				rows.add(row);
			}
			bufferedReader.close();
		}
		catch (IOException e) {  Log.d("readCSV", e.toString());}
	}

	public boolean populateTableOverview() {
		try {
			for (String[] row: rows) {
				// Ignore the first row
				if (!firstRowRead) {
					firstRowRead = true;
					continue;
				}
				
//			ContentValues values = new ContentValues();
//			values.put(Constants.fkRegion, fkRegion);
//			values.put(Constants.colCountryCode, countryCode);
//			fkCountry = db.insert(Constants.tblCountry, null, values);
				int index = 0;
				String Region = row[index++];
				String Country = row[index++];
				Integer TotalCase = Integer.parseInt(row[index++]);
				Double CasePerMillion = Double.parseDouble(row[index++]);
				Integer Case7Day = Integer.parseInt(row[index++]);
				Integer Case24Hour = Integer.parseInt(row[index++]);
				Integer TotalDeath = Integer.parseInt(row[index++]);
				Double DeathPerMillion = Double.parseDouble(row[index++]);
				Integer Death7Day = Integer.parseInt(row[index++]);
				Integer Death24Hour = Integer.parseInt(row[index++]);
				String Source = null;
				if(secondRowRead) Source = row[index];
				// 2nd row populate country as Terra
				if (!secondRowRead && firstRowRead) {
					secondRowRead = true;
					Source = "N/A";
					Region = "N/A";
				}
			}
		}
		catch (NumberFormatException e) {
			Log.d("populateTableOverview", e.toString());
		}
		return true;
	}

	public boolean populateTableDetails() {
		return true;
	}
}
