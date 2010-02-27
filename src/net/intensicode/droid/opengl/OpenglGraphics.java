package net.intensicode.droid.opengl;

import net.intensicode.core.*;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;


public final class OpenglGraphics extends DirectGraphics
    {
    public String vendor;

    public String renderer;

    public String version;

    public String extensions;

    public boolean hasHardwareBuffers;

    public boolean hasDrawTextureExtension;


    final void onSurfaceCreated( final GL10 aGL10 )
        {
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

        System.out.println( "GL vendor: " + vendor );
        System.out.println( "GL renderer: " + renderer );
        System.out.println( "GL version: " + version );
        System.out.println( "GL extensions: " + extensions );
        System.out.println( "GL has draw texture extension? " + hasDrawTextureExtension );
        System.out.println( "GL has hardware buffers? " + hasHardwareBuffers );

        myTextureManager.purgeAllTextures();

        myGL.glEnableClientState( GL10.GL_VERTEX_ARRAY );

        myTextureManager.gl = aGL10;
        myTextureManager.useDrawTextureExtension = hasDrawTextureExtension;
        }

    void onSurfaceChanged( final int aWidth, final int aHeight, final int aDisplayWidth, final int aDisplayHeight )
        {
        myWidth = aWidth;
        myHeight = aHeight;
        myDisplayWidth = aDisplayWidth;
        myDisplayHeight = aDisplayHeight;
        myScaleX = myDisplayWidth / (float) myWidth;
        myScaleY = myDisplayHeight / (float) myHeight;
        }

    final void releaseGL()
        {
        myGL = null;
        }

    final void onBeginFrame()
        {
        mMatrix4x4[ 0 ] = 1.0f;
        mMatrix4x4[ 5 ] = -1.0f;
        mMatrix4x4[ 12 ] = 0.0f;
        mMatrix4x4[ 13 ] = 1.0f;

        myGL.glMatrixMode( GL10.GL_TEXTURE );
        myGL.glLoadMatrixf( mMatrix4x4, 0 );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        enableTexturing();

        myTextureStateChanges = myTextureBindCalls = myTextureMatrixPops = myTextureMatrixPushes = myTextureCropChanges = 0;
        }

    final void onEndFrame()
        {
        disableTexturing();

        if ( myTextureMatrixPushedFlag ) popTextureMatrix();

        //#if DEBUG && DEBUG_OPENGL
        if ( myTextureBindCalls > 10 ) Log.debug( "gl texture bind calls: {}", myTextureBindCalls );
        if ( myTextureMatrixPops > 10 ) Log.debug( "gl texture matrix pops: {}", myTextureMatrixPops );
        if ( myTextureMatrixPushes > 10 ) Log.debug( "gl texture matrix pushes: {}", myTextureMatrixPushes );
        if ( myTextureCropChanges > 10 ) Log.debug( "gl texture crop resets: {}", myTextureCropChanges );
        if ( myTextureStateChanges > 10 ) Log.debug( "gl texture state changes: {}", myTextureStateChanges );
        //#endif
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

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        if ( aX1 == aX2 && aY1 == aY2 ) myGeometryDrawer.drawPoint( aX1, aY1 );
        else myGeometryDrawer.drawLine( aX1, aY1, aX2, aY2 );
        }

    public final void drawRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        fillColoredRect( aX, aY, aWidth, aHeight );
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
        myGeometryDrawer.drawTriangle( aX1, aY1, aX2, aY2, aX3, aY3 );
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

        enableImageAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        disableImageAlpha();
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
        if ( !myTextureEnabled ) enableTexturing();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final Texture texture = getOrLoadTexture( imageResource );
        if ( myActiveTexture != texture ) bindTexture( texture );

        if ( hasDrawTextureExtension )
            {
            final boolean cropChanged = texture.cropTexture( (GL11) myGL, aSourceRect );
            if ( cropChanged ) myTextureCropChanges++;
            final int x = aTargetX;
            final int y = myHeight - aTargetY - aSourceRect.height;
            final int width = aSourceRect.width;
            final int height = aSourceRect.height;
            ( (GL11Ext) myGL ).glDrawTexfOES( x * myScaleX, y * myScaleY, 0, width * myScaleX, height * myScaleY );
            }
        else
            {
            final boolean textureMatrixChanged = !isTextureMatrixUpToDate( texture, aSourceRect );
            if ( textureMatrixChanged )
                {
                if ( isTextureMatrixPushed() ) popTextureMatrix();
                pushTextureMatrix( texture, aSourceRect );
                }

            myGeometryDrawer.enableTextureCoordinates = true;
            myGeometryDrawer.drawSquare( aTargetX, aTargetY, aSourceRect.width, aSourceRect.height );
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
        if ( myTextureEnabled ) disableTexturing();

        myGeometryDrawer.enableTextureCoordinates = false;
        myGeometryDrawer.drawSquare( aX, aY, aWidth, aHeight );
        }

    private void enableImageAlpha( final int aAlpha256 )
        {
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
        myGL.glColor4f( 1f, 1f, 1f, aAlpha256 / MASK_COLOR_CHANNEL_AS_FLOAT_VALUE );
        }

    private void disableImageAlpha()
        {
        setColorARGB32( myColorARGB32 );
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        }

    private Texture getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.texture == null ) myTextureManager.makeTexture( aImage );
        return aImage.texture;
        }

    private void bindTexture( final Texture aTexture )
        {
        //#if DEBUG
        Assert.notSame( "texture already bound", myActiveTexture, aTexture );
        //#endif

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, aTexture.id );
        myActiveTexture = aTexture;

        myTextureBindCalls++;
        }

    private void enableTexturing()
        {
        myGL.glEnable( GL10.GL_TEXTURE_2D );
        myGL.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = true;

        myTextureStateChanges++;
        }

    private void disableTexturing()
        {
        myGL.glDisable( GL10.GL_TEXTURE_2D );
        myGL.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureEnabled = false;

        myTextureStateChanges++;
        }

    private boolean isTextureMatrixUpToDate( final Texture aTexture, final Rectangle aRectangle )
        {
        return myActiveTexture == aTexture && myTextureMatrixRect.equals( aRectangle );
        }

    private boolean isTextureMatrixPushed()
        {
        return myTextureMatrixPushedFlag;
        }

    private void pushTextureMatrix( final Texture aTexture, final Rectangle aRectangle )
        {
        //#if DEBUG
        Assert.isFalse( "already pushed", myTextureMatrixPushedFlag );
        //#endif

        aTexture.setMatrix( mMatrix4x4, aRectangle );

        myGL.glMatrixMode( GL10.GL_TEXTURE );
        myGL.glPushMatrix();
        myGL.glLoadMatrixf( mMatrix4x4, 0 );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        myTextureMatrixPushes++;
        myTextureMatrixPushedFlag = true;
        }

    private void popTextureMatrix()
        {
        //#if DEBUG
        Assert.isTrue( "nothing pushed", myTextureMatrixPushedFlag );
        //#endif

        myGL.glMatrixMode( GL10.GL_TEXTURE );
        myGL.glPopMatrix();

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        myTextureMatrixPops++;
        myTextureMatrixPushedFlag = false;
        }


    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private int myColorARGB32;

    private int myDisplayWidth;

    private int myDisplayHeight;


    private int myTextureBindCalls;

    private int myTextureMatrixPops;

    private int myTextureMatrixPushes;

    private int myTextureStateChanges;

    private int myTextureCropChanges;


    private Texture myActiveTexture;

    private boolean myTextureEnabled;

    private boolean myTextureMatrixPushedFlag;

    private final Rectangle myFullRect = new Rectangle();

    private final Rectangle myTextureMatrixRect = new Rectangle();

    private final TextureManager myTextureManager = new TextureManager();

    private final GeometryDrawer myGeometryDrawer = new GeometryDrawer();

    private final float[] mMatrix4x4 = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    private static final int MASK_RGB24 = 0x00FFFFFF;

    private static final int MASK_ALPHA32 = 0xFF000000;

    private static final int SHIFT_ALPHA = 24;

    private static final int SHIFT_RED = 16;

    private static final int SHIFT_BLUE = 8;

    private static final int MASK_COLOR_CHANNEL_8BITS = 255;

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;
    }
