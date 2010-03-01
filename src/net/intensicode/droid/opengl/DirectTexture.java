package net.intensicode.droid.opengl;

import android.graphics.*;
import net.intensicode.util.*;

public final class DirectTexture implements Texture
    {
    public final void makeUsing( final Bitmap aOriginalBitmap, final int aProperWidth, final int aProperHeight )
        {
        final Bitmap bitmap = makeProperBitmap( aOriginalBitmap, aProperWidth, aProperHeight );
        makeUsing( bitmap );
        bitmap.recycle();
        }

    public final void makeUsing( final Bitmap aBitmap )
        {
        //#if DEBUG
        Assert.isFalse( "not made", myHasTextureIdFlag );
        //#endif

        myWidth = aBitmap.getWidth();
        myHeight = aBitmap.getHeight();
        makeOpenglTexture( aBitmap );
        }

    public final void purge()
        {
        //#if DEBUG
        Assert.isTrue( "still valid", myHasTextureIdFlag );
        //#endif

        TextureUtilities.purge( myOglTextureId );
        myHasTextureIdFlag = false;
        }

    // From Texture

    public final void bind()
        {
        TextureUtilities.bindTexture( myOglTextureId );
        }

    public void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        aMatrix4x4[ TEXTURE_MATRIX_INDEX_OF_WIDTH ] = aSourceRect.width / (float) myWidth;
        aMatrix4x4[ TEXTURE_MATRIX_INDEX_OF_HEIGHT ] = -aSourceRect.height / (float) myHeight;
        aMatrix4x4[ TEXTURE_MATRIX_INDEX_OF_X ] = aSourceRect.x / (float) myWidth;
        aMatrix4x4[ TEXTURE_MATRIX_INDEX_OF_Y ] = aSourceRect.y / (float) myHeight - aMatrix4x4[ 5 ];
        }

    public final void setTextureCrop( final Rectangle aRect )
        {
        if ( myActiveCropRect.equals( aRect ) ) return;
        TextureUtilities.setTextureCropRect( aRect );
        myActiveCropRect.setTo( aRect );
        }

    // Implementation

    private void makeOpenglTexture( final Bitmap aBitmapARGB32 )
        {
        myWidth = aBitmapARGB32.getWidth();
        myHeight = aBitmapARGB32.getHeight();

        myOglTextureId = TextureUtilities.makeNewOpenglTexture();
        myHasTextureIdFlag = true;

        TextureUtilities.setTexturePixels( aBitmapARGB32 );
        }

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


    private int myWidth;

    private int myHeight;

    private int myOglTextureId;

    private boolean myHasTextureIdFlag;

    private final Paint myTextureClonePaint = new Paint();

    private final Canvas myTextureCloneCanvas = new Canvas();

    private final Rectangle myActiveCropRect = new Rectangle();
    }