package net.intensicode.droid.opengl;

import net.intensicode.core.Configuration;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Log;

public final class TextureManager
    {
    public final AtlasTextureManager atlasTextureManager = new AtlasTextureManager();

    public final DirectTextureManager directTextureManager = new DirectTextureManager();


    public final void setConfiguration( final Configuration aConfiguration )
        {
        myConfiguration = aConfiguration;
        myDefaultDirectTextureFlag = myConfiguration.readBoolean( "direct_by_default", true );
        }

    public final void addTexture( final AndroidImageResource aImageResource )
        {
        final String imageId = getImageOrDefaultId( aImageResource );
        if ( !configuredAsDirectTexture( imageId ) )
            {
            try
                {
                final AtlasHints hints = getConfiguredAtlasHints( imageId );
                atlasTextureManager.addTexture( aImageResource, hints );
                return;
                }
            catch ( final IllegalStateException e )
                {
                Log.error( e );
                }
            Log.info( "falling back to direct texture" );
            }

        directTextureManager.addTexture( aImageResource );
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
        atlasTextureManager.purgeAllTextures();
        directTextureManager.purgeAllTextures();

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
        final boolean isDirect = myConfiguration.readBoolean( aImageId, "direct", myDefaultDirectTextureFlag );
        Log.info( "texture {} direct? " + isDirect );
        return isDirect;
        }


    private boolean myDefaultDirectTextureFlag = true;

    private Configuration myConfiguration = Configuration.NULL_CONFIGURATION;

    private static final AtlasHints NO_ATLAS_HINTS = new AtlasHints();
    }
