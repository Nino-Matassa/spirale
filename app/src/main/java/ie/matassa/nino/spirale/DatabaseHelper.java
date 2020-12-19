package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;
import android.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constants.dbName, null/*CursorFactory*/, Constants.dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(sqlTableRegion);
			db.execSQL(sqlTableOverview);
			db.execSQL(sqlTableCountry);
			db.execSQL(sqlTableDetail);
		}
		catch (SQLException e) {
			Log.d("DatabaseHelper.onCreate", e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteFullException {
		// Unused because database is run in memory
	}
	
	private String sqlTableRegion =
	"create table Region ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "Region TEXT NOT NULL"
	+ ");";
	private String sqlTableOverview =
	"create table Overview ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	//+ "FK_Region INT NOT NULL, "
	+ "Region TEXT NOT NULL, "
	+ "Country TEXT NOT NULL, "
	+ "TotalCase INT NOT NULL, "
	+ "CasePerMillion DECIMAL(10, 5) NOT NULL, "
	+ "Case7Day INT NOT NULL, "
	+ "Case24Hour INT NOT NULL, "
	+ "TotalDeath INT NOT NULL, "
	+ "DeathPerMillion DECIMAL(10, 5) NOT NULL, "
	+ "Death7Day INT NOT NULL, "
	+ "Death24Hour INT NOT NULL, "
	+ "Source TEXT NOT NULL"
	//+ "FOREIGN KEY (FK_Region) REFERENCES Region(Id)"
	+ ");";
	private String sqlTableCountry =
	"create table Country ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "FK_Region INT NOT NULL, "
	+ "Country TEXT NOT NULL, "
	+ "FOREIGN KEY (FK_Region) REFERENCES Region(Id)"
	+ ");";
	private String sqlTableDetail =
	"create table Detail ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "FK_Country INT NOT NULL, "
	+ "Country TEXT NOT NULL, "
	+ "FOREIGN KEY (FK_Country) REFERENCES Country(Id)"
	+ ");";
}
