package net.intensicode.droid.opengl;

import net.intensicode.core.*;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;


public final class OpenglGraphics extends DirectGraphics
    {
    public final TextureManager textureManager = new TextureManager();

    public String vendor;

    public String renderer;

    public String version;

    public String extensions;

    public boolean hasHardwareBuffers;

    public boolean hasDrawTextureExtension;


    public OpenglGraphics( final GameSystem aGameSystem )
        {
        myGameSystem = aGameSystem;
        }

    public final void lateInitialize()
        {
        final Configuration configuration = myGameSystem.resources.loadConfigurationOrUseDefaults( "opengl.properties" );
        textureManager.setConfiguration( configuration );
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

    void onSurfaceChanged( final int aWidth, final int aHeight, final float aDisplayWidth, final float aDisplayHeight )
        {
        myWidth = aWidth;
        myHeight = aHeight;
        myScaleX = aDisplayWidth / (float) myWidth;
        myScaleY = aDisplayHeight / (float) myHeight;
        //#if DEBUG_OPENGL
        Log.info( "OpenglGraphics surface scale: {} {}", myScaleX, myScaleY );
        //#endif
        }

    void fixDrawTextureExtensionOffset( final float aOffsetX, final float aOffsetY )
        {
        myOffsetX = aOffsetX;
        myOffsetY = aOffsetY;
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
        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        blendImage( aImage, myFullRect, aX, aY, aAlpha256 );
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        if ( aAlpha256 == FULLY_TRANSPARENT ) return;
        if ( aAlpha256 == FULLY_OPAQUE ) drawImage( aImage, aSourceRect, aX, aY );

        myTextureStateManager.enableAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        myTextureStateManager.disableAlpha();
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        myFullRect.width = aImage.getWidth();
        myFullRect.height = aImage.getHeight();
        drawImage( aImage, myFullRect, aX, aY );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        final Position aligned = getAlignedPosition( aX, aY, aImage.getWidth(), aImage.getHeight(), aAlignment );
        drawImage( aImage, aligned.x, aligned.y );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
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

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        }

    public void drawChar( final char aCharCode, final int aX, final int aY )
        {
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


    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private float myOffsetX;

    private float myOffsetY;

    private int myColorARGB32;


    private final GameSystem myGameSystem;

    private final Rectangle myFullRect = new Rectangle();

    private final GeometryDrawer myGeometryDrawer = new GeometryDrawer();

    private final TextureStateManager myTextureStateManager = new TextureStateManager();

    private static final int MASK_RGB24 = 0x00FFFFFF;

    private static final int MASK_ALPHA32 = 0xFF000000;

    private static final int SHIFT_ALPHA = 24;

    private static final int SHIFT_RED = 16;

    private static final int SHIFT_BLUE = 8;

    private static final int MASK_COLOR_CHANNEL_8BITS = 255;

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;
    }
