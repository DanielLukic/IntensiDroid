package net.intensicode;

import android.content.*;
import android.media.AudioManager;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.admob.android.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import net.intensicode.configuration.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.droid.opengl.OpenglGameView;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.util.*;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class IntensiDroid extends DebugLifeCycleActivity implements PlatformContext, SystemContext
    {
    //#if ANAL
    private GoogleAnalyticsTracker myAnalyticsTracker;
    //#endif

    protected IntensiDroid()
        {
        AndroidLog.activate();
        }

    // From PlatformContext

    public final long compatibleTimeInMillis()
        {
        return SystemClock.uptimeMillis();
        }

    public final void openWebBrowser( final String aURL )
        {
        startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( aURL ) ) );
        }

    public final void sendEmail( final EmailData aEmailData )
        {
        final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND );
        emailIntent.setType( "text/plain" );
        emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{ aEmailData.to } );
        emailIntent.putExtra( Intent.EXTRA_SUBJECT, aEmailData.subject );
        emailIntent.putExtra( Intent.EXTRA_TEXT, aEmailData.text );
        startActivity( Intent.createChooser( emailIntent, "Send mail" ) );
        }

    public String screenOrientationId()
        {
        return AndroidUtilities.determineScreenOrientationId( this );
        }

    public final String getPlatformSpecString()
        {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( Build.BRAND );
        buffer.append( " * " );
        buffer.append( Build.MODEL );
        buffer.append( " * " );
        buffer.append( Build.DEVICE );
        buffer.append( " * " );
        buffer.append( Build.DISPLAY );
        buffer.append( " * " );
        buffer.append( Build.PRODUCT );
        try
            {
            buffer.append( " * " );
            buffer.append( Build.VERSION.SDK );
            buffer.append( " * " );
            buffer.append( Build.VERSION.RELEASE );
            buffer.append( " * " );
            buffer.append( Build.VERSION.INCREMENTAL );
            }
        catch ( final Exception e )
            {
            Log.error( "failed adding version information. ignored.", e );
            }
        return buffer.toString();
        }

    public final String getGraphicsSpecString()
        {
        final DirectGraphics graphics = myGameSystem.graphics;
        if ( graphics instanceof OpenglGraphics )
            {
            final OpenglGraphics opengl = (OpenglGraphics) graphics;

            final StringBuffer buffer = new StringBuffer();
            buffer.append( opengl.vendor );
            buffer.append( " * " );
            buffer.append( opengl.renderer );
            buffer.append( " * " );
            buffer.append( opengl.version );

            try
                {
                final DynamicArray strings = new DynamicArray();
                final OpenglGameView view = (OpenglGameView) myGameSystem.screen;
                view.openglGraphics.addOpenglStrings( strings );

                for ( int idx = 0; idx < strings.size; idx++ )
                    {
                    buffer.append( "\n" );
                    buffer.append( strings );
                    }
                }
            catch ( final Throwable t )
                {
                Log.error( t );
                }

            return buffer.toString();
            }
        else
            {
            return "AndroidCanvasGraphics";
            }
        }

    public final String getExtendedExceptionData( final Throwable aException )
        {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter( stringWriter );
        writer.print( "MESSAGE: " );
        writer.println( String.valueOf( aException.getMessage() ) );
        writer.print( "STACK: " );
        aException.printStackTrace( writer );
        writer.println();
        if ( aException.getCause() != null )
            {
            writer.print( "CAUSE: " );
            writer.println( String.valueOf( aException.getCause() ) );
            }
        return stringWriter.toString();
        }

    public void showError( final String aMessage, final Throwable aOptionalThrowable )
        {
        postDialog( aMessage, aOptionalThrowable, false );
        }

    public void showCriticalError( final String aMessage, final Throwable aOptionalThrowable )
        {
        postDialog( aMessage, aOptionalThrowable, true );
        }

    private void postDialog( final String aMessage, final Throwable aOptionalThrowable, final boolean aCritical )
        {
        myHandler.post( new Runnable()
        {
        public final void run()
            {
            myErrorDialogBuilder.setTitle( "IntensiGame Error Report" );
            myErrorDialogBuilder.setMessage( aMessage );
            if ( aCritical )
                {
                myErrorDialogBuilder.setCritical( aCritical );
                }
            if ( aOptionalThrowable != null )
                {
                final String exceptionText = getExtendedExceptionData( aOptionalThrowable );
                myErrorDialogBuilder.setCause( exceptionText );
                }
            myErrorDialogBuilder.createDialog();
            }
        } );
        }

    public final void storePreferences( final String aPreferencesId, final String aPropertyKey, final boolean aValue )
        {
        final SharedPreferences preferences = getSharedPreferences( aPreferencesId, Context.MODE_PRIVATE );
        preferences.edit().putBoolean( aPropertyKey, true ).commit();
        }

    // From SystemContext

    public final void trackState( final String aNewState )
        {
        //#if ANAL
        Log.info( "tracking state change: {}", aNewState );
        myAnalyticsTracker.trackEvent( "state", "change", aNewState, 0 );
        //#endif
        }

    public final void trackPageView( final String aPageId )
        {
        //#if ANAL
        Log.info( "tracking page view: {}", aPageId );
        myAnalyticsTracker.trackPageView( aPageId );
        //#endif
        }

    public String determineResourcesFolder( final int aWidth, final int aHeight, final String aScreenOrientationId )
        {
        return myHelper.determineResourcesFolder( aWidth, aHeight, aScreenOrientationId );
        }

    public final GameSystem system()
        {
        return myGameSystem;
        }

    public final void fillEmailData( final EmailData aEmailData )
        {
        }

    public final ConfigurationElementsTree getPlatformValues()
        {
        final ConfigurationElementsTree platform = new ConfigurationElementsTree( "Platform" );

        try
            {
            final ConfigurationElementsTree ui = platform.addSubTree( "UI" );
            ui.addLeaf( new CaptureBackKey( (AndroidKeysHandler) myGameSystem.keys ) );

            //#if PROFILING
            final ConfigurationElementsTree profiling = platform.addSubTree( "Profiling" );
            profiling.addLeaf( new StartProfiling() );
            profiling.addLeaf( new StopProfiling() );
            profiling.addLeaf( new DumpHprofData() );
            //#endif

            //#if !RELEASE

            final DirectGraphics graphics = myGameSystem.graphics;
            if ( graphics instanceof OpenglGraphics )
                {
                final ConfigurationElementsTree opengl = platform.addSubTree( "OpenGL" );
                opengl.addLeaf( new DumpTextureAtlases( (OpenglGraphics) graphics ) );
                }

            platform.addLeaf( new DumpMemory() );

            //#endif
            }
        catch ( final Exception e )
            {
            system().showError( "failed preparing platform values for configuration menu. ignored.", e );
            }

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

    public void onInfoTriggered()
        {
        // Default implementation does nothing..
        }

    public void onDebugTriggered()
        {
        myHelper.toggleDebugScreen();
        }

    public void onCheatTriggered()
        {
        myHelper.toggleCheatScreen();
        }

    public void onPauseApplication()
        {
        // Default implementation does nothing..
        }

    public void onDestroyApplication()
        {
        // Default implementation does nothing..
        }

    //#if ORIENTATION_DYNAMIC

    public void onOrientationChanged()
        {
        // Default implementation does nothing..
        }

    //#endif

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

        //#if ANAL
        myAnalyticsTracker = GoogleAnalyticsTracker.getInstance();
        myAnalyticsTracker.start( "${google_analytics_id}", 20, this );
        Log.info( "starting analytics tracker for id: ${google_analytics_id}" );
        //#endif

        trackState( "onCreate" );

        //#if ORIENTATION_LANDSCAPE
        //# setRequestedOrientation( android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        //#endif
        //#if ORIENTATION_PORTRAIT
        //# setRequestedOrientation( android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        //#endif

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        myHandler = new Handler( getMainLooper() );

        AndroidUtilities.showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        try
            {
            createGameViewAndGameSystem();
            }
        catch ( final Exception e )
            {
            showCriticalError( "failed initializing game system", e );
            }

        updateResourcesSubfolder();
        myHelper.initGameSystemFromConfigurationFile();

        //#if ADMOB
        myGameView.setId( 0x1723CAFE );
        myGameView.setFocusable( true );
        myGameView.requestFocus();
        myGameView.requestFocusFromTouch();

        final AdView adView = new AdView( this );
        adView.setId( 0x1723BABE );
        adView.setFocusable( false );
        adView.setBackgroundColor( 0x000000 );
        adView.setPrimaryTextColor( 0xFFFFFFFF );
        adView.setSecondaryTextColor( 0xFFCCCCCC );
        adView.setKeywords( "Android Game Tetris Arcade Action Falling Blocks Explosions Bombs Psychocell Berlin" );
        adView.setRequestInterval( 180 );
        adView.setEnabled( true );

        final RelativeLayout layout = new RelativeLayout( this );
        layout.addView( myGameView );
        layout.addView( adView );

        setContentView( layout );
        //#else
        //# setContentView( myGameView );
        //#endif
        }

    protected void onStart()
        {
        super.onStart();
        if ( myGameView.isInitialized() ) myGameSystem.start();
        }

    protected void onResume()
        {
        super.onResume();

        trackState( "onResume" );

        if ( myGameView.isInitialized() ) myGameSystem.start();
        }

    protected void onPause()
        {
        onPauseApplication();
        myGameSystem.stop(); // this is really the only one that has an effect..
        super.onPause();

        trackState( "onPause" );

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
        myGameSystem.stop();
        super.onStop();

        finishIfPauseShouldStop();
        }

    protected void onDestroy()
        {
        myGameSystem.destroy();
        super.onDestroy();

        trackState( "onDestroy" );

        //#if ANAL
        myAnalyticsTracker.stop();
        //#endif

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

        myGameSystem.engine.orientationChanged = true;
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
        final AndroidGameSystem system = new AndroidGameSystem( this, this );
        final AndroidGameEngine engine = new AndroidGameEngine( system );

        final VideoSystem videoSystem = createVideoSystem( system );
        final AndroidGameView view = videoSystem.view;
        final DirectScreen screen = videoSystem.screen;
        final DirectGraphics graphics = videoSystem.graphics;

        final AndroidResourcesManager resources = new AndroidResourcesManager( getAssets() );
        //#ifdef TOUCH
        final AndroidTouchHandler touch = new AndroidTouchHandler( system, screen );
        //#endif
        final AndroidKeysHandler keys = new AndroidKeysHandler();
        final AndroidStorageManager storage = new AndroidStorageManager( this );
        final AndroidAudioManager audio = new AndroidAudioManager( this, resources );

        system.graphics = graphics;

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
        system.trackball = trackball;
        //#endif
        system.resources = resources;

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
        //#if TRACKBALL
        myTrackballHandler = trackball;
        //#endif
        myOptionsMenuHandler = new OptionsMenuHandler( this, myGameSystem );

        myErrorDialogBuilder = new ErrorDialogBuilder( this, myGameSystem );

        myHelper = new IntensiGameHelper( myGameSystem );
        }

    private void updateResourcesSubfolder()
        {
        final WindowManager manager = getWindowManager();
        final Display display = manager.getDefaultDisplay();
        final int width = display.getWidth();
        final int height = display.getHeight();
        myHelper.updateResourcesSubfolder( width, height );
        }

    private VideoSystem createVideoSystem( final GameSystem aGameSystem )
        {
        //#if OPENGL
        if ( shouldUseCanvas() )
            {
            Log.info( "using canvas renderer in OPENGL build" );
            return VideoSystem.createCanvasVideoSystem( this, aGameSystem );
            }
        return VideoSystem.createOpenglVideoSystem( this, aGameSystem, this );
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


    private Handler myHandler;

    private GameSystem myGameSystem;

    private AndroidGameView myGameView;

    private IntensiGameHelper myHelper;

    private Configuration myEngineConfiguration;

    private OptionsMenuHandler myOptionsMenuHandler;

    //#if TRACKBALL
    private AndroidTrackballHandler myTrackballHandler;
    //#endif

    private ErrorDialogBuilder myErrorDialogBuilder;
    }
