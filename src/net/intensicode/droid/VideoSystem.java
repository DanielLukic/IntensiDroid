package net.intensicode.droid;

import android.content.Context;
import android.view.SurfaceHolder;
import net.intensicode.PlatformContext;
import net.intensicode.core.*;
import net.intensicode.droid.canvas.SurfaceCanvasGraphics;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.droid.opengl.OpenglRenderer;

public final class VideoSystem
    {
    public AndroidGameView view;

    public DirectScreen screen;

    public DirectGraphics graphics;


    public static VideoSystem createOpenglVideoSystem( final Context aContext, final GameSystem aGameSystem, final PlatformContext aPlatformContext )
        {
        final SurfaceProjection surfaceProjection = new SurfaceProjection();

        final AndroidGameView view = new AndroidGameView( aContext, SurfaceHolder.SURFACE_TYPE_GPU, surfaceProjection, aGameSystem );

        final OpenglRenderer renderer = new OpenglRenderer( aGameSystem, aPlatformContext, surfaceProjection );

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = new OpenglGraphics( renderer );
        videoSystem.screen = videoSystem.view = view;
        return videoSystem;
        }

    public static VideoSystem createCanvasVideoSystem( final Context aContext, final GameSystem aGameSystem )
        {
        final SurfaceProjection surfaceProjection = new SurfaceProjection();
        final AndroidGameView view = new AndroidGameView( aContext, SurfaceHolder.SURFACE_TYPE_HARDWARE, surfaceProjection, aGameSystem );

        final VideoSystem videoSystem = new VideoSystem();
        videoSystem.graphics = new SurfaceCanvasGraphics( surfaceProjection );
        videoSystem.screen = videoSystem.view = view;
        return videoSystem;
        }
    }
