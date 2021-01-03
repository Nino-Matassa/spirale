package ie.matassa.nino.spirale;
import android.content.*;
import android.os.*;

public class BusyBee extends AsyncTask<Void, Void, Void> {
  private Context context = null;

  public BusyBee(Context context) {
	this.context = context;
  }
  
  @Override
  protected void onPreExecute() {
	UIMessage.notificationMessage(context, "Busy!");
	super.onPreExecute();
  }

  @Override
  protected Void doInBackground(Void[] p1) {
	// TODO: Implement this method
	return null;
  }

  @Override
  protected void onPostExecute(Void result) {
	UIMessage.notificationMessage(context, null);
	super.onPostExecute(result);
  }
}
