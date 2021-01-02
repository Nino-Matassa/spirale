package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;

public class GenerateTables {
  private Context context = null;
  private SQLiteDatabase db = null;

  public GenerateTables(Context context) {
	this.context = context;
	db = Database.getInstance(context);
	generateRegion();
	generateCountry();
  }

  private void generateCountry() {
	String sql = "select distinct Overview.Country, Region.Id from Overview join Detail on Overview.Country = Detail.Country join Region on Overview.Region = Region.Region";
	Cursor cCountry = db.rawQuery(sql, null);
    cCountry.moveToFirst();
    do {
	  String Country = cCountry.getString(cCountry.getColumnIndex("Country"));
	  Integer Id = cCountry.getInt(cCountry.getColumnIndex("Id"));
	  ContentValues values = new ContentValues();
	  values.put("Country", Country);
	  values.put("FK_Region", Id);
	  Long pk = db.insert("Country", null, values);
	} while(cCountry.moveToNext());
  }

  private void generateRegion() {
	String sql = "insert into Region(Region) select distinct Region from Overview where Region != 'Terra'";
	db.execSQL(sql);
  }
}
