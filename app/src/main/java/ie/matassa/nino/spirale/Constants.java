package ie.matassa.nino.spirale;
import android.database.sqlite.*;

public class Constants {
  public static final String DataSource = "WHO (https://covid19.who.int/info)";
  public static final String CsvDetailsURL = "https://covid19.who.int/WHO-COVID-19-global-data.csv";
  public static final String CsvOverviewURL = "https://covid19.who.int/WHO-COVID-19-global-table-data.csv";
  public static final String dbName = null; //"dbSpiral";
  public static final String csvDetailsName = "DetailsTable.csv";
  public static final String csvOverviewName = "OverviewTable.csv";

  public static final String rNought = "R\u2080";
  public static final String proportional = "\u221D";
  public static final String forEach = "\u2200";
  public static final String house = "\u2302";
  public static final String approximately = "\u2248";
  
  public static final String roman100000 = "C\u0305"; // Roman numeral for 100,000
  public static final int oneHundredThousand = 100000;
  
  public static final String beta = "\u03D0";
  
  public static final String[] Urls = { CsvOverviewURL, CsvDetailsURL };
  public static final String[] Names = {csvOverviewName, csvDetailsName };

  public static final int dbVersion = 1;
  public static final int seven = 7;
  public static final int moonPhase = 27; // 0 is a number....
  public static final int abbreviate = 99;//15; Needs to be based on dynamic column width...
  public static final int delayMilliSeconds = 50;

  public static final String UITerra = "UITerra";
  public static final String UIRegion = "UIRegion";
  public static final String UICountry = "UICountry";
  public static final String UICountryByRegion = "UICountryByRegion";
  public static final String UICase24Hour = "UICase24Hour";
  public static final String UIDeath24Hour = "UIDeath24Hour";
  public static final String UITotalPrecentInfected = "UITotalPrecentInfected";
  public static final String UIRNought = "UIRNought";
  public static final String UITerraRNought = "UITerraRNought";
  public static final String UITotalCase = "UITotalCase";
  public static final String UITotalDeath = "UITotalDeath";
  public static final String UICasePerX = "UICasePerX";
  public static final String UIDeathPerX = "UIDeathPerX";
  public static final String UICase7Day = "UICase7Day";
  public static final String UIDeath7Day = "UIDeath7Day";
  public static final String UITerraTotalCases = "UITerraTotalCases";
  public static final String UITerraTotalDeaths = "UITerraTotalDeaths";
  public static final String UITerraCase24H = "UITerraCase24H";
  public static final String UITerraCase7D = "UITerraCase7D";
  public static final String UITerraDeath24H = "UITerraDeath24H";
  public static final String UITerraDeath7D = "UITerraDeath7D";
  public static final String UITerraTotalInfected = "UITerraTotalInfected";
  public static final String UITerraActiveCases = "UITerraActiveCases";
  public static final String UIActiveCases = "UIActiveCases";
  public static final String UITerraActiveCasesPerX = "UITerraActiveCasesPerX";
  public static final String UIRHSTerraRNought = "UIRHSTerraRNought";
  public static final String UIRHSTerraActiveCases = "UIRHSTerraActiveCases";
  public static final String UIRHSTerraActiveCasesPerX = "UIRHSTerraActiveCasesPerX";
}

