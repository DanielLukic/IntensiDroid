package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.util.Rectangle;

import javax.microedition.khronos.opengles.*;

public final class DirectTexture extends Texture
    {
    public DirectTexture( final GL10 aGL )
        {
        super( aGL );
        }

    public final void makeUsing( final Bitmap aBitmap )
        {
        width = aBitmap.getWidth();
        height = aBitmap.getHeight();
        makeOpenglTexture( aBitmap );
        }

    public final boolean isFullRect( final Rectangle aRectangle )
        {
        return aRectangle.x == 0 && aRectangle.y == 0 && aRectangle.width == width && aRectangle.height == height;
        }

    public final void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / (float) width;
        aMatrix4x4[ 5 ] = -aSourceRect.height / (float) height;
        aMatrix4x4[ 12 ] = aSourceRect.x / (float) width;
        aMatrix4x4[ 13 ] = aSourceRect.y / (float) height - aMatrix4x4[ 5 ];
        }

    public final boolean cropTextureIfNecessary( final GL11 aGL, final Rectangle aRect )
        {
        if ( myActiveCropRect.equals( aRect ) ) return false;

        theCropWorkspace[ 0 ] = aRect.x;
        theCropWorkspace[ 1 ] = aRect.y + aRect.height;
        theCropWorkspace[ 2 ] = aRect.width;
        theCropWorkspace[ 3 ] = -aRect.height;

        aGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );

        myActiveCropRect.setTo( aRect );

        return true;
        }


    private final Rectangle myActiveCropRect = new Rectangle();

    private static final int[] theCropWorkspace = new int[4];
    }
