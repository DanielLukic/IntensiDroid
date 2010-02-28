package net.intensicode.droid.opengl;

import net.intensicode.droid.*;
import net.intensicode.util.*;

import java.util.ArrayList;

public final class TextureAtlasTexture implements TexturePurger
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

    public final void add( final AndroidImageResource aImageResource, final int aX, final int aY )
        {
        //#if DEBUG
        Assert.isTrue( "made", myHasTextureIdFlag );
        //#endif

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( myOglTextureId );
        TextureUtilities.setTextureSubPixels( aImageResource.bitmap, aX, aY );
        TextureUtilities.setRenderTextureUnit();

        myAtlasRectangleWorkspace.x = aX;
        myAtlasRectangleWorkspace.y = aY;
        myAtlasRectangleWorkspace.width = aImageResource.getWidth();
        myAtlasRectangleWorkspace.height = aImageResource.getHeight();

        aImageResource.texture = new AtlasTexture( this, myAtlasRectangleWorkspace );
        aImageResource.texturePurger = this;

        myTexturizedImageResources.add( aImageResource );
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
        aMatrix4x4[ Texture.TEXTURE_MATRIX_INDEX_OF_Y ] = ( aAtlasRectangle.y + aSourceRect.y ) / (float) myHeight - aMatrix4x4[ 5 ];
        }

    private final Rectangle myCropCheckRectangle = new Rectangle();

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

        //#if DEBUG
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif
        while ( myTexturizedImageResources.size() > 0 )
            {
            purge( myTexturizedImageResources.remove( myTexturizedImageResources.size() - 1 ) );
            }

        TextureUtilities.purge( myOglTextureId );
        myHasTextureIdFlag = false;
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        aImageResource.texture = null;
        aImageResource.texturePurger = null;
        }


    private int myWidth;

    private int myHeight;

    private int myOglTextureId;

    private boolean myHasTextureIdFlag;

    private final Rectangle myActiveCropRect = new Rectangle();

    private final Rectangle myAtlasRectangleWorkspace = new Rectangle();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();
    }
