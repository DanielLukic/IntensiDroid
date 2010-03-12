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

    public boolean useOpenglIfPossible()
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

    public boolean onCreateOptionsMenu( final Menu aMenu )
        {
        final SubMenu trackballMenu = aMenu.addSubMenu( "TRACKBALL" );
        trackballMenu.add( "System events mode" ).setCheckable( true );
        trackballMenu.add( "Change responsivness" );
        trackballMenu.add( "Change horizontal threshold" );
        trackballMenu.add( "Change vertical threshold" );
        trackballMenu.add( "Change horizontal sensitivity" );
        trackballMenu.add( "Change vertical sensitivity" );

        final SubMenu touchMenu = aMenu.addSubMenu( "TOUCH" );
        touchMenu.add( "Emulate trackball" ).setCheckable( true );
        touchMenu.add( "Change responsivness" );
        touchMenu.add( "Change horizontal threshold" );
        touchMenu.add( "Change vertical threshold" );
        touchMenu.add( "Change horizontal sensitivity" );
        touchMenu.add( "Change vertical sensitivity" );

        final SubMenu orientationmenu = aMenu.addSubMenu( "ORIENTATION" );
        orientationmenu.add( "Emulate trackball" ).setCheckable( true );
        orientationmenu.add( "Change responsivness" );
        orientationmenu.add( "Change horizontal threshold" );
        orientationmenu.add( "Change vertical threshold" );
        orientationmenu.add( "Change horizontal sensitivity" );
        orientationmenu.add( "Change vertical sensitivity" );

        final SubMenu acceleratorMenu = aMenu.addSubMenu( "ACCELERATOR" );
        acceleratorMenu.add( "Emulate trackball" ).setCheckable( true );
        acceleratorMenu.add( "Change responsivness" );
        acceleratorMenu.add( "Change horizontal threshold" );
        acceleratorMenu.add( "Change vertical threshold" );
        acceleratorMenu.add( "Change horizontal sensitivity" );
        acceleratorMenu.add( "Change vertical sensitivity" );

        final SubMenu debugMenu = aMenu.addSubMenu( "DEBUG" );
        debugMenu.add( "Dump Texture Atlases" );
        debugMenu.add( "Select Dump Target" );

        //#if CONSOLE
        final SubMenu consoleMenu = aMenu.addSubMenu( "CONSOLE" );
        consoleMenu.add( "Show console" ).setCheckable( true );
        consoleMenu.add( "Set entry stay time" );
        //#endif

        return super.onCreateOptionsMenu( aMenu );
        }

    public boolean onPrepareOptionsMenu( final Menu aMenu )
        {
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

        final AndroidGameSystem system = new AndroidGameSystem( this );
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

    private AndroidGameView myGameView;

    private AndroidAnalogController myAnalogController;
    }
