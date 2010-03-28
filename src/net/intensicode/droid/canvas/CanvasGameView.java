package net.intensicode.droid.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import net.intensicode.droid.AndroidGameView;
import net.intensicode.util.*;


public final class CanvasGameView extends AndroidGameView
    {
    public CanvasGraphics graphics;


    public CanvasGameView( final Context aContext )
        {
        super( aContext, SurfaceHolder.SURFACE_TYPE_HARDWARE );
        }

    // Internal API (DirectScreen)

    public final void beginFrame()
        {
        Assert.isNotNull( "surface holder should be initialized", mySurfaceHolder );

        final Canvas canvas = graphics.canvas = mySurfaceHolder.lockCanvas();
        if ( canvas != null )
            {
            final float scaleX = getWidth() / (float) width();
            final float scaleY = getHeight() / (float) height();
            canvas.scale( scaleX, scaleY );
            }
        else Log.error( "lockCanvas failed with null object", null );
        }

    public final void endFrame()
        {
        Assert.isNotNull( "surface holder should be initialized", mySurfaceHolder );

        mySurfaceHolder.unlockCanvasAndPost( graphics.canvas );
        }

    public final void initialize()
        {
        Log.info( "Target screen size: {}x{}", width(), height() );
        Log.info( "Device screen size: {}x{}", getWidth(), getHeight() );
        }

    public final void cleanup()
        {
        }
    }
