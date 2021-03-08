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
  
  private static AlertDialog.Builder builder = null;
  private static AlertDialog alertDialog = null;

  public static void notificationMessage(final Context context, final String msg) {
	((Activity)context).runOnUiThread(new Runnable() {
		@Override
		public void run() {
		  if(msg == null) {
			alertDialog.dismiss();
			if(MainActivity.bCallUITerra) {
			  MainActivity.bCallUITerra = false;
			  new UITerra(context);
			}
			return;
		  }
		  if (builder == null) {
			builder = new AlertDialog.Builder(context);
			alertDialog = builder.create();
		  }
		  alertDialog.setMessage(msg);
		  alertDialog.show();
        }
      });
  }
  
  public static void informationBox(final Context context, final String msg) {
	((Activity)context).runOnUiThread(new Runnable() {
		@Override
		public void run() {
		  if (builder == null) {
			builder = new AlertDialog.Builder(context);
			alertDialog = builder.create();
		  }
		  alertDialog.setMessage(msg);
		  TextView textView = (TextView)alertDialog.findViewById(android.R.id.message);
		  textView.setGravity(Gravity.CENTER);
		  alertDialog.show();
        }
      });
  }
  
  public static String abbreviate(String text, int length) {
	return text.length() < Constants.abbreviate ? text:text.substring(0, Constants.abbreviate-3) + "...";
  }
}
