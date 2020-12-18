package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constants.dbName, null/*CursorFactory*/, Constants.dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) throws SQLiteFullException {
		db.execSQL(sqlTableRegion);
		db.execSQL(sqlTableOverview);
		db.execSQL(sqlTableCountry);
		db.execSQL(sqlTableDetail);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteFullException {
		// Unused because database is run in memory
	}
	
	private String sqlTableRegion =
	"create table Region ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "Continent TEXT NOT NULL"
	+ ");";
	private String sqlTableOverview =
	"create table Overview ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "FK_Region INT NOT NULL, "
	+ "Country TEXT NOT NULL, "
	+ "FOREIGN KEY (FK_Region) REFERENCES Region(Id)"
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
