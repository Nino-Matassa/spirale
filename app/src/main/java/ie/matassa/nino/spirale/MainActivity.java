package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import java.io.*;
import java.nio.channels.*;
import java.net.*;
import java.text.*;
import java.util.*;
import org.apache.http.impl.client.*;
import java.sql.*;
import android.text.format.*;
import android.database.sqlite.*;


public class MainActivity extends Activity {

  public static Activity activity = null;
  private TextView view = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	activity = this;
	setContentView(R.layout.main);
	view = findViewById(R.id.mainTextID);
	CSV.notificationMessage(MainActivity.this, "Checking For Updates");
//	String displayText = (String) view.getText();
//	view.setText(displayText);
	
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {
		public void run() {
		  	new CSV(MainActivity.this).getDataFiles();
		}
	  }, 500);
  }

  @Override
  protected void onResume() {
	super.onResume();
  }
}


