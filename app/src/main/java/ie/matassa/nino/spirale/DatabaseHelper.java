package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constants.dbName, null, Constants.dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) throws SQLiteFullException {
		db.execSQL(sqlTableOverview);
		db.execSQL(sqlTableRegion);
		db.execSQL(sqlTableCountry);
		db.execSQL(sqlTableDetail);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteFullException {
		// Unused because database is run in memory
	}
	
	private String sqlTableOverview = null;
	private String sqlTableRegion =
	"create table Region ("
	+ "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ "Continent TEXT NOT NULL"
	+ ");";
	private String sqlTableCountry = null;
	private String sqlTableDetail = null;
	
//	private String createTableRegion = "create table " + Constants.tblRegion + 
//	"(" + Constants.pkId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
//	Constants.colContinent + " TEXT NOT NULL);";
}
