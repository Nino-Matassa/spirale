package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;

public class UIOverview extends UI {
  protected Context context = null;
  protected Activity activity = null;
  private String dialogMessage = null;

  public UIOverview(Context context, Activity activity, String dialogMessage) {
	super(context, activity, dialogMessage);
	this.context = context;
	this.activity = activity;
	this.dialogMessage = dialogMessage;
  }
}
