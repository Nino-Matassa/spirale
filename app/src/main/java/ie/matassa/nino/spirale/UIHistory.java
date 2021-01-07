package ie.matassa.nino.spirale;
import android.content.*;
import android.app.*;

public class UIHistory {
  private int regionId = 0;
  private int countryId = 0;
  private String UIX = null;

  public UIHistory(int regionId, int countryId, String uiX) {
	this.regionId = regionId;
	this.countryId = countryId;
	this.UIX = uiX;
  }

  public int getRegionId() {
	return regionId;
  }

  public int getCountryId() {
	return countryId;
  }

  public String getUIX() {
	return UIX;
  }
}
