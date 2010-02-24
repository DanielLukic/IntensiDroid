package net.intensicode.droid;

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

        //#if DEBUG
        Log.debug( "Target screen size: {}x{}", width(), height() );
        Log.debug( "Device screen size: {}x{}", getWidth(), getHeight() );
        //#endif
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
        //#if DEBUG
        Assert.isNotNull( "surface holder should be initialized", mySurfaceHolder );
        //#endif

        final Canvas canvas = graphics.lockedCanvas = mySurfaceHolder.lockCanvas();
        if ( canvas != null ) canvas.scale( getWidth() * 1.0f / width(), getHeight() * 1.0f / height() );
        else Log.error( "lockCanvas failed with null object", null );
        }

    public final void endFrame()
        {
        //#if DEBUG
        Assert.isNotNull( "surface holder should be initialized", mySurfaceHolder );
        //#endif

        mySurfaceHolder.unlockCanvasAndPost( graphics.lockedCanvas );
        }

    public final void initialize()
        {
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
        //#if DEBUG
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        //#endif
        }

    public final void surfaceChanged( final SurfaceHolder aSurfaceHolder, final int aFormat, final int aWidth, final int aHeight )
        {
        //#if DEBUG
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        //#endif
        system.start();
        }

    public final void surfaceDestroyed( final SurfaceHolder aSurfaceHolder )
        {
        //#if DEBUG
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        //#endif
        system.stop();
        }


    private final SurfaceHolder mySurfaceHolder;

    private final Size myTargetSize = new Size();

    private final Position myTransformedPosition = new Position();
    }
