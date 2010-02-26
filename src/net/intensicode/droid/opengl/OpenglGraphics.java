package net.intensicode.droid.opengl;

import net.intensicode.core.*;
import net.intensicode.droid.*;
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
        hasHardwareBuffers = false; // NOT TESTED YET ON REAL DEVICE - !isVersion1_0;
        hasDrawTextureExtension = extensions.indexOf( "GL_OES_draw_texture" ) >= 0;

        if ( hasHardwareBuffers )
            {
            myFillRectSquare.freeHardwareBuffers( aGL10 );
            myFillRectSquare.generateHardwareBuffers( aGL10 );
            }

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

    public void onSurfaceChanged( final GL10 aGL10, final int aWidth, final int aHeight, final int aDisplayWidth, final int aDisplayHeight )
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
        mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ SHIFT_BLUE ] = mMatrix4x4[ 9 ] = 0.0f;
        mMatrix4x4[ 0 ] = 1.0f;
        mMatrix4x4[ 5 ] = -1.0f;
        mMatrix4x4[ 12 ] = 0.0f;
        mMatrix4x4[ 13 ] = 1.0f;

        myGL.glMatrixMode( GL10.GL_TEXTURE );
        myGL.glLoadMatrixf( mMatrix4x4, 0 );

        myGL.glMatrixMode( GL10.GL_MODELVIEW );

        enableTexturing();

        myTextureStateChanges = myTextureBindCalls = myTextureCropResets = 0;
        }

    private int myTextureStateChanges;

    private int myTextureBindCalls;

    private int myTextureCropResets;

    private boolean myTextureActive;

    private int myTextureId;

    private void enableTexturing()
        {
        myGL.glEnable( GL10.GL_TEXTURE_2D );
        myGL.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureActive = true;

        myTextureStateChanges++;
        }

    private void disableTexturing()
        {
        myGL.glDisable( GL10.GL_TEXTURE_2D );
        myGL.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
        myTextureActive = false;

        myTextureStateChanges++;
        }

    final void onEndFrame()
        {
        disableTexturing();

        //#if DEBUG && DEBUG_OPENGL
        if ( myTextureStateChanges > 10 ) Log.debug( "gl texture state changes: {}", myTextureStateChanges );
        if ( myTextureBindCalls > 10 ) Log.debug( "gl texture bind calls: {}", myTextureBindCalls );
        if ( myTextureCropResets > 10 ) Log.debug( "gl texture crop resets: {}", myTextureCropResets );
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

    private int myColorARGB32;

    public final void setFont( final FontResource aFont )
        {
        myFont = (AndroidFontResource) aFont;
        }

    public void clearRGB24( final int aRGB24 )
        {
        setColorRGB24( aRGB24 );
        fillRect( 0, 0, myWidth, myHeight );
        }

    public final void drawLine( final int aX1, final int aY1, final int aX2, final int aY2 )
        {
        if ( aX1 == aX2 && aY1 == aY2 ) fillColoredRect( aX1, aY1, 1, 1 );
        }

    private void fillColoredRect( final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( myTextureActive ) disableTexturing();
        if ( myBuffersDirty ) updateBuffers();
        myFillRectSquare.draw( myGL, aX, aY, aWidth, aHeight, false );
        }

    private final StaticSquare myFillRectSquare = new StaticSquare();

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
        myTriangle.set( 0, aX1, aY1 );
        myTriangle.set( 1, aX2, aY2 );
        myTriangle.set( 2, aX3, aY3 );
        myTriangle.draw( myGL );
        myBuffersDirty = true;
        }

    private final MutableTriangle myTriangle = new MutableTriangle();

    private boolean myBuffersDirty = true;

    public final void blendImage( final ImageResource aImage, final int aX, final int aY, final int aAlpha256 )
        {
        if ( aAlpha256 == FULLY_TRANSPARENT ) return;
        if ( aAlpha256 == FULLY_OPAQUE ) drawImage( aImage, aX, aY );

        enableImageAlpha( aAlpha256 );
        drawImage( aImage, aX, aY );
        disableImageAlpha();
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

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        if ( aAlpha256 == FULLY_TRANSPARENT ) return;
        if ( aAlpha256 == FULLY_OPAQUE ) drawImage( aImage, aSourceRect, aX, aY );

        enableImageAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        disableImageAlpha();
        }

    private void fillTexturedRect( final ImageResource aImage, final int aX, final int aY )
        {
        fillTexturedRect( aImage, aX, aY, aImage.getWidth(), aImage.getHeight() );
        }

    private int getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.textureId == 0 ) myTextureManager.makeTexture( aImage );
        return aImage.textureId;
        }

    private void fillTexturedRect( final ImageResource aImage, final int aX, final int aY, final int aWidth, final int aHeight )
        {
        if ( !myTextureActive ) enableTexturing();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int textureId = getOrLoadTexture( imageResource );
        if ( myTextureId != textureId ) bindTexture( textureId );

        if ( hasDrawTextureExtension )
            {
            if ( myIsCroppedFlag ) resetTextureCropping( imageResource.textureWidth, imageResource.textureHeight );
            final int y = myHeight - aY - aHeight;
            ( (GL11Ext) myGL ).glDrawTexfOES( aX * myScaleX, y * myScaleY, 0, aWidth * myScaleX, aHeight * myScaleY );
            }
        else
            {
            if ( myBuffersDirty ) updateBuffers();
            myFillRectSquare.draw( myGL, aX, aY, aWidth, aHeight, true );
            }
        }

    private boolean myIsCroppedFlag;

    private void resetTextureCropping( final int aWidth, final int aHeight )
        {
        mCropWorkspace[ 0 ] = 0;
        mCropWorkspace[ 1 ] = aHeight;
        mCropWorkspace[ 2 ] = aWidth;
        mCropWorkspace[ 3 ] = -aHeight;

        ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );

        myIsCroppedFlag = false;

        myTextureCropResets++;
        }

    private final int[] mCropWorkspace = new int[4];

    private void cropTexture( final Rectangle aRect, final AndroidImageResource aImageResource )
        {
        final float xFactor = aImageResource.textureWidth / (float) aImageResource.getWidth();
        final float yFactor = aImageResource.textureHeight / (float) aImageResource.getHeight();
        final float x = aRect.x * xFactor;
        final float y = aRect.y * yFactor;
        final float width = aRect.width * xFactor;
        final float height = aRect.height * yFactor;
        mCropWorkspace[ 0 ] = (int) x;
        mCropWorkspace[ 1 ] = (int) ( y + height );
        mCropWorkspace[ 2 ] = (int) width;
        mCropWorkspace[ 3 ] = (int) -height;

        ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );

        myIsCroppedFlag = true;
        }

    private void bindTexture( final int aTextureId )
        {
        if ( myTextureId == aTextureId ) return;

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, aTextureId );
        myTextureId = aTextureId;

        myTextureBindCalls++;
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY )
        {
        fillTexturedRect( aImage, aX, aY );
        }

    public final void drawImage( final ImageResource aImage, final int aX, final int aY, final int aAlignment )
        {
        final Position aligned = getAlignedPosition( aX, aY, aImage.getWidth(), aImage.getHeight(), aAlignment );
        drawImage( aImage, aligned.x, aligned.y );
        }

    public final void drawImage( final ImageResource aImage, final Rectangle aSourceRect, final int aTargetX, final int aTargetY )
        {
        if ( !myTextureActive ) enableTexturing();

        final AndroidImageResource imageResource = (AndroidImageResource) aImage;
        final int textureId = getOrLoadTexture( imageResource );
        if ( myTextureId != textureId ) bindTexture( textureId );

        if ( hasDrawTextureExtension )
            {
            cropTexture( aSourceRect, imageResource );
            final int x = aTargetX;
            final int y = myHeight - aTargetY - aSourceRect.height;
            final int width = aSourceRect.width;
            final int height = aSourceRect.height;
            ( (GL11Ext) myGL ).glDrawTexfOES( x * myScaleX, y * myScaleY, 0, width * myScaleX, height * myScaleY );
            }
        else
            {
            mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ SHIFT_BLUE ] = mMatrix4x4[ 9 ] = 0.0f;
            mMatrix4x4[ 0 ] = aSourceRect.width / (float) aImage.getWidth();
            mMatrix4x4[ 5 ] = -aSourceRect.height / (float) aImage.getHeight();
            mMatrix4x4[ 12 ] = aSourceRect.x / (float) aImage.getWidth();
            mMatrix4x4[ 13 ] = aSourceRect.y / (float) aImage.getHeight() - mMatrix4x4[ 5 ];

            myGL.glMatrixMode( GL10.GL_TEXTURE );
            myGL.glPushMatrix();
            myGL.glLoadMatrixf( mMatrix4x4, 0 );

            myGL.glMatrixMode( GL10.GL_MODELVIEW );

            if ( myBuffersDirty ) updateBuffers();
            myFillRectSquare.draw( myGL, aTargetX, aTargetY, aSourceRect.width, aSourceRect.height, true );

            myGL.glMatrixMode( GL10.GL_TEXTURE );
            myGL.glPopMatrix();

            myGL.glMatrixMode( GL10.GL_MODELVIEW );
            }
        }

    private void updateBuffers()
        {
        myFillRectSquare.updateBuffers( myGL, true );
        myBuffersDirty = false;
        }

    private float[] mMatrix4x4 = new float[]{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    public final void drawSubstring( final String aText, final int aStart, final int aEnd, final int aX, final int aY )
        {
        }

    public void drawChar( final char aCharCode, final int aX, final int aY )
        {
        }

    // Implementation


    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private int myDisplayWidth;

    private int myDisplayHeight;

    private AndroidFontResource myFont;

    private final TextureManager myTextureManager = new TextureManager();

    private static final int MASK_RGB24 = 0x00FFFFFF;

    private static final int MASK_ALPHA32 = 0xFF000000;

    private static final int SHIFT_ALPHA = 24;

    private static final int SHIFT_RED = 16;

    private static final int SHIFT_BLUE = 8;

    private static final int MASK_COLOR_CHANNEL_8BITS = 255;

    private static final float MASK_COLOR_CHANNEL_AS_FLOAT_VALUE = 255.0f;
    }
