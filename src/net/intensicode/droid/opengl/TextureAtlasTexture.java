package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.util.*;

public final class TextureAtlasTexture
    {
    public final void make( final int aWidth, final int aHeight )
        {
        //#if DEBUG
        Assert.isFalse( "not made", myHasTextureIdFlag );
        //#endif

        myWidth = aWidth;
        myHeight = aHeight;
        myOglTextureId = TextureUtilities.makeNewOpenglTexture();
        myHasTextureIdFlag = true;

        TextureUtilities.makeEmptyTexture( aWidth, aHeight );
        }

    public final void add( final Bitmap aBitmap, final int aX, final int aY )
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( myOglTextureId );
        TextureUtilities.setTextureSubPixels( aBitmap, aX, aY );
        TextureUtilities.setRenderTextureUnit();
        }

    public final void bind()
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        TextureUtilities.bindTexture( myOglTextureId );
        }

    public void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect, final Rectangle aAtlasRectangle )
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_WIDTH ] = aSourceRect.width / (float) myWidth;
        aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_HEIGHT ] = -aSourceRect.height / (float) myHeight;
        aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_X ] = ( aAtlasRectangle.x + aSourceRect.x ) / (float) myWidth;
        aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_Y ] = ( aAtlasRectangle.y + aSourceRect.y ) / (float) myHeight - aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_HEIGHT ];
        }

    public void cropTextureIfNecessary( final Rectangle aSourceRect, final Rectangle aAtlasRectangle )
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        myCropCheckRectangle.setTo( aSourceRect );
        myCropCheckRectangle.x += aAtlasRectangle.x;
        myCropCheckRectangle.y += aAtlasRectangle.y;

        if ( myActiveCropRect.equals( myCropCheckRectangle ) ) return;
        TextureUtilities.setTextureCropRect( myCropCheckRectangle );
        myActiveCropRect.setTo( myCropCheckRectangle );
        }

    public final void purge()
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        TextureUtilities.purge( myOglTextureId );
        myHasTextureIdFlag = false;
        }


    private int myWidth;

    private int myHeight;

    private int myOglTextureId;

    private boolean myHasTextureIdFlag;

    private final Rectangle myActiveCropRect = new Rectangle();

    private final Rectangle myCropCheckRectangle = new Rectangle();
    }
