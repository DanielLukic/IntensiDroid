package net.intensicode.droid;

import android.content.Context;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.util.*;

public abstract class AndroidGameView extends SurfaceView implements DirectScreen, SurfaceHolder.Callback
    {
    public GameSystem system;


    // Protected API

    protected AndroidGameView( final Context aContext, final int aSurfaceType )
        {
        super( aContext );

        mySurfaceHolder = getHolder();
        mySurfaceHolder.addCallback( this );
        mySurfaceHolder.setType( aSurfaceType );

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
        }

    public final void setViewportMode( final int aViewportModeId )
        {
        myViewportMode = aViewportModeId;
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

    public final boolean isInitialized()
        {
        return myInitialized;
        }

    public Position toTarget( final int aNativeX, final int aNativeY )
        {
        // What seems to happen is that Android scales the pixel coordinates, but not the touch event coordinates.
        // Therefore the offset has to be taken into account like this:
        myTransformedPosition.x = aNativeX * ( width() + myTransformOffsetX * 2 ) / getWidth() - myTransformOffsetX;
        myTransformedPosition.y = aNativeY * ( height() + myTransformOffsetY * 2 ) / getHeight() - myTransformOffsetY;
        return myTransformedPosition;
        }

    // From SurfaceHolder.Callback

    public final void surfaceCreated( final SurfaceHolder aSurfaceHolder )
        {
        Log.debug( "surfaceCreated" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        }

    public final void surfaceChanged( final SurfaceHolder aSurfaceHolder, final int aFormat, final int aWidth, final int aHeight )
        {
        Log.debug( "surfaceChanged" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        myInitialized = true;
        system.start();
        }

    public final void surfaceDestroyed( final SurfaceHolder aSurfaceHolder )
        {
        Log.debug( "surfaceDestroyed" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        system.stop();
        myInitialized = false;
        }


    protected int myViewportMode;

    protected int myTransformOffsetX;

    protected int myTransformOffsetY;

    protected final SurfaceHolder mySurfaceHolder;


    private boolean myInitialized;

    private final Size myTargetSize = new Size();

    private final Position myTransformedPosition = new Position();
    }
