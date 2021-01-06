package ie.matassa.nino.spirale;
import android.content.*;
import android.app.*;

public class UIHistory {
  private Context context = null;
  private int regionId = 0;
  private int countryId = 0;
  private String UI = null;

  public UIHistory(Context context, int regionId, int countryId, String ui) {
	this.context = context;
	this.regionId = regionId;
	this.countryId = countryId;
	UI = ui;
  }

  public String getUI() {
	return UI;
  }
}
