package net.intensicode.droid.opengl;

import android.graphics.Rect;
import android.view.SurfaceHolder;
import net.intensicode.PlatformContext;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.*;


public final class OpenglGraphics extends DirectGraphics
    {
    public final TextureManager textureManager = new TextureManager();

    public SurfaceHolder surfaceHolder;

    public Position targetOffset;

    public PositionF targetScale;

    public final Size targetSize = new Size();

    public final Size screenSize = new Size();

    public boolean initializeTriggered;

    public boolean cleanupTriggered;

    public String vendor;

    public String renderer;

    public String version;

    public String extensions;

    public boolean hasHardwareBuffers;

    public boolean hasDrawTextureExtension;


    public OpenglGraphics( final GameSystem aGameSystem, final PlatformContext aPlatformContext )
        {
        myGameSystem = aGameSystem;
        myPlatformContext = aPlatformContext;
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

    final void onSurfaceCreated( final GL10 aGL10 )
        {
        TextureUtilities.gl = aGL10;

        myGL = aGL10;

        vendor = aGL10.glGetString( GL10.GL_VENDOR );
        renderer = aGL10.glGetString( GL10.GL_RENDERER );
        version = aGL10.glGetString( GL10.GL_VERSION );
        extensions = aGL10.glGetString( GL10.GL_EXTENSIONS );

        final boolean isVersion1_0 = version.indexOf( "1.0" ) >= 0;
        hasHardwareBuffers = false; // NOT TESTED YET - !isVersion1_0;
        hasDrawTextureExtension = extensions.indexOf( "GL_OES_draw_texture" ) >= 0;

        myGeometryDrawer.gl = aGL10;
        if ( hasHardwareBuffers ) myGeometryDrawer.updateHardwareBuffers();

        Log.info( "GL vendor: " + vendor );
        Log.info( "GL renderer: " + renderer );
        Log.info( "GL version: " + version );
        Log.info( "GL extensions: " + extensions );
        Log.info( "GL has draw texture extension? " + hasDrawTextureExtension );
        Log.info( "GL has hardware buffers? " + !isVersion1_0 );

        textureManager.purgeAllTextures();

        myGL.glEnableClientState( GL10.GL_VERTEX_ARRAY );

        myTextureStateManager.gl = aGL10;
        }

    final void releaseGL()
        {
        AndroidImageResource.purgeAllTextures();

        myGeometryDrawer.reset();
        textureManager.purgeAllTextures();
        myTextureStateManager.reset();

        TextureUtilities.gl = null;

        myGL = null;
        }

    // From DirectGraphics

    public final int getColorRGB24()
        {
        return myColorARGB32 & MASK_RGB24;
        }

    public final int getColorARGB32()
        {
        return myColorARGB32;
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        setColorARGB32( MASK_ALPHA32 | aRGB24 );
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        final float alpha = ( ( aARGB32 >> SHIFT_ALPHA ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float red = ( ( aARGB32 >> SHIFT_RED ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float green = ( ( aARGB32 >> SHIFT_BLUE ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float blue = ( aARGB32 & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        myGL.glColor4f( red, green, blue, alpha );
        myColorARGB32 = aARGB32;
        }

    public final void setFont( final FontResource aFont )
        {
        myFont = aFont;
        }

    public void clearRGB24( final int aRGB24 )
        {
        setColorRGB24( aRGB24 );
        fillRect( 0, 0, myWidth, myHeight );
        }

    public void clearARGB32( final int aARGB32 )
        {
        setColorARGB32( aARGB32 );
        fillRect( 0, 0, myWidth, myHeight );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        if ( aX1 == aX2 && aY1 == aY2 ) myGeometryDrawer.drawPoint( aX1, aY1 );
        else myGeometryDrawer.drawLine( aX1, aY1, aX2, aY2 );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.drawRect( aX, aY, aWidth, aHeight );
        }

    public final void drawRGB( final int[] aARGB32, final int aOffsetX, final int aScanlineSize, final int aX, final int aY, final int aWidth, final int aHeight, final boolean aUseAlpha )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myGeometryDrawer.fillTriangle( aX1, aY1, aX2, aY2, aX3, aY3 );
        }

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        blendImage( aImage, myFullRect, aX, aY, aAlpha256 );
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        if ( aAlpha256 == FULLY_TRANSPARENT ) return;
        if ( aAlpha256 == FULLY_OPAQUE ) drawImage( aImage, aSourceRect, aX, aY );

        myTextureStateManager.enableAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        myTextureStateManager.disableAlpha();
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        drawImage( aImage, myFullRect, aX, aY );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        final Position aligned = getAlignedPosition( aX, aY, aImage.getWidth(), aImage.getHeight(), aAlignment );
        drawImage( aImage, aligned.x, aligned.y );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        //#if DEBUG
        if ( aImage == NullImageResource.NULL ) throw new IllegalArgumentException();
        //#else
        //# if ( aImage == NullImageResource.NULL ) return;
        //#endif

        myTextureStateManager.enableTexturingIfNecessary();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final Texture texture = getOrLoadTexture( imageResource );
        myTextureStateManager.bindTexture( texture );

        //#if DEBUG_OPENGL
        if ( hasDrawTextureExtension && Random.INSTANCE.nextInt( 16 ) > 10 )
            //#else
            //# if ( hasDrawTextureExtension )
            //#endif
            {
            myTextureStateManager.updateCrop( aSourceRect );

            final int x = aTargetX;
            final int y = myHeight - aTargetY - aSourceRect.height;
            final int width = aSourceRect.width;
            final int height = aSourceRect.height;
            ( (GL11Ext) myGL ).glDrawTexfOES( x * myScaleX + myOffsetX, y * myScaleY + myOffsetY, 0, width * myScaleX, height * myScaleY );
            }
        else
            {
            myTextureStateManager.updateMatrix( aSourceRect );

            myGeometryDrawer.enableTextureCoordinates = true;
            myGeometryDrawer.fillRect( aTargetX, aTargetY, aSourceRect.width, aSourceRect.height );
            }
        }

    private final Rect mySubstringRect = new Rect();

    private ImageResource mySubstringBuffer;

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        final AndroidFontResource resource = (AndroidFontResource) myFont;
        resource.paint.getTextBounds( aText, aStart, aEnd, mySubstringRect );

        if ( mySubstringBuffer != null && mySubstringBuffer.getWidth() < mySubstringRect.width() ) mySubstringBuffer = null;
        if ( mySubstringBuffer != null && mySubstringBuffer.getHeight() < mySubstringRect.height() ) mySubstringBuffer = null;
        if ( mySubstringBuffer == null ) mySubstringBuffer = AndroidImageResource.createFrom( mySubstringRect.width(), mySubstringRect.height() );

        final DirectGraphics graphics = mySubstringBuffer.getGraphics();
        graphics.setFont( myFont );
        graphics.drawSubstring( aText, aStart, aEnd, aX, aY );
        }

    public final void drawChar( final char aCharCode, final int aX, final int aY )
        {
        // TODO: Implement like drawSubstring.
        drawSubstring( Character.toString( aCharCode ), 0, 1, aX, aY );
        }

    public final void beginFrame()
        {
        if ( !myEglHelper.isStarted() ) initializeTriggered = true;

        if ( initializeTriggered )
            {
            initialize();
            initializeTriggered = false;
            }

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
        if ( myIsSoftwareRenderer ) myGL.glTranslatef( 0, targetSize.height, 0 );

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        TextureUtilities.setRenderTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );

        if ( targetOffset.validDirection() ) myGL.glClear( GL11.GL_COLOR_BUFFER_BIT );
        }

    private void initialize()
        {
        myEglHelper.start( getEglConfiguration() );
        myGL = (GL10) myEglHelper.createOrUpdateSurface( surfaceHolder );
        //#if TRACK_OPENGL
        myGL = new TrackingGL( myGL );
        //#endif
        onSurfaceCreated();
        onSurfaceChanged( screenSize.width, screenSize.height );
        lateInitialize();

        myIsSoftwareRenderer = renderer.toLowerCase().contains( "pixelflinger" );
        if ( !myIsSoftwareRenderer ) return;

        myPlatformContext.storePreferences( "renderer", "software renderer", true );

        myPlatformContext.showCriticalError( "Wrong version for your device! Switched to non-opengl mode. Please restart the application!", null );
        }

    private void lateInitialize()
        {
        final Configuration configuration = myGameSystem.resources.loadConfigurationOrUseDefaults( "opengl.properties" );
        TextureUtilities.maximumTextureSize = configuration.readInt( "max_texture_size", TextureUtilities.MAX_SAFE_TEXTURE_SIZE );
        Log.info( "TextureUtilities.maximumTextureSize = {}", TextureUtilities.maximumTextureSize );
        textureManager.setConfiguration( configuration );
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
            cleanupTriggered = true;
            }

        if ( cleanupTriggered )
            {
            cleanup();
            cleanupTriggered = false;
            }
        }

    private void cleanup()
        {
        releaseGL();
        myEglHelper.finish();
        myGL = null;
        }

    // Implementation

    private void fillColoredRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myTextureStateManager.disableTexturingIfNecessary();

        myGeometryDrawer.enableTextureCoordinates = false;
        myGeometryDrawer.fillRect( aX, aY, aWidth, aHeight );
        }

    private Texture getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.texture == null ) textureManager.addTexture( aImage );
        return aImage.texture;
        }

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

        onSurfaceCreated( myGL );
        }

    private void onSurfaceChanged( final int aWidth, final int aHeight )
        {
        final int virtualWidth = targetSize.width;
        final int virtualHeight = targetSize.height;

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

        fixDrawTextureExtensionOffset( xDelta / 2f, yDelta / 2f );
        onSurfaceChanged( virtualWidth, virtualHeight, factor );

        targetOffset.x = (int) xOffset;
        targetOffset.y = (int) yOffset;

        targetScale.x = factor;
        targetScale.y = factor;
        }

    private void fixDrawTextureExtensionOffset( final float aOffsetX, final float aOffsetY )
        {
        myOffsetX = aOffsetX;
        myOffsetY = aOffsetY;
        //#if DEBUG_OPENGL
        Log.info( "OpenglGraphics DTE offset: {} {}", myOffsetX, myOffsetY );
        //#endif
        }

    private void onSurfaceChanged( final int aWidth, final int aHeight, final float aScaleFactor )
        {
        myWidth = aWidth;
        myHeight = aHeight;
        myScaleX = aScaleFactor;
        myScaleY = aScaleFactor;
        //#if DEBUG_OPENGL
        Log.info( "OpenglGraphics surface scale: {} {}", myScaleX, myScaleY );
        //#endif
        }

    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private float myOffsetX;

    private float myOffsetY;

    private int myColorARGB32;

    private FontResource myFont;

    private boolean myIsSoftwareRenderer;


    private final GameSystem myGameSystem;

    private final PlatformContext myPlatformContext;

    private final Rectangle myFullRect = new Rectangle();

    private final EglHelper myEglHelper = new EglHelper();

    private final GeometryDrawer myGeometryDrawer = new GeometryDrawer();

    private final TextureStateManager myTextureStateManager = new TextureStateManager();

    private static final int MASK_RGB24 = 0x00FFFFFF;

    private static final int MASK_ALPHA32 = 0xFF000000;

    private static final int SHIFT_ALPHA = 24;

    private static final int SHIFT_RED = 16;

    private static final int SHIFT_BLUE = 8;

    private static final int MASK_COLOR_CHANNEL_8BITS = 255;

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;

    private static final int DROID_RECOMMENDED_DEPTH_BITS = 24;
    }
