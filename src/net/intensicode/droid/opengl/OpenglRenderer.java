package net.intensicode.droid.opengl;

import net.intensicode.PlatformContext;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.*;

public final class OpenglRenderer
    {
    public OpenglRenderer( final GameSystem aGameSystem, final PlatformContext aPlatformContext, final SurfaceProjection aSurfaceProjection )
        {
        myGameSystem = aGameSystem;
        myPlatformContext = aPlatformContext;
        mySurfaceProjection = aSurfaceProjection;
        }

    public final AtlasTextureManager getAtlasTextureManager()
        {
        return myTextureManager.atlasTextureManager;
        }

    public final String getOpenglSpecString()
        {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( myOpenglVendor );
        buffer.append( " * " );
        buffer.append( myOpenglRenderer );
        buffer.append( " * " );
        buffer.append( myOpenglVersion );
        return buffer.toString();
        }

    public final String getExtensionsSpecString()
        {
        return myOpenglExtensions;
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

        aDynamicArray.add( "EGL Configuration Active:" );
        aDynamicArray.add( myEglHelper.choosenConfiguration );
        }

    private int getBitDepth( final int aIdentifier )
        {
        final int[] buffer = new int[1];
        myGL.glGetIntegerv( aIdentifier, buffer, 0 );
        return buffer[ 0 ];
        }

    public final void initialize()
        {
        Log.info( "initialize" );

        myEglHelper.start( getEglConfiguration() );
        myGL = (GL10) myEglHelper.createOrUpdateSurface( mySurfaceProjection.holder );
        if ( myGL == null ) throw new IllegalStateException( "no opengl context available" );

        //#if TRACK_OPENGL
        myGL = new TrackingGL( myGL );
        //#endif

        attach( myGL );

        myGL.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
        myGL.glShadeModel( GL10.GL_SMOOTH );
        myGL.glDisable( GL10.GL_DITHER );
        myGL.glEnable( GL10.GL_BLEND );
        myGL.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
        myGL.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
        myGL.glClear( GL10.GL_COLOR_BUFFER_BIT );

        myOpenglVendor = myGL.glGetString( GL10.GL_VENDOR );
        myOpenglRenderer = myGL.glGetString( GL10.GL_RENDERER );
        myOpenglVersion = myGL.glGetString( GL10.GL_VERSION );
        myOpenglExtensions = myGL.glGetString( GL10.GL_EXTENSIONS );

        if ( myOpenglVersion == null || myOpenglExtensions == null ) throw new IllegalStateException( "opengl not initialized (anymore?)" );

        Log.info( "GL vendor: " + myOpenglVendor );
        Log.info( "GL renderer: " + myOpenglRenderer );
        Log.info( "GL version: " + myOpenglVersion );
        Log.info( "GL extensions: " + myOpenglExtensions );

        final boolean isVersion1_0 = myOpenglVersion != null ? myOpenglVersion.indexOf( "1.0" ) >= 0 : true;
        final boolean hasHardwareBuffers = myGL instanceof GL11;
        myHasDrawTextureExtension = myOpenglExtensions.indexOf( "GL_OES_draw_texture" ) >= 0;

        // Still broken..
        //if ( hasHardwareBuffers ) myGeometryDrawer.updateHardwareBuffers();

        Log.info( "GL has draw texture extension? " + myHasDrawTextureExtension );
        Log.info( "GL has hardware buffers? " + !isVersion1_0 );

        myTextureManager.purgeAllTextures();

        myGL.glEnableClientState( GL10.GL_VERTEX_ARRAY );

        myGL.glMatrixMode( GL10.GL_PROJECTION );
        myGL.glLoadIdentity();

        final float left = -mySurfaceProjection.offsetX;
        final float right = mySurfaceProjection.target.width + mySurfaceProjection.offsetX;
        final float bottom = mySurfaceProjection.target.height + mySurfaceProjection.offsetY;
        final float top = -mySurfaceProjection.offsetY;

        Log.info( "left: {}", left );
        Log.info( "right: {}", right );
        Log.info( "bottom: {}", bottom );
        Log.info( "top: {}", top );

        myGL.glOrthof( left, right, bottom, top, -1, 1 );
        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        myOffsetX = mySurfaceProjection.offsetX * mySurfaceProjection.scaleX;
        myOffsetY = mySurfaceProjection.offsetY * mySurfaceProjection.scaleY;

        myWidth = mySurfaceProjection.target.width;
        myHeight = mySurfaceProjection.target.height;
        myScaleX = mySurfaceProjection.scaleX;
        myScaleY = mySurfaceProjection.scaleY;

        Log.info( "myOffsetX: {}", myOffsetX );
        Log.info( "myOffsetY: {}", myOffsetY );
        Log.info( "myWidth: {}", myWidth );
        Log.info( "myHeight: {}", myHeight );
        Log.info( "myScaleX: {}", myScaleX );
        Log.info( "myScaleY: {}", myScaleY );

        loadMaximumTextureSizeFromOpenglProperties();

        final boolean isCrapRenderer = myOpenglVersion.contains( "1.0" );
        myIsSoftwareRenderer = myOpenglRenderer.toLowerCase().contains( "pixelflinger" );
        if ( !isCrapRenderer && !myIsSoftwareRenderer ) return;

        myPlatformContext.storePreferences( "renderer", "software renderer", true );

        myPlatformContext.showError( "Wrong version for your device! Switched to non-opengl mode.\n\nPlease restart the application!\n\nIf you continue you may experience crashes or bad graphics performance!", null );
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

    private void attach( final GL10 aGL10 )
        {
        Log.info( "attach {}", aGL10 );

        if ( aGL10 == null ) throw new NullPointerException( "real GL must not be null" );
        myGL = aGL10;
        myUtilities.attach( myGL );
        myGeometryDrawer.attach( myGL );
        myTextureStateManager.attach( myGL );
        }

    private void loadMaximumTextureSizeFromOpenglProperties()
        {
        // TODO: This should really happen somewhere else and only call OpenglRenderer#setMaxTextureSize(int)

        final Configuration configuration = myGameSystem.resources.loadConfigurationOrUseDefaults( "opengl.properties" );
        TextureUtilities.maximumTextureSize = configuration.readInt( "max_texture_size", TextureUtilities.MAX_SAFE_TEXTURE_SIZE );
        Log.info( "TextureUtilities.maximumTextureSize = {}", TextureUtilities.maximumTextureSize );
        myTextureManager.setConfiguration( configuration );
        }

    public final void destroySafely()
        {
        Log.info( "destroySafely" );

        try
            {
            AndroidImageResource.purgeAllTextures();
            myTextureManager.purgeAllTextures();
            myTextureStateManager.reset();
            myGeometryDrawer.reset();
            }
        catch ( final Exception e )
            {
            Log.error( "failed destroying renderer components", e );
            }

        try
            {
            myEglHelper.finish();
            }
        catch ( final Exception e )
            {
            Log.error( "failed destroying EGL helper", e );
            }

        attach( NoGL.INSTANCE );
        }

    public final void beginFrame()
        {
        if ( !myEglHelper.isStarted() )
            {
            Log.info( "initialize inside beginFrame" );
            initialize();
            }

        //#if DEBUG_OPENGL
        if ( myGL instanceof TrackingGL )
            {
            final TrackingGL gl = (TrackingGL) myGL;
            gl.beginFrame();
            }
        //#endif

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        myGL.glLoadIdentity();
        if ( myIsSoftwareRenderer ) myGL.glTranslatef( 0, mySurfaceProjection.target.height, 0 );

        myUtilities.setAtlasTextureUnit();
        myUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        myUtilities.setRenderTextureUnit();
        myUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );

        if ( clearBackgroundRequired() ) myGL.glClear( GL11.GL_COLOR_BUFFER_BIT );
        }

    private boolean clearBackgroundRequired()
        {
        return mySurfaceProjection.offsetX != 0 || mySurfaceProjection.offsetY != 0;
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
        if ( state == EglHelper.CONTEXT_OK ) return;

        Log.info( "destroySafely inside endFrame" );
        destroySafely();
        }

    public final int width()
        {
        return myWidth;
        }

    public final int height()
        {
        return myHeight;
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        final float alpha = ( ( aARGB32 >> SHIFT_ALPHA ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float red = ( ( aARGB32 >> SHIFT_RED ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float green = ( ( aARGB32 >> SHIFT_BLUE ) & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        final float blue = ( aARGB32 & MASK_COLOR_CHANNEL_8BITS ) / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE;
        myGL.glColor4f( red, green, blue, alpha );
        }

    public final void drawTexture( final float aX, final float aY, final float aWidth, final float aHeight )
        {
        ( (GL11Ext) myGL ).glDrawTexfOES( aX, aY, 0, aWidth, aHeight );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.drawRect( aX, aY, aWidth, aHeight );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.drawLine( aX1, aY1, aX2, aY2 );
        }

    public final void drawPoint( final int aX, final int aY )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.drawPoint( aX, aY );
        }

    public final void fillTriangle( final int aX1, final int aY1, final int aX2, final int aY2, final int aX3, final int aY3 )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.fillTriangle( aX1, aY1, aX2, aY2, aX3, aY3 );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        myTextureStateManager.enableTexturingIfNecessary();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final Texture texture = getOrLoadTexture( imageResource );
        myTextureStateManager.bindTexture( texture );

        //#if DEBUG_OPENGL
        if ( myHasDrawTextureExtension && Random.INSTANCE.nextInt( 16 ) > 10 )
            //#else
            //# if ( myHasDrawTextureExtension )
            //#endif
            {
            myTextureStateManager.updateCrop( aSourceRect );

            final int x = aTargetX;
            final int y = myHeight - aTargetY - aSourceRect.height;
            final int width = aSourceRect.width;
            final int height = aSourceRect.height;

            drawTexture( x * myScaleX + myOffsetX, y * myScaleY + myOffsetY, width * myScaleX, height * myScaleY );
            }
        else
            {
            myTextureStateManager.updateMatrix( aSourceRect );
            myGeometryDrawer.fillRect( aTargetX, aTargetY, aSourceRect.width, aSourceRect.height );
            }
        }

    public final void fillColoredRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        myTextureStateManager.disableTexturingIfNecessary();
        myGeometryDrawer.fillRect( aX, aY, aWidth, aHeight );
        }

    private Texture getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.texture == null ) myTextureManager.addTexture( aImage );
        return aImage.texture;
        }

    public final void enableAlpha( final int aAlpha256 )
        {
        myTextureStateManager.enableAlpha( aAlpha256 );
        }

    public final void disableAlpha()
        {
        myTextureStateManager.disableAlpha();
        }


    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private float myOffsetX;

    private float myOffsetY;

    private String myOpenglVendor;

    private String myOpenglRenderer;

    private String myOpenglVersion;

    private String myOpenglExtensions;

    private boolean myIsSoftwareRenderer;

    private boolean myHasDrawTextureExtension;

    private GL10 myGL = NoGL.INSTANCE;

    private final GameSystem myGameSystem;

    private final PlatformContext myPlatformContext;

    private final SurfaceProjection mySurfaceProjection;

    private final EglHelper myEglHelper = new EglHelper();

    private final TextureUtilities myUtilities = new TextureUtilities();

    private final GeometryDrawer myGeometryDrawer = new GeometryDrawer();

    private final TextureManager myTextureManager = new TextureManager( myUtilities );

    private final TextureStateManager myTextureStateManager = new TextureStateManager( myUtilities );

    private static final int SHIFT_ALPHA = 24;

    private static final int SHIFT_RED = 16;

    private static final int SHIFT_BLUE = 8;

    private static final int MASK_COLOR_CHANNEL_8BITS = 255;

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;

    private static final int SAMSUNG_GALAXY_DEPTH_BITS = 16;

    private static final int DROID_RECOMMENDED_DEPTH_BITS = 24;
    }
