package net.intensicode.droid;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import net.intensicode.core.DirectScreen;
import net.intensicode.core.GameSystem;
import net.intensicode.util.*;

public final class AndroidGameView extends SurfaceView implements DirectScreen, SurfaceHolder.Callback
    {
    public final boolean hasSurface()
        {
        return myHasSurfaceFlag;
        }

    public AndroidGameView( final Context aContext, final int aSurfaceType, final SurfaceProjection aSurfaceProjection, final GameSystem aGameSystem )
        {
        super( aContext );

        mySurfaceProjection = aSurfaceProjection;
        myGameSystem = aGameSystem;

        mySurfaceHolder = getHolder();
        mySurfaceHolder.addCallback( this );
        mySurfaceHolder.setType( aSurfaceType );

        mySurfaceProjection.holder = mySurfaceHolder;

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
        if ( mySurfaceProjection.target.width == 0 ) return getWidth();
        return mySurfaceProjection.target.width;
        }

    public final int height()
        {
        if ( mySurfaceProjection.target.height == 0 ) return getHeight();
        return mySurfaceProjection.target.height;
        }

    public final int getTargetWidth()
        {
        return mySurfaceProjection.target.width;
        }

    public final int getTargetHeight()
        {
        return mySurfaceProjection.target.height;
        }

    public final void setTargetSize( final int aWidth, final int aHeight )
        {
        mySurfaceProjection.setTargetSize( aWidth, aHeight );
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

    public final void initialize() throws Exception
        {
        mySurfaceProjection.setScreenSize( getWidth(), getHeight() );
        myGameSystem.graphics.initialize();
        }

    public final void beginFrame() throws InterruptedException
        {
        myGameSystem.graphics.beginFrame();
        }

    public final void endFrame()
        {
        myGameSystem.graphics.endFrame();
        }

    public final void cleanup()
        {
        myGameSystem.graphics.cleanup();
        }

    public Position toTarget( final int aNativeX, final int aNativeY )
        {
        myTransformedPosition.x = (int) ( ( aNativeX - mySurfaceProjection.offsetX ) / mySurfaceProjection.scaleX );
        myTransformedPosition.y = (int) ( ( aNativeY - mySurfaceProjection.offsetY ) / mySurfaceProjection.scaleY );
        return myTransformedPosition;
        }

    public Position toNative( final int aTargetX, final int aTargetY )
        {
        myTransformedPosition.x = (int) ( aTargetX * mySurfaceProjection.scaleX + mySurfaceProjection.offsetX );
        myTransformedPosition.y = (int) ( aTargetY * mySurfaceProjection.scaleY + mySurfaceProjection.offsetY );
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
        if ( hasSurface() ) myGameSystem.stop();

        myHasSurfaceFlag = true;
        myGameSystem.start();
        }

    public final void surfaceDestroyed( final SurfaceHolder aSurfaceHolder )
        {
        Log.debug( "surfaceDestroyed" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        myGameSystem.stop();
        myHasSurfaceFlag = false;
        }


    private boolean myHasSurfaceFlag;

    private final GameSystem myGameSystem;

    private final SurfaceHolder mySurfaceHolder;

    private final Position myTransformedPosition = new Position();

    protected final SurfaceProjection mySurfaceProjection;
    }
