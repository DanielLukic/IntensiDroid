package net.intensicode.droid.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import net.intensicode.core.DirectScreen;
import net.intensicode.util.*;
import net.intensicode.droid.AndroidGameEngine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public final class OpenglGameView extends GLSurfaceView implements DirectScreen, GLSurfaceView.Renderer
    {
    public OpenglGraphics graphics;

    public AndroidGameEngine engine;


    public OpenglGameView( final Context aContext )
        {
        super( aContext );

        setClickable( false );
        setFocusable( true );
        setFocusableInTouchMode( true );
        setHapticFeedbackEnabled( false );
        setKeepScreenOn( true );
        setLongClickable( false );
        setWillNotCacheDrawing( false );
        setWillNotDraw( false );

        setEGLConfigChooser( RED_BITS, GREEN_BITS, BLUE_BITS, ALPHA_BITS, DEPTH_BITS, STENCIL_BITS );

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

    // From Rendered

    public final void onSurfaceCreated( final GL10 aGL10, final EGLConfig aEGLConfig )
        {
        aGL10.glShadeModel( GL10.GL_SMOOTH );             //Enable Smooth Shading
        aGL10.glClearColor( 0.0f, 0.0f, 0.0f, 0.5f );     //Black Background
        aGL10.glClearDepthf( 1.0f );                     //Depth Buffer Setup
        aGL10.glEnable( GL10.GL_DEPTH_TEST );             //Enables Depth Testing
        aGL10.glDepthFunc( GL10.GL_LEQUAL );             //The Type Of Depth Testing To Do

        //Really Nice Perspective Calculations
        aGL10.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST );
        }

    public final void onSurfaceChanged( final GL10 aGL10, final int aWidth, final int aHeight )
        {
        aGL10.glViewport( 0, 0, aWidth, aHeight );     //Reset The Current Viewport
        aGL10.glMatrixMode( GL10.GL_PROJECTION );     //Select The Projection Matrix
        aGL10.glLoadIdentity();                     //Reset The Projection Matrix

        aGL10.glOrthof( 0, width(), 0, height(), -1, 1 );

        aGL10.glMatrixMode( GL10.GL_MODELVIEW );     //Select The Modelview Matrix
        aGL10.glLoadIdentity();                     //Reset The Modelview Matrix
        }

    private final Square mySquare = new Square();

    public final void onDrawFrame( final GL10 aGL10 )
        {
        graphics.gl = aGL10;

//        engine.runSingleLoop();

        //Clear Screen And Depth Buffer
        aGL10.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
        aGL10.glLoadIdentity();                    //Reset The Current Modelview Matrix

        mySquare.draw( aGL10, 32, 48, 32, 48 );

        graphics.gl = null;
        }


    private final Size myTargetSize = new Size();

    private final Position myTransformedPosition = new Position();

    private static final int RED_BITS = 8;

    private static final int GREEN_BITS = 8;

    private static final int BLUE_BITS = 8;

    private static final int ALPHA_BITS = 8;

    private static final int DEPTH_BITS = 0;

    private static final int STENCIL_BITS = 1;
    }
