package net.intensicode.droid.opengl;

import android.content.Context;
import android.view.SurfaceHolder;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL10;


public final class OpenglGameView extends AndroidGameView
    {
    public OpenglGraphics graphics;


    public OpenglGameView( final Context aContext )
        {
        super( aContext, SurfaceHolder.SURFACE_TYPE_GPU );
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

    // Internal API (DirectScreen)

    public final void beginFrame()
        {
        Assert.notNull( "opengl handle", myGL );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );
        myGL.glLoadIdentity();

        myGL.glClear( GL10.GL_COLOR_BUFFER_BIT );

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        TextureUtilities.setRenderTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
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
        Log.info( "Target screen size: {}x{}", width(), height() );
        Log.info( "Device screen size: {}x{}", getWidth(), getHeight() );

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
        updateDisplaySize( aWidth, aHeight );

        final int yOffset = ( aHeight - myDisplaySize.height ) / 2;
        myGL.glViewport( 0, yOffset, myDisplaySize.width, myDisplaySize.height );
        myGL.glMatrixMode( GL10.GL_PROJECTION );
        myGL.glLoadIdentity();
        final int virtualWidth = width();
        final int virtualHeight = height();
        myGL.glOrthof( 0, virtualWidth, 0, virtualHeight, -1, 1 );
        myGL.glTranslatef( 0, virtualHeight, 0 );
        myGL.glScalef( 1.0f, -1.0f, 1.0f );
        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        graphics.onSurfaceChanged( virtualWidth, virtualHeight, myDisplaySize.width, myDisplaySize.height );
        if ( AndroidUtilities.isEmulator() ) graphics.fixDrawTextureOffset( yOffset );
        }

    private void updateDisplaySize( final int aWidth, final int aHeight )
        {
        if ( myViewportMode == VIEWPORT_MODE_FULLSCREEN )
            {
            myDisplaySize.width = aWidth;
            myDisplaySize.height = aHeight;
            }
        else // VIEWPORT_MODE_SYSTEM - let system do the scaling..
            {
            myDisplaySize.width = width();
            myDisplaySize.height = height();
            }
        }


    private GL10 myGL;

    private final Size myDisplaySize = new Size();

    private final EglHelper myEglHelper = new EglHelper();

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;

    private static final int DROID_RECOMMENDED_DEPTH_BITS = 24;

    private static final int DROID_MAX_TEXTURE_SIZE = TextureUtilities.MAX_TEXTURE_SIZE;
    }
