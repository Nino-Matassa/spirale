package ie.matassa.nino.spirale;
import android.database.sqlite.*;

public class Constants {
  public static final String DataSource = "WHO (https://covid19.who.int/info)";
  public static final String CsvDetailsURL = "https://covid19.who.int/WHO-COVID-19-global-data.csv";
  public static final String CsvOverviewURL = "https://covid19.who.int/WHO-COVID-19-global-table-data.csv";
  public static final String dbName = null;
  public static final String csvDetailsName = "DetailsTable.csv";
  public static final String csvOverviewName = "OverviewTable.csv";

  public static final String[] Urls = { CsvOverviewURL, CsvDetailsURL };
  public static final String[] Names = {csvOverviewName, csvDetailsName };

  public static final int dbVersion = 1;
  public static final int oneMillion = 1000000;
  public static final int seven = 7;
  public static final int fourteen = 14;
  
  
  public static final String UITerra = "UITerra";
  public static final String UIRegion = "UIRegion";
  public static final String UICountry = "UICountry";
  public static final String UICountryByRegion = "UICountryByRegion";
  public static final String UICase24Hour = "UICase24Hour";
  public static final String UIDeath24Hour = "UIDeath24Hour";
  public static final String UITotalPrecentInfected = "UITotalPrecentInfected";
  public static final String UIInfectionsCurve = "UIInfectionsCurve";
  public static final String UITerraInfectionsCurve = "UITerraInfectionsCurve";
  public static final String UIRNought = "UIRNought";
  public static final String UITerraRNought = "UITerraRNought";
  public static final String UIRNought7 = "UIRNought7";
  public static final String UIRNought14 = "UIRNought14";
  public static final String UITerraRNought7 = "UITerraRNought7";
  public static final String UITerraRNought14 = "UITerraRNought14";
  public static final String UITerraTotalCases = "UITerraTotalCases";
  public static final String UITerraCasePerMillion = "UITerraCasePerMillion";
  public static final String UITerraTotalDeaths = "UITerraTotalDeaths";
  public static final String UITerraDeathPerMillion = "UITerraDeathCasePerMillion";
  public static final String UITerraCase24H = "UITerraCase24H";
  public static final String UITerraCase7D = "UITerraCase7D";
}

