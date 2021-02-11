package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.view.*;

public class UIMessage {
  public static void toast(final Context context, final String text, final int length) {
	new Handler(Looper.getMainLooper()).post(new Runnable() {
		@Override
		public void run() {
		  Toast.makeText(context, text, length).show();
		}
	  });
  }
  
  public static void toast(final Context context, final String text) {
	new Handler(Looper.getMainLooper()).post(new Runnable() {
		@Override
		public void run() {
		  Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		}
	  });
  }

  private static AlertDialog.Builder builder = null;
  private static AlertDialog alertDialog = null;

  public static void notificationMessage(final Context context, final String msg) {
	((Activity)context).runOnUiThread(new Runnable() {
		@Override
		public void run() {
		  if(msg == null) {
			alertDialog.dismiss();
			return;
		  }
		  if (builder == null) {
			builder = new AlertDialog.Builder(context);
			alertDialog = builder.create();
//			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//			final View dialogView = inflater.inflate(R.layout.spinner, null);
//			builder.setView(dialogView);

			//Spinner checkInProviders = (Spinner) dialogView .findViewById(R.id.spinner);
		  }
		  alertDialog.setMessage(msg);
		  alertDialog.show();
        }
      });
  }
}
