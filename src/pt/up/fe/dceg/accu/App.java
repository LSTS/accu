package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.neptus.imc.IMCDefinition;
import android.app.Application;

/**
 * Class extending application that does a single startup for the application needed to initialize
 * ACCU state object
 * @author sharp
 *
 */
public class App extends Application 
{	
	@Override
	public void onCreate()
	{
		super.onCreate();

		IMCDefinition.getInstance(getResources().openRawResource(R.raw.imc));

		// Sequence of calls needed to properly initialize ACCU
		Accu.getInstance(this);
		Accu.getInstance().load();
		Accu.getInstance().start();
        System.out.println("Global ACCU Object Initialized"); 
        
        
        //FIXME jqcorreia
    	// For now theme setting must be done here because it needs an activity restart

		// Do it before setContentView()
   	}
}
