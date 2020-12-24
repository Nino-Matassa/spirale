package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;

public class UIMessage {
  public static void toast(final Context context, final String text, final int length) {
	new Handler(Looper.getMainLooper()).post(new Runnable() {
		@Override
		public void run() {
		  Toast.makeText(context, text, length).show();
		}
	  });
  }

  private static AlertDialog.Builder builder = null;
  private static AlertDialog alertDialog = null;

  public static void notificationMessage(final Context context, final Activity activity, final String msg) {
    activity.runOnUiThread(new Runnable() {
		@Override
		public void run() {
		  if(msg == null) {
			alertDialog.dismiss();
			return;
		  }
		  if (builder == null) {
			builder = new AlertDialog.Builder(context);
			alertDialog = builder.create();
		  } else {
			alertDialog.dismiss(); // It's already been run
		  }
		  alertDialog.setMessage(msg);
		  alertDialog.show();
        }
      });
  }
}