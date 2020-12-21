package ie.matassa.nino.spirale;
import android.database.sqlite.*;

public class Constants
{
	public static final String DataSource = "WHO (https://covid19.who.int/info)";
	public static final String CsvDetailsURL = "https://covid19.who.int/WHO-COVID-19-global-data.csv";
	public static final String CsvOverviewURL = "https://covid19.who.int/WHO-COVID-19-global-table-data.csv";
	public static final String dbName = null; //"dbSpirale"; // null = keep it in memory
	public static final String csvDetailsName = "DetailsTable.csv";
	public static final String csvOverviewName = "OverviewTable.csv";
	
	public static final String[] Urls = { CsvOverviewURL, CsvDetailsURL };
	public static final String[] Names = {csvOverviewName, csvDetailsName };
	
	public static final int dbVersion = 1;
}

