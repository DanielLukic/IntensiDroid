package net.intensicode;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

public abstract class IntensiGame extends DebugLifeCycleActivity implements SystemContext
    {
    protected IntensiGame()
        {
        if ( Log.theLog == null || !( Log.theLog instanceof AndroidLog ) ) Log.theLog = new AndroidLog();
        }

    // From SystemContext

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

        setWindowFeatures();
        setAudioFeatures();

        createGameViewAndGameSystem();
        IntensiGameHelper.initGameSystemFromConfigurationFile( myGameSystem );

        setContentView( myGameView );
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
        final AndroidGameSystem system = new AndroidGameSystem( this );
        final AndroidGameEngine engine = new AndroidGameEngine( system );
        final AndroidGameView view = new AndroidGameView( this );
        final AndroidCanvasGraphics graphics = new AndroidCanvasGraphics();
        final AndroidResourcesManager resources = new AndroidResourcesManager( getAssets() );
        //#ifdef TOUCH_SUPPORTED
        final AndroidTouchHandler touch = new AndroidTouchHandler( system, view );
        //#endif
        final AndroidKeysHandler keys = new AndroidKeysHandler();
        final AndroidStorageManager storage = new AndroidStorageManager( this );
        final AndroidAudioManager audio = new AndroidAudioManager( this );

        view.setOnTouchListener( touch );
        view.setOnKeyListener( keys );

        view.graphics = graphics;
        view.engine = engine;

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

    private GameSystem myGameSystem;

    private SurfaceView myGameView;
    }
