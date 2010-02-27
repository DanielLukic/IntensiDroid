package net.intensicode.droid.opengl;

import android.graphics.*;
import net.intensicode.util.*;

import javax.microedition.khronos.opengles.*;

public final class ProperTexture extends Texture
    {
    public ProperTexture( final GL10 aGL )
        {
        super( aGL );
        }

    public final void makeUsing( final Bitmap aOriginalBitmap, final int aProperWidth, final int aProperHeight )
        {
        width = aProperWidth;
        height = aProperHeight;
        myOriginalWidth = aOriginalBitmap.getWidth();
        myOriginalHeight = aOriginalBitmap.getHeight();

        final Bitmap bitmap = makeProperBitmap( aOriginalBitmap, aProperWidth, aProperHeight );
        makeOpenglTexture( bitmap );
        bitmap.recycle();
        }

    // From Texture

    public final boolean isFullRect( final Rectangle aRectangle )
        {
        return aRectangle.x == 0 && aRectangle.y == 0 && aRectangle.width == myOriginalWidth && aRectangle.height == myOriginalHeight;
        }

    public final void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ 0 ] = aSourceRect.width / (float) myOriginalWidth;
        aMatrix4x4[ 5 ] = -aSourceRect.height / (float) myOriginalHeight;
        aMatrix4x4[ 12 ] = aSourceRect.x / (float) myOriginalWidth;
        aMatrix4x4[ 13 ] = aSourceRect.y / (float) myOriginalHeight - aMatrix4x4[ 5 ];
        }

    public final boolean cropTextureIfNecessary( final GL11 aGL, final Rectangle aRect )
        {
        if ( myActiveCropRect.equals( aRect ) ) return false;

        final float xFactor = width / (float) myOriginalWidth;
        final float yFactor = height / (float) myOriginalHeight;
        final float x = aRect.x * xFactor;
        final float y = aRect.y * yFactor;
        final float scaledWidth = aRect.width * xFactor;
        final float scaledHeight = aRect.height * yFactor;
        theCropWorkspace[ 0 ] = (int) x;
        theCropWorkspace[ 1 ] = (int) ( y + scaledHeight );
        theCropWorkspace[ 2 ] = (int) scaledWidth;
        theCropWorkspace[ 3 ] = (int) -scaledHeight;

        aGL.glTexParameteriv( GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, theCropWorkspace, 0 );

        myActiveCropRect.setTo( aRect );

        return true;
        }

    // Implementation

    private Bitmap makeProperBitmap( final Bitmap aBitmap, final int aWidth, final int aHeight )
        {
        final Bitmap bitmap = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        myTextureCloneCanvas.setBitmap( bitmap );
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

        return bitmap;
        }


    private int myOriginalWidth;

    private int myOriginalHeight;

    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();

    private final Rect myTextureCloneSourceRect = new Rect();

    private final Rect myTextureCloneTargetRect = new Rect();

    private final Rectangle myActiveCropRect = new Rectangle();

    private static final int[] theCropWorkspace = new int[4];
    }
