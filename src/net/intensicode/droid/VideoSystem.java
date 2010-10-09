package net.intensicode.droid;

import android.content.Context;
import net.intensicode.PlatformContext;
import net.intensicode.core.*;
import net.intensicode.droid.canvas.CanvasGameView;
import net.intensicode.droid.canvas.CanvasGraphics;
import net.intensicode.droid.opengl.OpenglGameView;
import net.intensicode.droid.opengl.OpenglGraphics;

public final class VideoSystem
    {
    public AndroidGameView view;

    public DirectScreen screen;

    public DirectGraphics graphics;


    public static VideoSystem createOpenglVideoSystem( final Context aContext, final GameSystem aGameSystem, final PlatformContext aPlatformContext )
        {
        final OpenglGameView screen = new OpenglGameView( aContext );
        final OpenglGraphics graphics = new OpenglGraphics( aGameSystem, aPlatformContext );

        screen.openglGraphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
        }

    public static VideoSystem createCanvasVideoSystem( final Context aContext, final GameSystem aGameSystem )
        {
        final CanvasGameView screen = new CanvasGameView( aContext );
        final CanvasGraphics graphics = new CanvasGraphics();

        screen.canvasGraphics = graphics;
        screen.system = aGameSystem;

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = graphics;
        videoSystem.screen = screen;
        videoSystem.view = screen;
        return videoSystem;
        }
    }
