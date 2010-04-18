package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Log;

import java.util.ArrayList;

public final class AtlasTextureManager
    {
    public final void addTexture( final AndroidImageResource aImageResource, final AtlasHints aAtlasHints )
        {
        //#if DEBUG_OPENGL
        Log.debug( "inserting texture for {} into atlas", aImageResource.resourcePath );
        //#endif

        final TextureAtlas atlas = getAtlasFor( aImageResource, aAtlasHints );
        if ( aAtlasHints.position != null )
            {
            atlas.add( aImageResource, aAtlasHints.position );
            }
        else
            {
            atlas.add( aImageResource );
            }
        }

    private TextureAtlas getAtlasFor( final AndroidImageResource aImageResource, final AtlasHints aAtlasHints )
        {
        if ( aAtlasHints.atlasId != null ) return getOrCreateAtlas( aAtlasHints.atlasId );
        return getAtlasWithEnoughRoomFor( aImageResource );
        }

    public final ArrayList<FreeAreaTrackingTextureAtlas> getTextureAtlases()
        {
        return myTextureAtlases;
        }

    private TextureAtlas getAtlasWithEnoughRoomFor( final AndroidImageResource aImageResource )
        {
        final int numberOfAtlases = myTextureAtlases.size();
        for ( int idx = 0; idx < numberOfAtlases; idx++ )
            {
            final TextureAtlas atlas = myTextureAtlases.get( idx );
            if ( myNamedAtlases.contains( atlas ) ) continue;
            if ( atlas.enoughRoomFor( aImageResource ) ) return atlas;
            }
        return createNewAtlas();
        }

    public final void purgeAllTextures()
        {
        //#if DEBUG_OPENGL
        Log.debug( "purging {} texture atlases", myTextureAtlases.size() );
        //#endif
        while ( myTextureAtlases.size() > 0 )
            {
            final TextureAtlas atlas = myTextureAtlases.remove( myTextureAtlases.size() - 1 );
            atlas.purge();
            }
        }

    // Implementation

    private TextureAtlas getOrCreateAtlas( final String aAtlasId )
        {
        for ( int idx = 0; idx < myNamedAtlases.size(); idx++ )
            {
            final FreeAreaTrackingTextureAtlas existingAtlas = myNamedAtlases.get( idx );
            if ( existingAtlas.is( aAtlasId ) ) return existingAtlas;
            }

        final FreeAreaTrackingTextureAtlas newAtlas = createNewAtlas( aAtlasId );
        myNamedAtlases.add( newAtlas );
        return newAtlas;
        }

    private FreeAreaTrackingTextureAtlas createNewAtlas( final String aAtlasId )
        {
        final FreeAreaTrackingTextureAtlas newAtlas = new FreeAreaTrackingTextureAtlas( aAtlasId );
        myTextureAtlases.add( newAtlas );

        //#if DEBUG_OPENGL
        Log.debug( "new texture atlas created: {}", newAtlas );
        //#endif

        return newAtlas;
        }

    private TextureAtlas createNewAtlas()
        {
        final int newAtlasId = myTextureAtlases.size() + 1;
        return getOrCreateAtlas( Integer.toString( newAtlasId ) );
        }


    private final ArrayList<FreeAreaTrackingTextureAtlas> myNamedAtlases = new ArrayList<FreeAreaTrackingTextureAtlas>();

    private final ArrayList<FreeAreaTrackingTextureAtlas> myTextureAtlases = new ArrayList<FreeAreaTrackingTextureAtlas>();
    }
