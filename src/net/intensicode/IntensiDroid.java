package net.intensicode;

import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
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
        final Context context = this;

        myHandler.post( new Runnable()
        {
        public final void run()
            {
            final ErrorDialogBuilder dialogBuilder = new ErrorDialogBuilder( context, myGameSystem );
            dialogBuilder.setTitle( "IntensiGame Error Report" );
            dialogBuilder.setMessage( aMessage );
            if ( aCritical )
                {
                dialogBuilder.setCritical( aCritical );
                }
            if ( aOptionalThrowable != null )
                {
                final String exceptionText = getExtendedExceptionData( aOptionalThrowable );
                dialogBuilder.setCause( exceptionText );
                }
            dialogBuilder.createDialog();
            }
        } );
        }

    // From SystemContext

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

        //#if ORIENTATION_LANDSCAPE
        //# setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        //#endif
        //#if ORIENTATION_PORTRAIT
        //# setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        //#endif

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        myHandler = new Handler( getMainLooper() );

        AndroidUtilities.showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        createGameViewAndGameSystem();

        myHelper = new IntensiGameHelper( myGameSystem );
        myHelper.initGameSystemFromConfigurationFile();

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
        myGameSystem.destroy();
        super.onDestroy();

        AndroidImageResource.purgeAll();
        System.gc();
        }

    //#if ORIENTATION_DYNAMIC

    public void onConfigurationChanged( final Configuration aConfiguration )
        {
        Log.info( "onConfigurationChanged {}", aConfiguration );
        super.onConfigurationChanged( aConfiguration );

        final TextView view = new TextView( this );
        view.setText( "Updating view" );
        setContentView( view );

        final String resourcesSubFolder = AndroidUtilities.determineResourcesSubFolder( this );
        myResources.switchSubFolder( resourcesSubFolder );

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
        myResources = resources;
        //#if TRACKBALL
        myTrackballHandler = trackball;
        //#endif
        myOptionsMenuHandler = new OptionsMenuHandler( this, myGameSystem );
        }

    private VideoSystem createVideoSystem( final GameSystem aGameSystem )
        {
        //#if OPENGL
        return VideoSystem.createOpenglVideoSystem( this, aGameSystem );
        //#else
        //# return VideoSystem.createCanvasVideoSystem( this, aGameSystem );
        //#endif
        }


    private Handler myHandler;

    private GameSystem myGameSystem;

    private AndroidGameView myGameView;

    private IntensiGameHelper myHelper;

    private OptionsMenuHandler myOptionsMenuHandler;

    private AndroidResourcesManager myResources;

    //#if TRACKBALL
    private AndroidTrackballHandler myTrackballHandler;
    //#endif
    }
