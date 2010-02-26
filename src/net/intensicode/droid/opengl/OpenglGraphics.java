package net.intensicode.droid.opengl;

import android.graphics.*;
import android.opengl.GLUtils;
import net.intensicode.core.*;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;
import java.util.ArrayList;


public final class OpenglGraphics extends DirectGraphics implements TexturePurger
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

        //#if DEBUG
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif

        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.get( myTexturizedImageResources.size() - 1 ) );
            }

        myGL.glEnableClientState( GL10.GL_VERTEX_ARRAY );
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
        mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ 8 ] = mMatrix4x4[ 9 ] = 0.0f;
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

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "purging texture {} ({})", aImageResource.textureId, aImageResource.resourcePath );
        //#endif

        mTextureNameWorkspace[ 0 ] = aImageResource.textureId;
        myGL.glDeleteTextures( 1, mTextureNameWorkspace, 0 );

        aImageResource.textureId = aImageResource.textureWidth = aImageResource.textureHeight = 0;
        aImageResource.texturePurger = null;

        final boolean removed = myTexturizedImageResources.remove( aImageResource );
        //#if DEBUG
        if ( !removed ) Log.debug( "failed removing texturized image from internal list" );
        //#endif
        }

    // From DirectGraphics

    public final int getColorRGB24()
        {
        return myColorARGB32 & 0x00FFFFFF;
        }

    public final int getColorARGB32()
        {
        return myColorARGB32;
        }

    public final void setColorRGB24( final int aRGB24 )
        {
        setColorARGB32( 0xFF000000 | aRGB24 );
        }

    public final void setColorARGB32( final int aARGB32 )
        {
        final float alpha = ( ( aARGB32 >> 24 ) & 255 ) / 255.0f;
        final float red = ( ( aARGB32 >> 16 ) & 255 ) / 255.0f;
        final float green = ( ( aARGB32 >> 8 ) & 255 ) / 255.0f;
        final float blue = ( aARGB32 & 255 ) / 255.0f;
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

        final boolean extensionState = hasDrawTextureExtension;

        // Disable because it does not support blending color and texture..
        hasDrawTextureExtension = false;

        enableImageAlpha( aAlpha256 );
        drawImage( aImage, aX, aY );
        disableImageAlpha();

        // Reset extension state..
        hasDrawTextureExtension = extensionState;
        }

    private void enableImageAlpha( final int aAlpha256 )
        {
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
        myGL.glColor4f( 1f, 1f, 1f, aAlpha256 / 255f );
        }

    private void disableImageAlpha()
        {
        setColorARGB32( myColorARGB32 );
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        }

    public final void blendImage( final ImageResource aImage, final Rectangle aSourceRect, final int aX, final int aY, final int aAlpha256 )
        {
        final boolean extensionState = hasDrawTextureExtension;

        // Disable because it does not support blending color and texture..
        hasDrawTextureExtension = false;

        enableImageAlpha( aAlpha256 );
        drawImage( aImage, aSourceRect, aX, aY );
        disableImageAlpha();

        // Reset extension state..
        hasDrawTextureExtension = extensionState;
        }

    private void fillTexturedRect( final ImageResource aImage, final int aX, final int aY )
        {
        fillTexturedRect( aImage, aX, aY, aImage.getWidth(), aImage.getHeight() );
        }

    private int getOrLoadTexture( final AndroidImageResource aImage )
        {
        if ( aImage.textureId == 0 ) makeTexture( aImage );
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
            mMatrix4x4[ 1 ] = mMatrix4x4[ 2 ] = mMatrix4x4[ 4 ] = mMatrix4x4[ 6 ] = mMatrix4x4[ 8 ] = mMatrix4x4[ 9 ] = 0.0f;
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

    private void makeTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "making texture for {}", aImageResource.resourcePath );
        //#endif

        myTexturizedImageResources.add( aImageResource );

        mTextureNameWorkspace[ 0 ] = 0;
        myGL.glGenTextures( 1, mTextureNameWorkspace, 0 );

        final int textureName = aImageResource.textureId = mTextureNameWorkspace[ 0 ];
        //#if DEBUG
        Log.debug( "new texture id: {}", textureName );
        //#endif

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, textureName );

        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );

        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );

        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );

        final Bitmap originalBitmap = aImageResource.bitmap;

        final int originalWidth = originalBitmap.getWidth();
        final int properWidth = findClosestPowerOfTwo( originalWidth );
        final int originalHeight = originalBitmap.getHeight();
        final int properHeight = findClosestPowerOfTwo( originalHeight );

        final Bitmap bitmap = makeProperBitmap( originalBitmap, properWidth, properHeight );

        aImageResource.textureWidth = bitmap.getWidth();
        aImageResource.textureHeight = bitmap.getHeight();

        aImageResource.texturePurger = this;

        if ( myUseGlutilsFlag )
            {
            GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bitmap, 0 );
            }
        else
            {
            int[] bitmap_data = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels( bitmap_data, 0, bitmap.getWidth(), 0, 0,
                              bitmap.getWidth(), bitmap.getHeight() );
            for ( int n = 0; n < bitmap.getWidth() * bitmap.getHeight(); ++n )
                {
                int pixel = bitmap_data[ n ];
                bitmap_data[ n ] = ( ( ( 0xFF000000 & pixel ) ) |        // Alpha.
                                     ( ( 0x00FF0000 & pixel ) >> 16 ) |  // Red.
                                     ( ( 0x0000FF00 & pixel ) ) |        // Green.
                                     ( ( 0x000000FF & pixel ) << 16 ) );  // Blue.
                }
            IntBuffer bitmap_data_buffer = IntBuffer.wrap( bitmap_data );
            myGL.glBindTexture( GL10.GL_TEXTURE_2D, textureName );
            myGL.glTexImage2D( GL10.GL_TEXTURE_2D,
                               0,                      // Mipmap level.
                               GL10.GL_RGBA,           // Internal format.
                               bitmap.getWidth(),
                               bitmap.getHeight(),
                               0,                      // Border.
                               GL10.GL_RGBA,           // Format.
                               GL10.GL_UNSIGNED_BYTE,
                               bitmap_data_buffer );
            }

        if ( hasDrawTextureExtension )
            {
            mCropWorkspace[ 0 ] = 0;
            mCropWorkspace[ 1 ] = bitmap.getHeight();
            mCropWorkspace[ 2 ] = bitmap.getWidth();
            mCropWorkspace[ 3 ] = -bitmap.getHeight();

            ( (GL11) myGL ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );
            }

        if ( bitmap != originalBitmap ) bitmap.recycle(); // only the created ones..
        }

    private int findClosestPowerOfTwo( final int aOriginalValue )
        {
        final int nextPowerOfTwo = Math.min( MAX_TEXTURE_SIZE_IN_PIXELS, findNextPowerOfTwo( aOriginalValue ) );
        final int deltaToNext = Math.abs( nextPowerOfTwo - aOriginalValue );
        final int previousPowerOfTwo = nextPowerOfTwo / 2;
        if ( previousPowerOfTwo < 2 ) return nextPowerOfTwo;
        final int deltaToPrevious = Math.abs( previousPowerOfTwo - aOriginalValue );
        if ( deltaToNext < deltaToPrevious ) return nextPowerOfTwo;
        else return previousPowerOfTwo;
        }

    private int findNextPowerOfTwo( int aPositiveInteger )
        {
        if ( aPositiveInteger == 0 ) return 1;
        aPositiveInteger--;
        for ( int i = 1; i < 30; i <<= 1 )
            {
            aPositiveInteger = aPositiveInteger | aPositiveInteger >> i;
            }
        return aPositiveInteger + 1;
        }

    private Bitmap makeProperBitmap( final Bitmap aBitmap, final int aWidth, final int aHeight )
        {
        if ( aWidth == aBitmap.getWidth() && aHeight == aBitmap.getHeight() ) return aBitmap;

        final Bitmap bitmap32 = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        myTextureCloneCanvas.setBitmap( bitmap32 );
        myTextureCloneSourceRect.right = aBitmap.getWidth();
        myTextureCloneSourceRect.bottom = aBitmap.getHeight();
        myTextureCloneTargetRect.right = aWidth;
        myTextureCloneTargetRect.bottom = aHeight;
        myTextureCloneCanvas.drawBitmap( aBitmap, myTextureCloneSourceRect, myTextureCloneTargetRect, myTextureClonePaint );

        //#if DEBUG
        Log.debug( "created proper texture bitmap" );
        Log.debug( "bitmap size: {}x{}", aBitmap.getWidth(), aBitmap.getHeight() );
        Log.debug( "proper size: {}x{}", aWidth, aHeight );
        //#endif

        return bitmap32;
        }


    private GL10 myGL;

    private int myWidth;

    private int myHeight;

    private float myScaleX;

    private float myScaleY;

    private int myDisplayWidth;

    private int myDisplayHeight;

    private AndroidFontResource myFont;

    private boolean myUseGlutilsFlag;

    private final int[] mCropWorkspace = new int[4];

    private final int[] mTextureNameWorkspace = new int[1];

    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();

    private final Rect myTextureCloneSourceRect = new Rect();

    private final Rect myTextureCloneTargetRect = new Rect();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();

    private static final int MAX_TEXTURE_SIZE_IN_PIXELS = 512;
    }
