package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.util.*;

public class Database {
  private Database() {}
  private static SQLiteDatabase instance = null;

  public static void setInstanceToNull() { // drop database
	//instance = null; // Let java control the memory in case the db is dropped while in the background
  }

  public static SQLiteDatabase getInstance(Context context) {
	if (instance == null) {
	  instance = new DatabaseHelper(context).getWritableDatabase();
	}
	return instance;
  }
  
  public static boolean databaseExists() {
	if (instance == null)
	  return false;
	else
	  return true;
  }
}
