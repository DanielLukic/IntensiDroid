package net.intensicode.droid.opengl;

import android.content.Context;
import android.os.Build;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL10;


public final class OpenglGameView extends SurfaceView implements DirectScreen, SurfaceHolder.Callback
    {
    public OpenglGraphics graphics;

    public GameSystem system;


    public OpenglGameView( final Context aContext )
        {
        super( aContext );

        mySurfaceHolder = getHolder();
        mySurfaceHolder.addCallback( this );
        mySurfaceHolder.setType( SurfaceHolder.SURFACE_TYPE_GPU );

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
        Assert.notNull( "opengl handle", myGL );
        //#endif

        graphics.onBeginFrame();

        myGL.glMatrixMode( GL10.GL_MODELVIEW );
        myGL.glLoadIdentity();

        myGL.glClear( GL10.GL_COLOR_BUFFER_BIT );
        }

    public final void endFrame()
        {
        //#if DEBUG
        Assert.notNull( "opengl handle", myGL );
        //#endif

        graphics.onEndFrame();

        final int state = myEglHelper.swapAndReturnContextState();
        if ( state == EglHelper.CONTEXT_LOST )
            {
            //#if DEBUG
            Log.debug( "graphics context lost" );
            //#endif
            myEglHelper.finish();
            }
        }

    public final void initialize()
        {
        initializeGraphics();
        }

    public final void cleanup()
        {
        graphics.releaseGL();
        myEglHelper.finish();
        myGL = null;
        }

    public Position toTarget( final int aNativeX, final int aNativeY )
        {
        myTransformedPosition.x = aNativeX * width() / getWidth();
        myTransformedPosition.y = aNativeY * height() / getHeight();
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

    // Implementation

    private void initializeGraphics()
        {
        myEglHelper.start( getEglConfiguration() );
        myGL = (GL10) myEglHelper.createOrUpdateSurface( mySurfaceHolder );
        onSurfaceCreated( myGL );
        onSurfaceChanged( myGL, getWidth(), getHeight() );
        }

    private int[] getEglConfiguration()
        {
        if ( isSamsungGalaxy() )
            {
            // Samsung Galaxy needs this and other devices seem to be OK with it:
            return new int[]{ EGL10.EGL_DEPTH_SIZE, SAMSUNG_GALAXY_DEPTH_BITS, EGL10.EGL_NONE };
            }
        return new int[]{ EGL10.EGL_NONE };
        }

    private boolean isSamsungGalaxy()
        {
        final boolean isSamsung = Build.BRAND.toLowerCase().indexOf( "samsung" ) != -1;
        final boolean isGalaxy = Build.MODEL.toLowerCase().indexOf( "galaxy" ) != -1;
        return isSamsung && isGalaxy;
        }

    private void onSurfaceCreated( final GL10 aGL10 )
        {
        aGL10.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
        aGL10.glShadeModel( GL10.GL_SMOOTH );
        aGL10.glDisable( GL10.GL_DITHER );
        aGL10.glEnable( GL10.GL_BLEND );
        aGL10.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
        aGL10.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
        aGL10.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );

        graphics.onSurfaceCreated( aGL10 );
        }

    private void onSurfaceChanged( final GL10 aGL10, final int aWidth, final int aHeight )
        {
        aGL10.glViewport( 0, 0, aWidth, aHeight );
        aGL10.glMatrixMode( GL10.GL_PROJECTION );
        aGL10.glLoadIdentity();
        aGL10.glOrthof( 0, width(), 0, height(), -1, 1 );
        aGL10.glTranslatef( 0, height(), 0 );
        aGL10.glScalef( 1.0f, -1.0f, 1.0f );
        aGL10.glMatrixMode( GL10.GL_MODELVIEW );

        graphics.onSurfaceChanged( aGL10, width(), height(), aWidth, aHeight );
        }


    private GL10 myGL;

    private final SurfaceHolder mySurfaceHolder;

    private final Size myTargetSize = new Size();

    private final EglHelper myEglHelper = new EglHelper();

    private final Position myTransformedPosition = new Position();

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;
    }
