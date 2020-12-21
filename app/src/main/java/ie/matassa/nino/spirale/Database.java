package ie.matassa.nino.spirale;
import android.database.sqlite.*;
import java.io.*;
import android.content.*;

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
	if (instance.equals(null))
	  return false;
	else
	  return true;
  }
}
