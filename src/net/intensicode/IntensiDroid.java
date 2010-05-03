package net.intensicode;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.*;
import android.view.*;
import net.intensicode.configuration.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.util.*;

import java.io.*;

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
        //#if TRACKBALL
        myTrackballHandler = trackball;
        //#endif
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

    private OptionsMenuHandler myOptionsMenuHandler;

    //#if TRACKBALL
    private AndroidTrackballHandler myTrackballHandler;
    //#endif
    }
