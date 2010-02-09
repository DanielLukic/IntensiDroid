package net.intensicode.droid.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.graphics.PixelFormat;
import net.intensicode.core.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL10;


public final class OpenglGameView extends GLSurfaceView implements DirectScreen, GLSurfaceView.Renderer
    {
    public OpenglGraphics graphics;

    public GameSystem system;


    public OpenglGameView( final Context aContext )
        {
        super( aContext );

        //#if DEBUG
        setDebugFlags( DEBUG_CHECK_GL_ERROR );
        //#endif

        setClickable( false );
        setFocusable( true );
        setFocusableInTouchMode( true );
        setHapticFeedbackEnabled( false );
        setKeepScreenOn( true );
        setLongClickable( false );
        setWillNotCacheDrawing( false );
        setWillNotDraw( false );

//        setEGLConfigChooser( RED_BITS, GREEN_BITS, BLUE_BITS, ALPHA_BITS, DEPTH_BITS, STENCIL_BITS );
//        setEGLConfigChooser(8, 8, 8, 8, 0, 0);

        // Fix for Samsung Galaxy.. Seems to be OK with other devices, too..
        setEGLConfigChooser(
                new GLSurfaceView.EGLConfigChooser()
                {
                public EGLConfig chooseConfig( EGL10 egl, EGLDisplay display )
                    {
                    int[] attributes = new int[]{
                            //EGL10.EGL_RED_SIZE,
                            //5,
                            //EGL10.EGL_BLUE_SIZE,
                            //5,
                            //EGL10.EGL_GREEN_SIZE,
                            //6,
                            EGL10.EGL_DEPTH_SIZE,
                            16,
                            EGL10.EGL_NONE
                    };
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] result = new int[1];
                    egl.eglChooseConfig( display, attributes, configs, 1, result );
                    return configs[ 0 ];
                    }
                }
        );

        setRenderer( this );
        setRenderMode( RENDERMODE_CONTINUOUSLY );
        }

    // From DirectScreen

    public final int width()
        {
        if ( myTargetSize.width != 0 ) return myTargetSize.width;
        return getWidth();
        }

    public final int height()
        {
        if ( myTargetSize.height != 0 ) return myTargetSize.height;
        return getHeight();
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

    public Position toTarget( final int aNativeX, final int aNativeY )
        {
        myTransformedPosition.x = (int) ( aNativeX / getWidth() * width() );
        myTransformedPosition.y = (int) ( aNativeY / getHeight() * height() );
        return myTransformedPosition;
        }

    public final void beginFrame()
        {
        // Because of the GLSurfaceView/Rendered architecture this will do nothing.
        // Everything is handled in onDrawFrame.
        }

    public final void endFrame()
        {
        // Because of the GLSurfaceView/Rendered architecture this will do nothing.
        // Everything is handled in onDrawFrame.
        }

    // From Renderer

    public final void onSurfaceCreated( final GL10 aGL10, final EGLConfig aEGLConfig )
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

    public final void onSurfaceChanged( final GL10 aGL10, final int aWidth, final int aHeight )
        {
        aGL10.glViewport( 0, 0, aWidth, aHeight );
        aGL10.glMatrixMode( GL10.GL_PROJECTION );
        aGL10.glLoadIdentity();
        aGL10.glOrthof( 0, width(), 0, height(), -1, 1 );
        aGL10.glTranslatef( 0, height(), 0 );
        aGL10.glScalef( 1.0f, -1.0f, 1.0f );
        aGL10.glMatrixMode( GL10.GL_MODELVIEW );

        graphics.onSurfaceChanged( aGL10, aWidth, aHeight );
        }

    public final void onDrawFrame( final GL10 aGL10 )
        {
        graphics.onBeginFrame();

        aGL10.glMatrixMode( GL10.GL_MODELVIEW );
        aGL10.glLoadIdentity();

        aGL10.glClear( GL10.GL_COLOR_BUFFER_BIT );

        system.engine.runSingleLoop();
//        system.runSingleLoop();

        graphics.onEndFrame();
        }


    private final Size myTargetSize = new Size();

    private final Position myTransformedPosition = new Position();

    private static final int RED_BITS = 5;

    private static final int GREEN_BITS = 6;

    private static final int BLUE_BITS = 5;

    private static final int ALPHA_BITS = 5;

    private static final int DEPTH_BITS = 16;

    private static final int STENCIL_BITS = 0;
    }
