package ie.matassa.nino.spirale;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

	@Override
	public Loader<String> onCreateLoader(int id, Bundle urls) {
		
		return new AsyncTaskLoader<String>(this) {
			private boolean downloadMainDB = false;
			private boolean downloadOverviewDB = false;
			
			private String resultString = "";

			@Override
			public String loadInBackground() {
				
				return resultString;
			}
			@Override
			protected void onStartLoading(){
				// progress bar, etc.
				forceLoad();
			}
			
			private boolean downloadCSV(String csvFileName, String csvUrl) {
				String filePath = MainActivity.this.getFilesDir().getPath().toString() + "/" + csvFileName;
				File file = new File(filePath);
				if(file.exists()) file.delete();

				try {
					ReadableByteChannel readChannel = Channels.newChannel(new URL(csvUrl).openStream());
					FileOutputStream fileOS = new FileOutputStream(filePath);
					FileChannel writeChannel = fileOS.getChannel();
					writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
					writeChannel.close();
					readChannel.close();
				}
				catch(IOException e) {
					Log.d("downloadCSV", e.toString());
					return false;
				}
				return true;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<String> p1, String p2) {
		// TODO: Implement this method
	}

	@Override
	public void onLoaderReset(Loader<String> p1) {
		// TODO: Implement this method
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		super.onBackPressed();
	}
}
