package net.intensicode.droid.canvas;

import android.content.Context;
import android.view.SurfaceHolder;
import net.intensicode.droid.AndroidGameView;
import net.intensicode.util.Log;


public final class CanvasGameView extends AndroidGameView
    {
    public CanvasGraphics canvasGraphics;


    public CanvasGameView( final Context aContext )
        {
        super( aContext, SurfaceHolder.SURFACE_TYPE_HARDWARE );
        }

    // Internal API (DirectScreen)

    public final void beginFrame()
        {
        canvasGraphics.setScreenSize( getWidth(), getHeight() );
        canvasGraphics.setTargetSize( width(), height() );
        system.graphics.beginFrame();

        myTargetOffset.x = canvasGraphics.offsetX;
        myTargetOffset.y = canvasGraphics.offsetY;
        myTargetScale.x = canvasGraphics.scale;
        myTargetScale.y = canvasGraphics.scale;
        }

    public final void endFrame()
        {
        system.graphics.endFrame();
        }

    public final void initialize()
        {
        Log.info( "Target screen size: {}x{}", width(), height() );
        Log.info( "Device screen size: {}x{}", getWidth(), getHeight() );
        canvasGraphics.surfaceHolder = mySurfaceHolder;
        }

    public final void cleanup()
        {
        }
    }
