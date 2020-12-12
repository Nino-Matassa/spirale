package ie.matassa.nino.spirale;

import android.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.content.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

	@Override
	public Loader<String> onCreateLoader(int p1, Bundle p2) {
		// TODO: Implement this method
		return null;
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
