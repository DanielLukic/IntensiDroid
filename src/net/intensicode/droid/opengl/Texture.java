package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;

public class Texture
    {
    public boolean allowUseOfGlUtils;

    public int id;


    public Texture( final GL10 aGL )
        {
        myGL = aGL;
        }

    public void makeUsing( final Bitmap aBitmap )
        {
        myWidth = aBitmap.getWidth();
        myHeight = aBitmap.getHeight();
        makeOpenglTexture( aBitmap );

        setScaledSize( myWidth, myHeight );
        }

    public final void purge()
        {
        mTextureNameWorkspace[ 0 ] = id;
        myGL.glDeleteTextures( 1, mTextureNameWorkspace, 0 );
        id = 0;
        }

    public boolean isFullRect( final Rectangle aRectangle )
        {
        return aRectangle.x == 0 && aRectangle.y == 0 && aRectangle.width == myScaledWidth && aRectangle.height == myScaledHeight;
        }

    public void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / myScaledWidth;
        aMatrix4x4[ 5 ] = -aSourceRect.height / myScaledHeight;
        aMatrix4x4[ 12 ] = aSourceRect.x / myScaledWidth;
        aMatrix4x4[ 13 ] = aSourceRect.y / myScaledHeight - aMatrix4x4[ 5 ];
        }

    public boolean cropTextureIfNecessary( final GL11 aGL, final Rectangle aRect )
        {
        if ( myActiveCropRect.equals( aRect ) ) return false;

        final float x = aRect.x * myScaleFactorX;
        final float y = aRect.y * myScaleFactorY;
        final float scaledWidth = aRect.width * myScaleFactorX;
        final float scaledHeight = aRect.height * myScaleFactorY;
        theCropWorkspace[ 0 ] = (int) x;
        theCropWorkspace[ 1 ] = (int) ( y + scaledHeight );
        theCropWorkspace[ 2 ] = (int) scaledWidth;
        theCropWorkspace[ 3 ] = (int) -scaledHeight;

        aGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );

        myActiveCropRect.setTo( aRect );

        return true;
        }

    // Protected API

    protected final void setScaledSize( final float aWidth, final float aHeight )
        {
        myScaledWidth = aWidth;
        myScaledHeight = aHeight;
        myScaleFactorX = myWidth / myScaledWidth;
        myScaleFactorY = myHeight / myScaledHeight;
        }

    protected final void makeOpenglTexture( final Bitmap aBitmapARGB32 )
        {
        myWidth = aBitmapARGB32.getWidth();
        myHeight = aBitmapARGB32.getHeight();

        if ( id == 0 ) makeNewOpenglTexture();

        if ( allowUseOfGlUtils )
            {
            GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, aBitmapARGB32, 0 );
            }
        else
            {
            makeTexImageFromBitmapARGB32( aBitmapARGB32 );
            }
        }

    // Implementation

    private void makeTexImageFromBitmapARGB32( final Bitmap aBitmap )
        {
        //#if DEBUG
        Assert.equals( "texture width matches", myWidth, aBitmap.getWidth() );
        Assert.equals( "texture height matches", myHeight, aBitmap.getHeight() );
        //#endif

        final int numberOfPixels = myWidth * myHeight;
        final int[] data = new int[numberOfPixels];
        aBitmap.getPixels( data, 0, myWidth, 0, 0, myWidth, myHeight );

        for ( int idx = 0; idx < numberOfPixels; ++idx )
            {
            final int pixel = data[ idx ];
            final int alpha = pixel & MASK_ALPHA_32;
            final int red = ( pixel & MASK_RED_32 ) >> SHIFT_SWITCH_RGB_BGR;
            final int green = pixel & MASK_GREEN_32;
            final int blue = ( pixel & MASK_BLUE_32 ) << SHIFT_SWITCH_RGB_BGR;
            data[ idx ] = alpha | red | green | blue;
            }

        final IntBuffer dataBuffer = IntBuffer.wrap( data );
        myGL.glTexImage2D( GL10.GL_TEXTURE_2D, MIPMAP_LEVEL_ZERO, INTERNAL_TEXTURE_FORMAT_RGBA,
                           myWidth, myHeight, BORDER_SIZE_ZERO,
                           BITMAP_FORMAT_RGBA, BITMAP_DATA_FORMAT, dataBuffer );
        }

    private void makeNewOpenglTexture()
        {
        //#if DEBUG
        Assert.isTrue( "no opengl set", id == 0 );
        //#endif

        mTextureNameWorkspace[ 0 ] = 0;
        myGL.glGenTextures( 1, mTextureNameWorkspace, 0 );
        id = mTextureNameWorkspace[ 0 ];

        //#if DEBUG
        Log.debug( "new texture id: {}", id );
        //#endif

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, id );
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
        }


    private GL10 myGL;

    protected int myWidth;

    protected int myHeight;

    protected float myScaledWidth;

    protected float myScaledHeight;

    private float myScaleFactorX = 1;

    private float myScaleFactorY = 1;

    private final int[] mTextureNameWorkspace = new int[1];

    protected final Rectangle myActiveCropRect = new Rectangle();

    protected static final int[] theCropWorkspace = new int[4];

    private static final int SHIFT_SWITCH_RGB_BGR = 16;

    private static final int MASK_ALPHA_32 = 0xFF000000;

    private static final int MASK_RED_32 = 0x00FF0000;

    private static final int MASK_GREEN_32 = 0x0000FF00;

    private static final int MASK_BLUE_32 = 0x000000FF;

    private static final int BORDER_SIZE_ZERO = 0;

    private static final int MIPMAP_LEVEL_ZERO = 0;

    private static final int BITMAP_FORMAT_RGBA = GL10.GL_RGBA;

    private static final int INTERNAL_TEXTURE_FORMAT_RGBA = GL10.GL_RGBA;

    private static final int BITMAP_DATA_FORMAT = GL10.GL_UNSIGNED_BYTE;
    }
