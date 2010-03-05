package net.intensicode;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

public abstract class IntensiDroid extends DebugLifeCycleActivity implements SystemContext
    {
    protected IntensiDroid()
        {
        if ( Log.theLog == null || !( Log.theLog instanceof AndroidLog ) ) Log.theLog = new AndroidLog();
        }

    // From SystemContext

    public final GameSystem system()
        {
        return myGameSystem;
        }

    public final boolean useOpenglIfPossible()
        {
        //#if OPENGL
        //# return true;
        //#else
        return false;
        //#endif
        }

    public void onFramesDropped()
        {
        // Default implementation does nothing..
        }

    public void onDebugTriggered()
        {
        IntensiGameHelper.toggleDebugScreen( myGameSystem );
        }

    public void onCheatTriggered()
        {
        IntensiGameHelper.toggleCheatScreen( myGameSystem );
        }

    public void onPauseApplication()
        {
        // Default implementation does nothing..
        }

    public void onDestroyApplication()
        {
        // Default implementation does nothing..
        }

    public void terminateApplication()
        {
        finish();
        }

    // From Activity

    public final boolean onTrackballEvent( final MotionEvent aMotionEvent )
        {
        myAnalogController.onTrackballEvent( aMotionEvent );
        return false;
        }

    public final void onCreate( final Bundle savedInstanceState )
        {
        super.onCreate( savedInstanceState );

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        AndroidUtilities.showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        createGameViewAndGameSystem();
        IntensiGameHelper.initGameSystemFromConfigurationFile( myGameSystem );

        setContentView( myGameView );
        }

    protected void onStart()
        {
        super.onStart();
//        myGameSystem.start(); => no surface yet!
        }

    protected void onResume()
        {
        super.onResume();
//        myGameSystem.start(); => no surface yet!
        }

    protected void onPause()
        {
        myGameSystem.stop(); // this is really the only one that has an effect..
        super.onPause();
        }

    protected void onStop()
        {
        myGameSystem.stop();
        super.onStop();
        }

    protected void onDestroy()
        {
        myGameSystem.stop();
        myGameSystem.destroy();
        super.onDestroy();

        AndroidImageResource.purgeAll();
        System.gc();
        }

    // Implementation

    private void setWindowFeatures()
        {
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        final Window window = getWindow();
        window.setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }

    private void setAudioFeatures()
        {
        setVolumeControlStream( AudioManager.STREAM_MUSIC );
        }

    private boolean isGameSystemCreated()
        {
        return myGameSystem != null;
        }

    private synchronized void createGameViewAndGameSystem()
        {
        final String resourcesSubFolder = AndroidUtilities.determineResourcesSubFolder( this );

        final AndroidGameSystem system = new AndroidGameSystem( this );
        final AndroidGameEngine engine = new AndroidGameEngine( system );

        final VideoSystem videoSystem = createVideoSystem( system );
        final SurfaceView view = videoSystem.view;
        final DirectScreen screen = videoSystem.screen;
        final DirectGraphics graphics = videoSystem.graphics;

        final AndroidResourcesManager resources = new AndroidResourcesManager( getAssets(), resourcesSubFolder );
        //#ifdef TOUCH
        final AndroidTouchHandler touch = new AndroidTouchHandler( system, screen );
        //#endif
        final AndroidKeysHandler keys = new AndroidKeysHandler();
        final AndroidStorageManager storage = new AndroidStorageManager( this );
        final AndroidAudioManager audio = new AndroidAudioManager( this );

        //#if SENSORS
        final AndroidSensorsManager sensors = new AndroidSensorsManager( this );
        //#endif

        final AndroidAnalogController analog = new AndroidAnalogController( sensors );

        view.setOnTouchListener( touch );
        view.setOnKeyListener( keys );

        system.analog = analog;
        system.resources = resources;
        system.graphics = graphics;
        system.storage = storage;
        //#if SENSORS
        system.sensors = sensors;
        //#endif
        system.engine = engine;
        system.screen = screen;
        //#ifdef TOUCH
        system.touch = touch;
        //#endif
        system.audio = audio;
        system.keys = keys;

        myGameView = view;
        myGameSystem = system;
        myAnalogController = analog;
        }

    private VideoSystem createVideoSystem( final GameSystem aGameSystem )
        {
        if ( useOpenglIfPossible() )
            {
            //#if DEBUG
            Log.debug( "creating OPENGL video system" );
            //#endif
            return VideoSystem.createOpenglVideoSystem( this, aGameSystem );
            }
        else
            {
            //#if DEBUG
            Log.debug( "creating CANVAS video system" );
            //#endif
            return VideoSystem.createCanvasVideoSystem( this, aGameSystem );
            }
        }


    private GameSystem myGameSystem;

    private SurfaceView myGameView;

    private AndroidAnalogController myAnalogController;
    }
