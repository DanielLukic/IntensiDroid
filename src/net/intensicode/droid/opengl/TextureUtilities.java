package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import net.intensicode.util.Log;
import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;

public final class TextureUtilities
    {
    public static final int NO_TEXTURE_ID_SET = -1;

    // Note: Galaxy and G1 do not like 1024.. terrible slow downs.. assuming others have problems, too..

    public static final int MAX_SAFE_TEXTURE_SIZE = 512;

    public static int maximumTextureSize = MAX_SAFE_TEXTURE_SIZE;

    public static boolean allowUseOfGlUtils = true;


    public final void attach( final GL10 aGL )
        {
        if ( aGL == null ) throw new NullPointerException( "real GL must not be null" );
        myGL = aGL;
        theActiveTextureUnit = UNKNOWN_TEXTURE_UNIT_ID;
        for ( int idx = 0; idx < BOUND_TEXTURE_IDS.length; idx++ ) BOUND_TEXTURE_IDS[ idx ] = NO_TEXTURE_ID_SET;
        }

    public final void setAtlasTextureUnit()
        {
        if ( theActiveTextureUnit == ATLAS_TEXTURE_UNIT_ID ) return;

        myGL.glActiveTexture( GL10.GL_TEXTURE1 );
        myGL.glClientActiveTexture( GL10.GL_TEXTURE1 );

        theActiveTextureUnit = ATLAS_TEXTURE_UNIT_ID;
        }

    public final void setRenderTextureUnit()
        {
        if ( theActiveTextureUnit == RENDER_TEXTURE_UNIT_ID ) return;

        myGL.glActiveTexture( GL10.GL_TEXTURE0 );
        myGL.glClientActiveTexture( GL10.GL_TEXTURE0 );

        theActiveTextureUnit = RENDER_TEXTURE_UNIT_ID;
        }

    public final int makeNewOpenglTexture()
        {
        final int[] workspace = new int[1];
        workspace[ 0 ] = NO_TEXTURE_ID_SET;
        myGL.glGenTextures( 1, workspace, 0 );

        final int id = workspace[ 0 ];
        myGL.glBindTexture( GL10.GL_TEXTURE_2D, id );
        myGL.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT );
        myGL.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT );

        //#if DEBUG
        Log.debug( "new texture id: {}", id );
        //#endif

        return id;
        }

    public static IntBuffer getPixelDataABGR( final Bitmap aBitmap, final int aWidth, final int aHeight )
        {
        final int numberOfPixels = aWidth * aHeight;
        final int[] data = new int[numberOfPixels];
        aBitmap.getPixels( data, 0, aWidth, 0, 0, aWidth, aHeight );

        for ( int idx = 0; idx < numberOfPixels; ++idx )
            {
            final int pixel = data[ idx ];
            final int alpha = pixel & MASK_ALPHA_32;
            final int red = ( pixel & MASK_RED_32 ) >> SHIFT_SWITCH_RGB_BGR;
            final int green = pixel & MASK_GREEN_32;
            final int blue = ( pixel & MASK_BLUE_32 ) << SHIFT_SWITCH_RGB_BGR;
            data[ idx ] = alpha | red | green | blue;
            }

        return IntBuffer.wrap( data );
        }

    public final void makeEmptyTexture( final int aWidth, final int aHeight )
        {
        myGL.glTexImage2D( GL10.GL_TEXTURE_2D, MIPMAP_LEVEL_ZERO, INTERNAL_TEXTURE_FORMAT_RGBA,
                           aWidth, aHeight, BORDER_SIZE_ZERO,
                           BITMAP_FORMAT_RGBA, BITMAP_DATA_FORMAT, null );
        }

    public final void makeTexImageFromBitmapARGB32( final Bitmap aBitmap )
        {
        final int width = aBitmap.getWidth();
        final int height = aBitmap.getHeight();

        final IntBuffer dataBuffer = getPixelDataABGR( aBitmap, width, height );
        myGL.glTexImage2D( GL10.GL_TEXTURE_2D, MIPMAP_LEVEL_ZERO, INTERNAL_TEXTURE_FORMAT_RGBA,
                           width, height, BORDER_SIZE_ZERO,
                           BITMAP_FORMAT_RGBA, BITMAP_DATA_FORMAT, dataBuffer );
        }

    public final void makeSubTexImageFromBitmapARGB32( final Bitmap aBitmap, final int aX, final int aY )
        {
        final int width = aBitmap.getWidth();
        final int height = aBitmap.getHeight();

        final IntBuffer dataBuffer = getPixelDataABGR( aBitmap, width, height );
        myGL.glTexSubImage2D( GL10.GL_TEXTURE_2D, MIPMAP_LEVEL_ZERO, aX, aY, width, height,
                              BITMAP_FORMAT_RGBA, BITMAP_DATA_FORMAT, dataBuffer );
        }

    public final void setTexturePixels( final Bitmap aBitmapARGB32 )
        {
        if ( allowUseOfGlUtils && aBitmapARGB32.getConfig() != null )
            {
            GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, aBitmapARGB32, 0 );
            }
        else
            {
            makeTexImageFromBitmapARGB32( aBitmapARGB32 );
            }
        }

    public final void setTextureSubPixels( final Bitmap aBitmapARGB32, final int aX, final int aY )
        {
        if ( allowUseOfGlUtils && aBitmapARGB32.getConfig() != null )
            {
            GLUtils.texSubImage2D( GL10.GL_TEXTURE_2D, 0, aX, aY, aBitmapARGB32 );
            }
        else
            {
            makeSubTexImageFromBitmapARGB32( aBitmapARGB32, aX, aY );
            }
        }

    public final void bindTexture( final int aId )
        {
        if ( BOUND_TEXTURE_IDS[ theActiveTextureUnit ] == aId ) return;

        myGL.glBindTexture( GL10.GL_TEXTURE_2D, aId );

        BOUND_TEXTURE_IDS[ theActiveTextureUnit ] = aId;
        }

    public final void setTextureCropRect( final Rectangle aRectangle )
        {
        theCropWorkspace[ 0 ] = aRectangle.x;
        theCropWorkspace[ 1 ] = aRectangle.y + aRectangle.height;
        theCropWorkspace[ 2 ] = aRectangle.width;
        theCropWorkspace[ 3 ] = -aRectangle.height;

        final GL11 gl11 = (GL11) myGL;
        gl11.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );
        }

    public final void purge( final int aOglTextureId )
        {
        final int[] workspace = new int[1];
        workspace[ 0 ] = aOglTextureId;
        myGL.glDeleteTextures( 1, workspace, 0 );

        for ( int idx = 0; idx < BOUND_TEXTURE_IDS.length; idx++ )
            {
            if ( BOUND_TEXTURE_IDS[ idx ] != aOglTextureId ) continue;
            BOUND_TEXTURE_IDS[ idx ] = NO_TEXTURE_ID_SET;
            }
        }


    private GL10 myGL = NoGL.INSTANCE;

    private static final int UNKNOWN_TEXTURE_UNIT_ID = -1;

    private static final int RENDER_TEXTURE_UNIT_ID = 0;

    private static final int ATLAS_TEXTURE_UNIT_ID = 1;

    private static int theActiveTextureUnit = RENDER_TEXTURE_UNIT_ID;

    private static final int[] theCropWorkspace = new int[4];

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

    private static final int NUMBER_OF_TEXTURE_UNITS = 2;

    private static final int[] BOUND_TEXTURE_IDS = new int[NUMBER_OF_TEXTURE_UNITS];
    }
