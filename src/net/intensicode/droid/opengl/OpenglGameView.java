package net.intensicode.droid.opengl;

import android.content.Context;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.droid.AndroidUtilities;
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

    public final void addOpenglStrings( final DynamicArray aDynamicArray )
        {
        final int alphaBits = getBitDepth( GL10.GL_ALPHA_BITS );
        final int redBits = getBitDepth( GL10.GL_RED_BITS );
        final int greenBits = getBitDepth( GL10.GL_GREEN_BITS );
        final int blueBits = getBitDepth( GL10.GL_BLUE_BITS );

        aDynamicArray.add( "EGL Display Mode:" );
        final StringBuffer buffer = new StringBuffer();
        buffer.append( 'A' );
        buffer.append( alphaBits );
        buffer.append( 'R' );
        buffer.append( redBits );
        buffer.append( 'G' );
        buffer.append( greenBits );
        buffer.append( 'B' );
        buffer.append( blueBits );
        aDynamicArray.add( buffer.toString() );

        aDynamicArray.add( "EGL Configurations Available:" );
        for ( int idx = 0; idx < myEglHelper.availableConfigurations.size; idx++ )
            {
            aDynamicArray.add( myEglHelper.availableConfigurations.get( idx ) );
            }

        aDynamicArray.add( "EGL Configuration Active:" );
        aDynamicArray.add( myEglHelper.choosenConfiguration );
        }

    private int getBitDepth( final int aIdentifier )
        {
        final int[] buffer = new int[1];
        myGL.glGetIntegerv( aIdentifier, buffer, 0 );
        return buffer[ 0 ];
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
        Assert.notNull( "opengl handle", myGL );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );
        myGL.glLoadIdentity();

        myGL.glClear( GL10.GL_COLOR_BUFFER_BIT );
        }

    public final void endFrame()
        {
        Assert.notNull( "opengl handle", myGL );

        final int state = myEglHelper.swapAndReturnContextState();
        if ( state == EglHelper.CONTEXT_LOST )
            {
            Log.debug( "graphics context lost" );
            myEglHelper.finish();
            }
        }

    public final void initialize()
        {
        myEglHelper.start( getEglConfiguration() );
        myGL = (GL10) myEglHelper.createOrUpdateSurface( mySurfaceHolder );
        onSurfaceCreated();
        onSurfaceChanged( getWidth(), getHeight() );
        graphics.lateInitialize();
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
        Log.debug( "surfaceCreated" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        }

    public final void surfaceChanged( final SurfaceHolder aSurfaceHolder, final int aFormat, final int aWidth, final int aHeight )
        {
        Log.debug( "surfaceChanged" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        system.start();
        }

    public final void surfaceDestroyed( final SurfaceHolder aSurfaceHolder )
        {
        Log.debug( "surfaceDestroyed" );
        Assert.equals( "surface holder should not have changed", mySurfaceHolder, aSurfaceHolder );
        system.stop();
        }

    // Implementation

    private int[] getEglConfiguration()
        {
        if ( AndroidUtilities.isSamsungGalaxy() )
            {
            // Samsung Galaxy needs this and other devices seem to be OK with it:
            return new int[]{ EGL10.EGL_DEPTH_SIZE, SAMSUNG_GALAXY_DEPTH_BITS, EGL10.EGL_NONE };
            }
        if ( AndroidUtilities.isDroidOrMilestone() )
            {
            // Let's assume it can handle this:
            TextureUtilities.maximumTextureSize = DROID_MAX_TEXTURE_SIZE;

            return new int[]{ EGL10.EGL_DEPTH_SIZE, DROID_RECOMMENDED_DEPTH_BITS, EGL10.EGL_NONE };
            }
        return new int[]{ EGL10.EGL_NONE };
        }

    private void onSurfaceCreated()
        {
        myGL.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
        myGL.glShadeModel( GL10.GL_SMOOTH );
        myGL.glDisable( GL10.GL_DITHER );
        myGL.glEnable( GL10.GL_BLEND );
        myGL.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
        myGL.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
        myGL.glClear( GL10.GL_COLOR_BUFFER_BIT );

        graphics.onSurfaceCreated( myGL );
        }

    private void onSurfaceChanged( final int aWidth, final int aHeight )
        {
        myGL.glViewport( 0, 0, aWidth, aHeight );
        myGL.glMatrixMode( GL10.GL_PROJECTION );
        myGL.glLoadIdentity();
        myGL.glOrthof( 0, width(), 0, height(), -1, 1 );
        myGL.glTranslatef( 0, height(), 0 );
        myGL.glScalef( 1.0f, -1.0f, 1.0f );
        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        graphics.onSurfaceChanged( width(), height(), aWidth, aHeight );
        }


    private GL10 myGL;

    private final SurfaceHolder mySurfaceHolder;

    private final Size myTargetSize = new Size();

    private final EglHelper myEglHelper = new EglHelper();

    private final Position myTransformedPosition = new Position();

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;

    private static final int DROID_RECOMMENDED_DEPTH_BITS = 24;

    private static final int DROID_MAX_TEXTURE_SIZE = TextureUtilities.MAX_TEXTURE_SIZE;
    }
