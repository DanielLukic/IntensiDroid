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
        if ( myTargetSize.height == 0 ) return getHeight();
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
        myTransformedPosition.x = (int) (( aNativeX - myTargetOffset.x ) / myTargetScale.x);
        myTransformedPosition.y = (int) (( aNativeY - myTargetOffset.y ) / myTargetScale.y);
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

        // Fix for Nexus One switching the screen size from portrait to landscape mode when in landscape mode.
        // I guess this is actually the desired behavior for IntensiGame: Whenever the surface changes, restart.
        if ( isInitialized() ) system.stop();

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


    protected final SurfaceHolder mySurfaceHolder;


    private boolean myInitialized;

    protected final Size myTargetSize = new Size();

    protected final Position myTargetOffset = new Position();

    protected final PositionF myTargetScale = new PositionF( 1f, 1f );

    private final Position myTransformedPosition = new Position();
    }
