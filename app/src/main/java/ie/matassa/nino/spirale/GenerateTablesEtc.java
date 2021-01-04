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
	populateOverviewFK_Region();
	populateDetailFK_Country();
  }

  private void populateDetailFK_Country() {
	String sqlCountry = "select Id, Country from Country";
	Cursor cCountry = db.rawQuery(sqlCountry, null);
	cCountry.moveToFirst();
	do {
	  int Id = cCountry.getInt(cCountry.getColumnIndex("Id"));
	  String Country = cCountry.getString(cCountry.getColumnIndex("Country"));
	  Country = Country.replace("'", "''"); //sqlUpdate = "update Detail set FK_Country = 207 where Country = 'Lao People's Democratic Republic'" fails on execution
	  String sqlUpdate = "update Detail set FK_Country = #1 where Country = '#2'";
	  sqlUpdate = sqlUpdate.replace("#1", String.valueOf(Id));
	  sqlUpdate = sqlUpdate.replace("#2", Country);
	  db.rawQuery(sqlUpdate, null);
	} while(cCountry.moveToNext());
  }

  private void populateOverviewFK_Region() {
	String sqlRegion = "select Id, Region from Region";
	Cursor cRegion = db.rawQuery(sqlRegion, null);
	cRegion.moveToFirst();
	do {
	  int Id = cRegion.getInt(cRegion.getColumnIndex("Id"));
	  String Region = cRegion.getString(cRegion.getColumnIndex("Region"));
	  String sqlUpdate = "update Overview set FK_Region = #1 where Region = '#2'";
	  sqlUpdate = sqlUpdate.replace("#1", String.valueOf(Id));
	  sqlUpdate = sqlUpdate.replace("#2", Region);
	  db.rawQuery(sqlUpdate, null);
	} while(cRegion.moveToNext());
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


