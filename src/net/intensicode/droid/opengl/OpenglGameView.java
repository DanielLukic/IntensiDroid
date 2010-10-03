package net.intensicode.droid.opengl;

import android.content.Context;
import android.view.SurfaceHolder;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.*;


public final class OpenglGameView extends AndroidGameView
    {
    public OpenglGraphics graphics;


    public OpenglGameView( final Context aContext )
        {
        super( aContext, SurfaceHolder.SURFACE_TYPE_GPU );
        }

    public final boolean isSofwareRenderer()
        {
        final GL10 gl = myEglHelper.start( getEglConfiguration() );
        final String renderer = gl.glGetString( GL10.GL_RENDERER ).toLowerCase();
        return renderer.contains( "pixelflinger" );
        }

    public final void cleanupEarly()
        {
        myEglHelper.finish();
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

//        aDynamicArray.add( "EGL Configurations Available:" );
//        for ( int idx = 0; idx < myEglHelper.availableConfigurations.size; idx++ )
//            {
//            aDynamicArray.add( myEglHelper.availableConfigurations.get( idx ) );
//            }

        aDynamicArray.add( "EGL Configuration Active:" );
        aDynamicArray.add( myEglHelper.choosenConfiguration );
        }

    private int getBitDepth( final int aIdentifier )
        {
        final int[] buffer = new int[1];
        if ( myGL == null ) return 0;
        myGL.glGetIntegerv( aIdentifier, buffer, 0 );
        return buffer[ 0 ];
        }

    // Internal API (DirectScreen)

    public final void beginFrame()
        {
        Assert.notNull( "opengl handle", myGL );

        //#if DEBUG_OPENGL
        if ( myGL instanceof TrackingGL )
            {
            final TrackingGL gl = (TrackingGL) myGL;
            gl.beginFrame();
            }
        //#endif

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        myGL.glLoadIdentity();
        if ( myIsSoftwareRenderer ) myGL.glTranslatef( 0, myTargetSize.height, 0 );

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        TextureUtilities.setRenderTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );

        if ( myTargetOffset.validDirection() ) myGL.glClear( GL11.GL_COLOR_BUFFER_BIT );
        }

    public final void endFrame()
        {
        Assert.notNull( "opengl handle", myGL );

        //#if DEBUG_OPENGL
        if ( myGL instanceof TrackingGL )
            {
            final TrackingGL gl = (TrackingGL) myGL;
            gl.endFrame();
            }
        //#endif

        final int state = myEglHelper.swapAndReturnContextState();
        if ( state == EglHelper.CONTEXT_LOST )
            {
            Log.debug( "graphics context lost" );
            myEglHelper.finish();
            }
        }

    public final void initialize()
        {
        Assert.isTrue( "AndroidGameView initialized", isInitialized() );

        if ( !myEglHelper.isStarted() ) myEglHelper.start( getEglConfiguration() );

        myGL = (GL10) myEglHelper.createOrUpdateSurface( mySurfaceHolder );
        //#if TRACK_OPENGL
        myGL = new TrackingGL( myGL );
        //#endif
        onSurfaceCreated();
        onSurfaceChanged( getWidth(), getHeight() );
        graphics.lateInitialize();

        myIsSoftwareRenderer = graphics.renderer.toLowerCase().contains( "pixelflinger" );
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
            return new int[]{ EGL10.EGL_DEPTH_SIZE, SAMSUNG_GALAXY_DEPTH_BITS, EGL10.EGL_NONE };
            }
        if ( AndroidUtilities.isDroidOrMilestone() )
            {
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
        final int virtualWidth = width();
        final int virtualHeight = height();

        Log.info( "Target screen size: {}x{}", virtualWidth, virtualHeight );
        Log.info( "Device screen size: {}x{}", aWidth, aHeight );

        final float hFactor = aWidth / (float) virtualWidth;
        final float vFactor = aHeight / (float) virtualHeight;
        final float factor = Math.min( hFactor, vFactor );

        final float targetWidth = virtualWidth * factor;
        final float targetHeight = virtualHeight * factor;

        final float xDelta = aWidth - targetWidth;
        final float yDelta = aHeight - targetHeight;

        final float xOffset = xDelta / factor / 2f;
        final float yOffset = yDelta / factor / 2f;

        myGL.glMatrixMode( GL10.GL_PROJECTION );
        myGL.glLoadIdentity();
        final float left = -xOffset;
        final float right = virtualWidth + xOffset;
        final float bottom = virtualHeight + yOffset;
        final float top = -yOffset;
        myGL.glOrthof( left, right, bottom, top, -1, 1 );
        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        graphics.fixDrawTextureExtensionOffset( xDelta / 2f, yDelta / 2f );
        graphics.onSurfaceChanged( virtualWidth, virtualHeight, factor );

        myTargetOffset.x = (int) xOffset;
        myTargetOffset.y = (int) yOffset;
        }


    private GL10 myGL;

    private boolean myIsSoftwareRenderer;

    private final EglHelper myEglHelper = new EglHelper();

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;

    private static final int DROID_RECOMMENDED_DEPTH_BITS = 24;
    }
