package net.intensicode;

import android.media.AudioManager;
import android.os.*;
import android.view.*;
import net.intensicode.configuration.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

public abstract class IntensiDroid extends DebugLifeCycleActivity implements PlatformContext, SystemContext
    {
    protected IntensiDroid()
        {
        if ( Log.theLog == null || !( Log.theLog instanceof AndroidLog ) ) Log.theLog = new AndroidLog();
        }

    // From PlatformContext

    public final long compatibleTimeInMillis()
        {
        return SystemClock.uptimeMillis();
        }

    // From SystemContext

    public final GameSystem system()
        {
        return myGameSystem;
        }

    public boolean useOpenglIfPossible()
        {
        //#if OPENGL
        //# return true;
        //#else
        return false;
        //#endif
        }

    public final ConfigurationElementsTree getPlatformValues()
        {
        final ConfigurationElementsTree platform = new ConfigurationElementsTree( "Platform" );

        final ConfigurationElementsTree trackball = platform.addSubTree( "Trackball" );
        trackball.addLeaf( new TrackballPreset( myGameSystem.analog ) );
        trackball.addLeaf( new InitialTicksThreshold( myGameSystem.analog ) );
        trackball.addLeaf( new MultiTicksThreshold( myGameSystem.analog ) );
        trackball.addLeaf( new AdditionalMultiTicksThreshold( myGameSystem.analog ) );
        trackball.addLeaf( new SilenceBeforeUpdateInMillis( myGameSystem.analog ) );
        trackball.addLeaf( new MultiEventThresholdInMillis( myGameSystem.analog ) );
        trackball.addLeaf( new ForcedSilenceBetweenEventsInMillis( myGameSystem.analog ) );
        trackball.addLeaf( new DirectionIgnoreFactorFixed( myGameSystem.analog ) );

        final ConfigurationElementsTree ui = platform.addSubTree( "UI" );
        ui.addLeaf( new CaptureBackKey( (AndroidKeysHandler) myGameSystem.keys ) );

        return platform;
        }

    public final ConfigurationElementsTree getSystemValues()
        {
        return myGameSystem.getSystemValues();
        }

    public ConfigurationElementsTree getApplicationValues()
        {
        return ConfigurationElementsTree.EMPTY;
        }

    public final void loadConfigurableValues()
        {
        final IntensiGameHelper helper = new IntensiGameHelper( myGameSystem );
        helper.loadConfiguration( getPlatformValues() );
        helper.loadConfiguration( getSystemValues() );
        helper.loadConfiguration( getApplicationValues() );
        }

    public final void saveConfigurableValues()
        {
        final IntensiGameHelper helper = new IntensiGameHelper( myGameSystem );
        helper.saveConfiguration( getPlatformValues() );
        helper.saveConfiguration( getSystemValues() );
        helper.saveConfiguration( getApplicationValues() );
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

    public final void triggerConfigurationMenu()
        {
        myHandler.post( new Runnable()
        {
        public final void run()
            {
            openOptionsMenu();
            }
        } );
        }

    public void terminateApplication()
        {
        finish();
        }

    // From Activity

    public boolean onOptionsItemSelected( final MenuItem aMenuItem )
        {
        if ( myOptionsMenuHandler != null ) return myOptionsMenuHandler.onOptionsItemSelected( aMenuItem );
        return super.onOptionsItemSelected( aMenuItem );
        }

    public boolean onCreateOptionsMenu( final Menu aMenu )
        {
        if ( myOptionsMenuHandler != null ) myOptionsMenuHandler.onCreateOptionsMenu( aMenu );
        return super.onCreateOptionsMenu( aMenu );
        }

    public boolean onPrepareOptionsMenu( final Menu aMenu )
        {
        aMenu.clear();
        if ( myOptionsMenuHandler != null ) myOptionsMenuHandler.onCreateOptionsMenu( aMenu );
        return super.onPrepareOptionsMenu( aMenu );
        }

    public final boolean onTrackballEvent( final MotionEvent aMotionEvent )
        {
        myAnalogController.onTrackballEvent( aMotionEvent );
        return false;
        }

    public final void onWindowFocusChanged( final boolean aHasFocusFlag )
        {
        super.onWindowFocusChanged( aHasFocusFlag );
        system().engine.paused = !aHasFocusFlag;
        }

    public final void onCreate( final Bundle savedInstanceState )
        {
        super.onCreate( savedInstanceState );

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        myHandler = new Handler( getMainLooper() );

        AndroidUtilities.showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        createGameViewAndGameSystem();

        final IntensiGameHelper helper = new IntensiGameHelper( myGameSystem );
        helper.initGameSystemFromConfigurationFile();

        setContentView( myGameView );
        }

    protected void onStart()
        {
        super.onStart();
        if ( myGameView.isInitialized() ) myGameSystem.start();
        }

    protected void onResume()
        {
        super.onResume();
        if ( myGameView.isInitialized() ) myGameSystem.start();
        }

    protected void onPause()
        {
        onPauseApplication();
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

        final AndroidGameSystem system = new AndroidGameSystem( this, this );
        final AndroidGameEngine engine = new AndroidGameEngine( system );

        final VideoSystem videoSystem = createVideoSystem( system );
        final AndroidGameView view = videoSystem.view;
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

        final AndroidAnalogController analog = new AndroidAnalogController();

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
        myOptionsMenuHandler = new OptionsMenuHandler( this, myGameSystem );
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


    private Handler myHandler;

    private GameSystem myGameSystem;

    private AndroidGameView myGameView;

    private AndroidAnalogController myAnalogController;

    private OptionsMenuHandler myOptionsMenuHandler;
    }
