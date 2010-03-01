package net.intensicode.droid.opengl;

import net.intensicode.util.Rectangle;

public class AtlasTexture implements Texture
    {
    public AtlasTexture( final TextureAtlasTexture aTextureAtlasTexture, final Rectangle aAtlasRectangle )
        {
        myAtlasTexture = aTextureAtlasTexture;
        myAtlasRectangle.setTo( aAtlasRectangle );
        }

    public final void copyAtlasRectTo( final Rectangle aRectangle )
        {
        aRectangle.setTo( myAtlasRectangle );
        }

    // From Texture

    public final void bind()
        {
        myAtlasTexture.bind();
        }

    public final void setMatrix( final float[] aMatrix4x4, final Rectangle aSourceRect )
        {
        myAtlasTexture.setMatrix( aMatrix4x4, aSourceRect, myAtlasRectangle );
        }

    public final void setTextureCrop( final Rectangle aRect )
        {
        myAtlasTexture.cropTextureIfNecessary( aRect, myAtlasRectangle );
        }


    private final TextureAtlasTexture myAtlasTexture;

    private final Rectangle myAtlasRectangle = new Rectangle();
    }
