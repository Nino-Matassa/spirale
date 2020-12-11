package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {

	@Override
	public void onCreate(SQLiteDatabase p1) {
		// TODO: Implement this method
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3) {
		// TODO: Implement this method
	}
	
	public DatabaseHelper(Context context) {
		super(context, Constants.dbName, null, 1);
	}
}
