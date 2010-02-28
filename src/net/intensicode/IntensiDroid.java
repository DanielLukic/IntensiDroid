package net.intensicode;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.droid.canvas.*;
import net.intensicode.droid.opengl.*;
import net.intensicode.util.*;

public abstract class IntensiDroid extends DebugLifeCycleActivity implements SystemContext
    {
    protected IntensiDroid()
        {
        if ( Log.theLog == null || !( Log.theLog instanceof AndroidLog ) ) Log.theLog = new AndroidLog();
        }

    // From SystemContext

    public final boolean useOpenglIfPossible()
        {
        //#if OPENGL
        //# return true;
        //#else
        return false;
        //#endif
        }

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

    public void terminateApplication()
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
        System.out.println( "Board: " + android.os.Build.BOARD );
        System.out.println( "Brand: " + android.os.Build.BRAND );
        System.out.println( "Device: " + android.os.Build.DEVICE );
        System.out.println( "Display: " + android.os.Build.DISPLAY );
        System.out.println( "Model: " + android.os.Build.MODEL );
        System.out.println( "Product: " + android.os.Build.PRODUCT );
        System.out.println( "Tags: " + android.os.Build.TAGS );
        System.out.println( "Type: " + android.os.Build.TYPE );
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

        view.setOnTouchListener( touch );
        view.setOnKeyListener( keys );

        system.resources = resources;
        system.graphics = graphics;
        system.storage = storage;
        system.engine = engine;
        system.screen = screen;
        //#ifdef TOUCH
        system.touch = touch;
        //#endif
        system.audio = audio;
        system.keys = keys;

        myGameView = view;
        myGameSystem = system;
        }

    private VideoSystem createVideoSystem( final GameSystem aGameSystem )
        {
        if ( useOpenglIfPossible() )
            {
            //#if DEBUG
            Log.debug( "creating OPENGL video system" );
            //#endif
            return createOpenglVideoSystem( aGameSystem );
            }
        else
            {
            //#if DEBUG
            Log.debug( "creating CANVAS video system" );
            //#endif
            return createCanvasVideoSystem( aGameSystem );
            }
        }

    private VideoSystem createOpenglVideoSystem( final GameSystem aGameSystem )
        {
        final OpenglGameView screen = new OpenglGameView( this );
        final OpenglGraphics graphics = new OpenglGraphics( aGameSystem );

        screen.graphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
        }

    private VideoSystem createCanvasVideoSystem( final GameSystem aGameSystem )
        {
        final AndroidGameView screen = new AndroidGameView( this );
        final AndroidCanvasGraphics graphics = new AndroidCanvasGraphics();

        screen.graphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
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


    private class VideoSystem
        {
        public SurfaceView view;

        public DirectScreen screen;

        public DirectGraphics graphics;
        }
    }
