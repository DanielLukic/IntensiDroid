package net.intensicode.droid.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.util.*;


public final class AndroidGameView extends SurfaceView implements DirectScreen, SurfaceHolder.Callback
    {
    public AndroidCanvasGraphics graphics;

    public GameSystem system;


    public AndroidGameView( final Context aContext )
        {
        super( aContext );

        mySurfaceHolder = getHolder();
        mySurfaceHolder.addCallback( this );
        mySurfaceHolder.setType( SurfaceHolder.SURFACE_TYPE_HARDWARE );

        setClickable( false );
        setFocusable( true );
        setFocusableInTouchMode( true );
        setHapticFeedbackEnabled( false );
        setKeepScreenOn( true );
        setLongClickable( false );
        setWillNotCacheDrawing( false );
        setWillNotDraw( false );
        }

    // From DirectScreen

    public final int width()
        {
        if ( myTargetSize.width == 0 ) return getWidth();
        return myTargetSize.width;
        }

    public final int height()
        {
        if ( myTargetSize.width == 0 ) return getHeight();
        return myTargetSize.height;
        }

    public final int getTargetWidth()
        {
        return myTargetSize.width;
        }

    public final int getTargetHeight()
        {
        return myTargetSize.height;
        }

    public final void setTargetSize( final int aWidth, final int aHeight )
        {
        myTargetSize.setTo( aWidth, aHeight );

        Log.debug( "Target screen size: {}x{}", width(), height() );
        Log.debug( "Device screen size: {}x{}", getWidth(), getHeight() );
        }

    // Internal API

    public final int getNativeWidth()
        {
        return getWidth();
        }

    public final int getNativeHeight()
        {
        return getHeight();
        }

    public final void beginFrame()
        {
        Assert.isNotNull( "surface holder should be initialized", mySurfaceHolder );

        final Canvas canvas = graphics.canvas = mySurfaceHolder.lockCanvas();
        if ( canvas != null ) canvas.scale( getWidth() * 1.0f / width(), getHeight() * 1.0f / height() );
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

    public Position toTarget( final int aNativeX, final int aNativeY )
        {
        myTransformedPosition.x = (int) ( aNativeX * width() / getWidth() );
        myTransformedPosition.y = (int) ( aNativeY * height() / getHeight() );
        return myTransformedPosition;
        }

    // From SurfaceHolder.Callback

    public final void surfaceCreated( final SurfaceHolder aSurfaceHolder )
        {
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        }

    public final void surfaceChanged( final SurfaceHolder aSurfaceHolder, final int aFormat, final int aWidth, final int aHeight )
        {
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        system.start();
        }

    public final void surfaceDestroyed( final SurfaceHolder aSurfaceHolder )
        {
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        system.stop();
        }


    private final SurfaceHolder mySurfaceHolder;

    private final Size myTargetSize = new Size();

    private final Position myTransformedPosition = new Position();
    }
