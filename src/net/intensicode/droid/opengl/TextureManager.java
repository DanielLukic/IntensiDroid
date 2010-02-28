package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;

public final class TextureManager
    {
    public final void addTexture( final AndroidImageResource aImageResource )
        {
        final String imageId = aImageResource.resourcePath;
        if ( configuredForAtlas( imageId ) )
            {
            myAtlasTextureManager.addTexture( aImageResource );
            }
        else
            {
            myDirectTextureManager.addTexture( aImageResource );
            }
        }

    public final void purgeAllTextures()
        {
        myAtlasTextureManager.purgeAllTextures();
        myDirectTextureManager.purgeAllTextures();
        }

    // Implementation

    private boolean configuredForAtlas( final String aImageId )
        {
        // TODO: Get this from a configuration..
        if ( "logo_anim.png".equals( aImageId ) ) return false;
        if ( "title_logo.png".equals( aImageId ) ) return false;
        if ( "title_background.png".equals( aImageId ) ) return false;
        return true;
        }


    private final AtlasTextureManager myAtlasTextureManager = new AtlasTextureManager();

    private final DirectTextureManager myDirectTextureManager = new DirectTextureManager();
    }
