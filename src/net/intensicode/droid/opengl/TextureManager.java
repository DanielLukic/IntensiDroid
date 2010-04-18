package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.core.Configuration;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Log;

public final class TextureManager
    {
    public final void setConfiguration( final Configuration aConfiguration )
        {
        myConfiguration = aConfiguration;
        }

    public final Bitmap[] dumpTextureAtlases()
        {
        return myAtlasTextureManager.dumpTextureAtlases();
        }

    public final void addTexture( final AndroidImageResource aImageResource )
        {
        final String imageId = getImageOrDefaultId( aImageResource );
        if ( configuredAsDirectTexture( imageId ) )
            {
            myDirectTextureManager.addTexture( aImageResource );
            }
        else
            {
            final AtlasHints hints = getConfiguredAtlasHints( imageId );
            myAtlasTextureManager.addTexture( aImageResource, hints );
            }
        }

    private AtlasHints getConfiguredAtlasHints( final String aImageId )
        {
        try
            {
            final String hints = myConfiguration.readString( aImageId, "atlas", null );
            if ( hints == null ) return NO_ATLAS_HINTS;
            return AtlasHints.parse( hints );
            }
        catch ( final Exception e )
            {
            Log.error( "bad atlas hints for {} ignored", aImageId, e );
            return NO_ATLAS_HINTS;
            }
        }

    private String getImageOrDefaultId( final AndroidImageResource aImageResource )
        {
        final String aResourcePath = aImageResource.resourcePath;
        if ( aResourcePath == null || aResourcePath.length() == 0 )
            {
            throw new IllegalArgumentException( aImageResource.toString() );
            }

        final int lastDotPos = aResourcePath.lastIndexOf( '.' );
        if ( lastDotPos == -1 ) return aResourcePath;
        return aResourcePath.substring( 0, lastDotPos );
        }

    public final void purgeAllTextures()
        {
        myAtlasTextureManager.purgeAllTextures();
        myDirectTextureManager.purgeAllTextures();

        TextureUtilities.setAtlasTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        TextureUtilities.setRenderTextureUnit();
        TextureUtilities.bindTexture( TextureUtilities.NO_TEXTURE_ID_SET );
        }

    // Implementation

    private boolean configuredAsDirectTexture( final String aImageId )
        {
        //#if DEBUG
        net.intensicode.util.Log.debug( "TextureManager looking up configuration for {}", aImageId );
        //#endif
        return myConfiguration.readBoolean( aImageId, "direct", DEFAULT_DIRECT_TEXTURE_CONFIGURATION );
        }


    private Configuration myConfiguration = Configuration.NULL_CONFIGURATION;

    private final AtlasTextureManager myAtlasTextureManager = new AtlasTextureManager();

    private final DirectTextureManager myDirectTextureManager = new DirectTextureManager();

    private static final boolean DEFAULT_DIRECT_TEXTURE_CONFIGURATION = false;

    private static final AtlasHints NO_ATLAS_HINTS = new AtlasHints();
    }
