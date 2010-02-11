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

    public final GameSystem getGameSystem()
        {
        return myGameSystem;
        }

    public void onApplicationShouldPause( final GameSystem aGameSystem )
        {
        // Default implementation does nothing..
        }

    public void onFramesDropped( final GameSystem aGameSystem )
        {
        myGameSystem.showError( "cannot keep minimum frame rate - system overloaded?", null );
        }

    public final void terminateApplication()
        {
        finish();
        }

    // From Activity

    public final void onCreate( final Bundle savedInstanceState )
        {
        super.onCreate( savedInstanceState );

        //#if DEBUG
        Assert.isFalse( "game system already initialized", isGameSystemCreated() );
        //#endif

        showDeviceSpecs();

        setWindowFeatures();
        setAudioFeatures();

        createGameViewAndGameSystem();
        IntensiGameHelper.initGameSystemFromConfigurationFile( myGameSystem );

        setContentView( myGameView );
        }

    // Implementation

    private void showDeviceSpecs()
        {
        //#if DEBUG
        Log.debug( "Board: {}", android.os.Build.BOARD );
        Log.debug( "Brand: {}", android.os.Build.BRAND );
        Log.debug( "Device: {}", android.os.Build.DEVICE );
        Log.debug( "Display: {}", android.os.Build.DISPLAY );
        Log.debug( "Model: {}", android.os.Build.MODEL );
        Log.debug( "Product: {}", android.os.Build.PRODUCT );
        Log.debug( "Tags: {}", android.os.Build.TAGS );
        Log.debug( "Type: {}", android.os.Build.TYPE );
        //#endif
        }

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
        final String resourcesSubFolder = determineResourcesSubFolder();

        final AndroidGameSystem system = new AndroidGameSystem( this );
        final AndroidGameEngine engine = new AndroidGameEngine( system );
        final AndroidGameView view = new AndroidGameView( this );
        final AndroidCanvasGraphics graphics = new AndroidCanvasGraphics();
        final AndroidResourcesManager resources = new AndroidResourcesManager( getAssets(), resourcesSubFolder );
        //#ifdef TOUCH_SUPPORTED
        final AndroidTouchHandler touch = new AndroidTouchHandler( system, view );
        //#endif
        final AndroidKeysHandler keys = new AndroidKeysHandler();
        final AndroidStorageManager storage = new AndroidStorageManager( this );
        final AndroidAudioManager audio = new AndroidAudioManager( this );

        view.setOnTouchListener( touch );
        view.setOnKeyListener( keys );

        view.graphics = graphics;
        view.system = system;

        system.resources = resources;
        system.graphics = graphics;
        system.storage = storage;
        system.engine = engine;
        system.screen = view;
        //#ifdef TOUCH_SUPPORTED
        system.touch = touch;
        //#endif
        system.audio = audio;
        system.keys = keys;

        myGameView = view;
        myGameSystem = system;
        }

    private String determineResourcesSubFolder()
        {
        final Display display = getWindowManager().getDefaultDisplay();
        final int orientation = display.getOrientation();
        if ( orientation == ORIENTATION_PORTRAIT && looksLikePortrait() ) return SUB_FOLDER_PORTRAIT;
        if ( orientation == ORIENTATION_LANDSCAPE && looksLikeLandscape() ) return SUB_FOLDER_LANDSCAPE;
        if ( looksLikePortrait() ) return SUB_FOLDER_PORTRAIT;
        if ( looksLikeLandscape() ) return SUB_FOLDER_LANDSCAPE;
        if ( looksLikeSquare() ) return SUB_FOLDER_SQUARE;
        return NO_SUB_FOLDER;
        }

    private boolean looksLikePortrait()
        {
        final Display display = getWindowManager().getDefaultDisplay();
        return display.getWidth() < display.getHeight();
        }

    private boolean looksLikeLandscape()
        {
        final Display display = getWindowManager().getDefaultDisplay();
        return display.getWidth() > display.getHeight();
        }

    private boolean looksLikeSquare()
        {
        final Display display = getWindowManager().getDefaultDisplay();
        return display.getWidth() == display.getHeight();
        }


    private GameSystem myGameSystem;

    private SurfaceView myGameView;

    private static final int ORIENTATION_PORTRAIT = 0;

    private static final int ORIENTATION_LANDSCAPE = 1;

    private static final String SUB_FOLDER_SQUARE = "s";

    private static final String SUB_FOLDER_PORTRAIT = "p";

    private static final String SUB_FOLDER_LANDSCAPE = "l";

    private static final String NO_SUB_FOLDER = null;
    }
