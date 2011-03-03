package net.intensicode;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.screens.ScreenBase;
import net.intensicode.util.Assert;
import net.intensicode.util.Log;

public abstract class IntensiDroid extends DebugLifeCycleActivity implements IntensiGameContext
    {
    protected IntensiDroid()
        {
        AndroidLog.activate();
        }

    // From IntensiGameContext

    public final GameSystem system()
        {
        if ( myGameSystem == null ) throw new IllegalStateException();
        return myGameSystem;
        }

    public final SystemContext context()
        {
        if ( system().context == null ) throw new IllegalStateException();
        return system().context;
        }

    public final IntensiGameHelper helper()
        {
        if ( myHelper == null ) throw new IllegalStateException();
        return myHelper;
        }

    public abstract ScreenBase createMainScreen() throws Exception;

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

    //#if TRACKBALL
    public final boolean onTrackballEvent( final MotionEvent aMotionEvent )
        {
        return myTrackballHandler.onTrackballEvent( aMotionEvent );
        }
    //#endif

    public final void onWindowFocusChanged( final boolean aHasFocusFlag )
        {
        super.onWindowFocusChanged( aHasFocusFlag );
        system().engine.paused = !aHasFocusFlag;
        }

    public final void onCreate( final Bundle savedInstanceState )
        {
        super.onCreate( savedInstanceState );

        AndroidPlatformHooks.getInstance().onCreate( this );

        hooks().trackState( "app", "lifecycle", "create" );

        //#if ORIENTATION_LANDSCAPE
        //# setRequestedOrientation( android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        //#endif
        //#if ORIENTATION_PORTRAIT
        //# setRequestedOrientation( android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        //#endif

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        AndroidUtilities.showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        try
            {
            createGameViewAndGameSystem();

            updateResourcesSubfolder();
            myHelper.initGameSystemFromConfigurationFile();

            AndroidPlatformHooks.getInstance().setContentView( this, myGameView );
            }
        catch ( final Exception e )
            {
            final AndroidPlatformContext platform = new AndroidPlatformContext( this, null );
            platform.showCriticalError( "failed initializing game system", e );
            finish();
            }
        }

    protected void onStart()
        {
        super.onStart();
        if ( myGameSystem != null ) if ( myGameView.isInitialized() ) system().start();
        }

    protected void onResume()
        {
        super.onResume();

        if ( myGameSystem != null ) hooks().trackState( "app", "lifecycle", "resume" );

        if ( myGameView.isInitialized() ) system().start();
        }

    private PlatformHooks hooks()
        {
        return AndroidPlatformHooks.getInstance();
        }

    protected void onPause()
        {
        if ( myGameSystem != null ) context().onPauseApplication();
        if ( myGameSystem != null ) myGameSystem.stop(); // this is really the only one that has an effect..
        super.onPause();

        if ( myGameSystem != null ) hooks().trackState( "app", "lifecycle", "pause" );

        finishIfPauseShouldStop();
        }

    private void finishIfPauseShouldStop()
        {
        loadEngineConfigurationIfNecessary();
        if ( !myEngineConfiguration.readBoolean( "IntensiDroid.pauseShouldStop", false ) ) return;
        Log.info( "finishing activity because of IntensiDroid.pauseShouldStop = true" );
        finish();
        }

    private void loadEngineConfigurationIfNecessary()
        {
        if ( myEngineConfiguration != null ) return;
        myEngineConfiguration = system().resources.loadConfigurationOrUseDefaults( "engine.properties" );
        }

    protected void onStop()
        {
        if ( myGameSystem != null ) myGameSystem.stop();
        super.onStop();

        finishIfPauseShouldStop();
        }

    protected void onDestroy()
        {
        if ( myGameSystem != null ) myGameSystem.destroy();
        super.onDestroy();

        if ( myGameSystem != null ) hooks().trackState( "app", "lifecycle", "destroy" );

        AndroidPlatformHooks.getInstance().onDestroy( this );

        AndroidImageResource.purgeAll();
        System.gc();
        }

    //#if ORIENTATION_DYNAMIC

    public void onConfigurationChanged( final android.content.res.Configuration aConfiguration )
        {
        Log.info( "onConfigurationChanged {}", aConfiguration );
        super.onConfigurationChanged( aConfiguration );

        final TextView view = new TextView( this );
        view.setText( "Updating view" );
        setContentView( view );

        updateResourcesSubfolder();
        myHelper.initGameSystemFromConfigurationFile();

        setContentView( myGameView );

        system().engine.orientationChanged = true;
        }

    //#endif

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

    private synchronized void createGameViewAndGameSystem() throws Exception
        {
        myGameSystem = new AndroidGameSystem();

        myHelper = new IntensiGameHelper( myGameSystem );

        myGameSystem.platform = new AndroidPlatformContext( this, myGameSystem );
        myGameSystem.hooks = AndroidPlatformHooks.getInstance();
        myGameSystem.context = new AndroidSystemContext( this, this );
        final AndroidGameEngine engine = new AndroidGameEngine( myGameSystem );

        final VideoSystem videoSystem = createVideoSystem( myGameSystem, myGameSystem.platform );
        final AndroidGameView view = videoSystem.view;
        myGameView = view;
        final DirectScreen screen = videoSystem.screen;
        final DirectGraphics graphics = videoSystem.graphics;

        final AndroidResourcesManager resources = new AndroidResourcesManager( getAssets() );
        //#ifdef TOUCH
        final AndroidTouchHandler touch = new AndroidTouchHandler( myGameSystem, screen );
        //#endif
        final AndroidKeysHandler keys = new AndroidKeysHandler();
        final AndroidStorageManager storage = new AndroidStorageManager( this );
        final AndroidAudioManager audio = new AndroidAudioManager( this, resources );

        final NetworkIO network = new AndroidNetworkIO();

        //#if RENDER_ASYNC
        final net.intensicode.graphics.AsyncRenderQueue renderQueue = new net.intensicode.graphics.AsyncRenderQueue( 2 );
        myGameSystem.renderThread = new net.intensicode.graphics.AsyncRenderThread( renderQueue, graphics, myGameSystem.platform );
        myGameSystem.graphics = new net.intensicode.graphics.AsyncDirectGraphics( renderQueue );
        //#else
        //# myGameSystem.graphics = graphics;
        //#endif

        //#if SENSORS
        final AndroidSensorsManager sensors = new AndroidSensorsManager( this );
        //#endif

        //#if TRACKBALL
        final AndroidTrackballHandler trackball = new AndroidTrackballHandler();
        //#endif

        //#if TOUCH
        view.setOnTouchListener( touch );
        //#endif
        view.setOnKeyListener( keys );

        //#if TRACKBALL
        myGameSystem.trackball = trackball;
        //#endif
        myGameSystem.resources = resources;

        myGameSystem.network = network;

        myGameSystem.storage = storage;
        //#if SENSORS
        myGameSystem.sensors = sensors;
        //#endif
        myGameSystem.engine = engine;
        myGameSystem.screen = screen;
        //#ifdef TOUCH
        myGameSystem.touch = touch;
        //#endif
        myGameSystem.audio = audio;
        myGameSystem.keys = keys;

        //#if TRACKBALL
        myTrackballHandler = trackball;
        //#endif
        myOptionsMenuHandler = new OptionsMenuHandler( this, myGameSystem );

        AndroidPlatformHooks.getInstance().onCreate( myGameSystem );
        }

    private void updateResourcesSubfolder()
        {
        final WindowManager manager = getWindowManager();
        final Display display = manager.getDefaultDisplay();
        final int width = display.getWidth();
        final int height = display.getHeight();
        myHelper.updateResourcesSubfolder( width, height );
        }

    private VideoSystem createVideoSystem( final GameSystem aGameSystem, final PlatformContext aPlatformContext )
        {
        //#if OPENGL
        if ( shouldUseCanvas() )
            {
            Log.info( "using canvas renderer in OPENGL build" );
            return VideoSystem.createCanvasVideoSystem( this, aGameSystem );
            }
        return VideoSystem.createOpenglVideoSystem( this, aGameSystem, aPlatformContext );
        //#else
        //# return VideoSystem.createCanvasVideoSystem( this, aGameSystem );
        //#endif
        }

    private final boolean shouldUseCanvas()
        {
        try
            {
            final SharedPreferences preferences = getSharedPreferences( "renderer", Context.MODE_PRIVATE );
            return preferences.getBoolean( "software renderer", false );
            }
        catch ( final Exception e )
            {
            return false;
            }
        }


    private GameSystem myGameSystem;

    private AndroidGameView myGameView;

    private IntensiGameHelper myHelper;

    private Configuration myEngineConfiguration;

    private OptionsMenuHandler myOptionsMenuHandler;

    //#if TRACKBALL
    private AndroidTrackballHandler myTrackballHandler;
    //#endif
    }
