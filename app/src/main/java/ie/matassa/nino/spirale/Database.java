package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.util.*;

public class Database {
  private Database() {}
  private static SQLiteDatabase instance = null;

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
  
  public static void deleteDatabase() {
	instance = null;
  }
}
