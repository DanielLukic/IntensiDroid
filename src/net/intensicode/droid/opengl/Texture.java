package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;

public abstract class Texture
    {
    public boolean allowUseOfGlUtils;

    public int id;

    public int width;

    public int height;


    public final void purge()
        {
        mTextureNameWorkspace[ 0 ] = id;
        myGL.glDeleteTextures( 1, mTextureNameWorkspace, 0 );
        id = 0;
        }

    // Abstract Interface

    public abstract boolean isFullRect( final Rectangle aRectangle );

    public abstract void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect );

    public abstract boolean cropTextureIfNecessary( final GL11 aGL, final Rectangle aRect );

    // Protected API

    protected Texture( final GL10 aGL )
        {
        myGL = aGL;
        }

    protected final void makeOpenglTexture( final Bitmap aBitmapARGB32 )
        {
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
        Assert.equals( "texture width matches", width, aBitmap.getWidth() );
        Assert.equals( "texture height matches", height, aBitmap.getHeight() );
        //#endif

        final int numberOfPixels = width * height;
        final int[] data = new int[numberOfPixels];
        aBitmap.getPixels( data, 0, width, 0, 0, width, height );

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
                           width, height, BORDER_SIZE_ZERO,
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

    private final int[] mTextureNameWorkspace = new int[1];

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
