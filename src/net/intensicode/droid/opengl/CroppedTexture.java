package net.intensicode.droid.opengl;

import android.graphics.*;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;

public final class CroppedTexture extends Texture
    {
    public CroppedTexture( final GL10 aGL )
        {
        super( aGL );
        }

    public final void makeUsing( final Bitmap aOriginalBitmap, final int aProperWidth, final int aProperHeight )
        {
        final Bitmap bitmap = makeProperBitmap( aOriginalBitmap, aProperWidth, aProperHeight );
        super.makeUsing( bitmap );
        bitmap.recycle();

        setScaledSize( aOriginalBitmap.getWidth(), aOriginalBitmap.getHeight() );
        }

    // From Texture

    public void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / (float) myWidth;
        aMatrix4x4[ 5 ] = -aSourceRect.height / (float) myHeight;
        aMatrix4x4[ 12 ] = aSourceRect.x / (float) myWidth;
        aMatrix4x4[ 13 ] = aSourceRect.y / (float) myHeight - aMatrix4x4[ 5 ];
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

    // Implementation

    private Bitmap makeProperBitmap( final Bitmap aBitmap, final int aWidth, final int aHeight )
        {
        final Bitmap bitmap = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        myTextureCloneCanvas.setBitmap( bitmap );
        myTextureCloneCanvas.drawBitmap( aBitmap, 0, 0, myTextureClonePaint );

        //#if DEBUG
        Log.debug( "created proper texture bitmap" );
        Log.debug( "bitmap size: {}x{}", aBitmap.getWidth(), aBitmap.getHeight() );
        Log.debug( "proper size: {}x{}", aWidth, aHeight );
        //#endif

        return bitmap;
        }


    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();
    }
