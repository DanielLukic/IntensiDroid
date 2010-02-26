package net.intensicode.droid.opengl;

import android.graphics.*;
import android.opengl.GLUtils;
import net.intensicode.droid.*;
import net.intensicode.util.Log;

import javax.microedition.khronos.opengles.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class TextureManager implements TexturePurger
    {
    public GL10 gl;

    public boolean useGlUtils;

    public boolean useDrawTextureExtension;


    public void makeTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "making texture for {}", aImageResource.resourcePath );
        //#endif

        myTexturizedImageResources.add( aImageResource );

        mTextureNameWorkspace[ 0 ] = 0;
        gl.glGenTextures( 1, mTextureNameWorkspace, 0 );

        final int textureName = aImageResource.textureId = mTextureNameWorkspace[ 0 ];
        //#if DEBUG
        Log.debug( "new texture id: {}", textureName );
        //#endif

        gl.glBindTexture( GL10.GL_TEXTURE_2D, textureName );

        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );

        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );

        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );

        final Bitmap originalBitmap = aImageResource.bitmap;

        final int originalWidth = originalBitmap.getWidth();
        final int properWidth = findNextPowerOfTwo( originalWidth );
        if ( properWidth > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();

        final int originalHeight = originalBitmap.getHeight();
        final int properHeight = findNextPowerOfTwo( originalHeight );
        if ( properHeight > MAX_TEXTURE_SIZE_IN_PIXELS ) throw new IllegalArgumentException();

        final Bitmap bitmap = makeProperBitmap( originalBitmap, properWidth, properHeight );

        aImageResource.textureWidth = bitmap.getWidth();
        aImageResource.textureHeight = bitmap.getHeight();

        aImageResource.texturePurger = this;

        if ( useGlUtils )
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
            gl.glBindTexture( GL10.GL_TEXTURE_2D, textureName );
            gl.glTexImage2D( GL10.GL_TEXTURE_2D,
                             0,                      // Mipmap level.
                             GL10.GL_RGBA,           // Internal format.
                             bitmap.getWidth(),
                             bitmap.getHeight(),
                             0,                      // Border.
                             GL10.GL_RGBA,           // Format.
                             GL10.GL_UNSIGNED_BYTE,
                             bitmap_data_buffer );
            }

        if ( useDrawTextureExtension )
            {
            mCropWorkspace[ 0 ] = 0;
            mCropWorkspace[ 1 ] = bitmap.getHeight();
            mCropWorkspace[ 2 ] = bitmap.getWidth();
            mCropWorkspace[ 3 ] = -bitmap.getHeight();

            ( (GL11) gl ).glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0 );
            }

        if ( bitmap != originalBitmap ) bitmap.recycle(); // only the created ones..
        }

    public void purgeAllTextures()
        {
        //#if DEBUG
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif
        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.get( myTexturizedImageResources.size() - 1 ) );
            }
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "purging texture {} ({})", aImageResource.textureId, aImageResource.resourcePath );
        //#endif

        mTextureNameWorkspace[ 0 ] = aImageResource.textureId;
        gl.glDeleteTextures( 1, mTextureNameWorkspace, 0 );

        aImageResource.textureId = aImageResource.textureWidth = aImageResource.textureHeight = 0;
        aImageResource.texturePurger = null;

        final boolean removed = myTexturizedImageResources.remove( aImageResource );
        //#if DEBUG
        if ( !removed ) Log.debug( "failed removing texturized image from internal list" );
        //#endif
        }

    // Implementation

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


    private final int[] mCropWorkspace = new int[4];

    private final int[] mTextureNameWorkspace = new int[1];

    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();

    private final Rect myTextureCloneSourceRect = new Rect();

    private final Rect myTextureCloneTargetRect = new Rect();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();

    private static final int MAX_TEXTURE_SIZE_IN_PIXELS = 512;
    }
