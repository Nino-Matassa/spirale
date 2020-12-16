package ie.matassa.nino.spirale;
import android.content.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.util.*;

public class CSV {
	private String filePath = null;
	
	public CSV(Context context, String csvFileName) {
		filePath = context.getFilesDir().getPath().toString() + "/" + csvFileName;
	}
	
	public List readCSV() {
		List rows = new ArrayList<String>();
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
		return rows;
	}
}
