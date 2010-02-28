package net.intensicode.droid.opengl;

import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Log;

import java.util.ArrayList;

public final class AtlasTextureManager
    {
    public final void addTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Log.debug( "inserting texture for {} into atlas", aImageResource.resourcePath );
        //#endif
        if ( !myActiveAtlas.enoughRoomFor( aImageResource ) ) createNewAtlas();
        myActiveAtlas.add( aImageResource );
        }

    public final void purgeAllTextures()
        {
        //#if DEBUG
        Log.debug( "purging {} texture atlases", myTextureAtlases.size() );
        //#endif
        while ( myTextureAtlases.size() > 0 )
            {
            final TextureAtlas atlas = myTextureAtlases.remove( myTextureAtlases.size() - 1 );
            atlas.purge();
            }
        }

    // Implementation

    private void createNewAtlas()
        {
        //#if DEBUG
        Log.debug( "creating new texture atlas - id {}", myTextureAtlases.size() );
        //#endif
        myActiveAtlas = new SimpleTextureAtlas();
        myTextureAtlases.add( myActiveAtlas );
        }


    private TextureAtlas myActiveAtlas = new NullTextureAtlas();

    private final ArrayList<TextureAtlas> myTextureAtlases = new ArrayList<TextureAtlas>();
    }
