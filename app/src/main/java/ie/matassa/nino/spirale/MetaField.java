package ie.matassa.nino.spirale;

public class MetaField {
  public String key = null;
  public String value = null;
  public boolean underlineKey = false;
  public boolean underlineValue = false;
  public int regionId = 0;
  public int countryId = 0;
  public String UI = Constants.UICountry;

  public MetaField(int regionId, int countryId, String UI) {
	this.regionId = regionId;
	this.countryId = countryId;
	this.UI = UI;
  }
  
  public MetaField() {}
}
