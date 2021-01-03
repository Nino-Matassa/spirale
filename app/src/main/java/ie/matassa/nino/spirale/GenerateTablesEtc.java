package ie.matassa.nino.spirale;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;

public class GenerateTablesEtc {
  private Context context = null;
  private SQLiteDatabase db = null;

  public GenerateTablesEtc(Context context) {
	this.context = context;
	db = Database.getInstance(context);
	generateRegion();
	generateCountry();
  }

  private void generateCountry() {
	String sql = "insert into Country(Country, FK_Region) select distinct Overview.Country, Region.Id from Overview join Detail on Overview.Country = Detail.Country join Region on Overview.Region = Region.Region";
	db.execSQL(sql);
  }

  private void generateRegion() {
	String sql = "insert into Region(Region) select distinct Region from Overview where Region != 'Terra'";
	db.execSQL(sql);
  }
}

/*


*/
