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
		String Region = null;
		String Country = null;
		Integer TotalCase = 0;
		Double CasePerMillion = 0.0;
		Integer Case7Day = 0;
		Integer Case24Hour = 0;
		Integer TotalDeath = 0;
		Double DeathPerMillion = 0.0;
		Integer Death7Day = 0;
		Integer Death24Hour = 0;
		String Source = null;
		try {
			for (String[] row: rows) {
				// Ignore the first row
				if (!firstRowRead) {
					firstRowRead = true;
					continue;
				}
				int index = 0;
				Country = row[index++];
				if(Country.indexOf("\"") > -1)
					Country += ", " + row[index++];
				Region = row[index++];
				if(Region.equals("Other")) continue;
				TotalCase = Integer.parseInt(row[index++]);
				CasePerMillion = Double.parseDouble(row[index++]);
				Case7Day = Integer.parseInt(row[index++]);
				Case24Hour = Integer.parseInt(row[index++]);
				TotalDeath = Integer.parseInt(row[index++]);
				DeathPerMillion = Double.parseDouble(row[index++]);
				Death7Day = Integer.parseInt(row[index++]);
				Death24Hour = Integer.parseInt(row[index++]);
				Source = null;
				if(secondRowRead) Source = row[index];
				// 2nd row populate country as Terra
				if (!secondRowRead && firstRowRead) {
					secondRowRead = true;
					Source = "N/A";
					Region = "Terra";
				}
				ContentValues values = new ContentValues();
				values.put("Country", Country);
				values.put("Region", Region);
				values.put("TotalCase", TotalCase);
				values.put("CasePerMillion", CasePerMillion);
				values.put("Case7Day", Case7Day);
				values.put("Case24Hour", Case24Hour);
				values.put("TotalDeath", TotalDeath);
				values.put("DeathPerMillion", DeathPerMillion);
				values.put("Death7Day", Death7Day);
				values.put("Death24Hour", Death24Hour);
				values.put("Source", Source);
				Long Id = db.insert("Overview", null, values);
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
