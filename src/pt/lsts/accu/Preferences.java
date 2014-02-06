package pt.lsts.accu;

import pt.up.fe.dceg.accu.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   	
    	addPreferencesFromResource(R.xml.preferences);
    }
}
