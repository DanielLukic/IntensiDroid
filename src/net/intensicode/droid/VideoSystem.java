package net.intensicode.droid;

import android.content.Context;
import android.view.SurfaceView;
import net.intensicode.core.*;
import net.intensicode.droid.canvas.*;
import net.intensicode.droid.opengl.*;

public final class VideoSystem
    {
    public SurfaceView view;

    public DirectScreen screen;

    public DirectGraphics graphics;


    public static VideoSystem createOpenglVideoSystem( final Context aContext, final GameSystem aGameSystem )
        {
        final OpenglGameView screen = new OpenglGameView( aContext );
        final OpenglGraphics graphics = new OpenglGraphics( aGameSystem );

        screen.graphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
        }

    public static VideoSystem createCanvasVideoSystem( final Context aContext, final GameSystem aGameSystem )
        {
        final AndroidGameView screen = new AndroidGameView( aContext );
        final AndroidCanvasGraphics graphics = new AndroidCanvasGraphics();

        screen.graphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
        }
    }
