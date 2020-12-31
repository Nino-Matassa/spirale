package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;

public class GenerateTables {
  private Context context = null;
  private SQLiteDatabase db = null;

  public GenerateTables(Context context) {
	this.context = context;
	db = Database.getInstance(context);
	generateRegion();
  }

  private void generateRegion() {
	String sql = "insert into Region(Region) select distinct Region from Overview where Region != 'Terra'";
	db.execSQL(sql);
  }
}
