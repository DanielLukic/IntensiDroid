package net.intensicode.droid.opengl;

import android.graphics.Bitmap;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.util.Log;

import java.util.ArrayList;

public final class AtlasTextureManager
    {
    public final void addTexture( final AndroidImageResource aImageResource )
        {
        //#if DEBUG_OPENGL
        Log.debug( "inserting texture for {} into atlas", aImageResource.resourcePath );
        //#endif

        final TextureAtlas atlas = getAtlasWithEnoughRoomFor( aImageResource );
        atlas.add( aImageResource );
        }

    public final Bitmap[] dumpTextureAtlases()
        {
        final Bitmap[] bitmaps = new Bitmap[myTextureAtlases.size()];
        for ( int idx = 0; idx < myTextureAtlases.size(); idx++ )
            {
            bitmaps[ idx ] = myTextureAtlases.get( idx ).dumpLayout();
            }
        return bitmaps;
        }

    private TextureAtlas getAtlasWithEnoughRoomFor( final AndroidImageResource aImageResource )
        {
        final int numberOfAtlases = myTextureAtlases.size();
        for ( int idx = 0; idx < numberOfAtlases; idx++ )
            {
            final TextureAtlas atlas = myTextureAtlases.get( idx );
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

    private TextureAtlas createNewAtlas()
        {
        final int newAtlasId = myTextureAtlases.size() + 1;
        final FreeAreaTrackingTextureAtlas newAtlas = new FreeAreaTrackingTextureAtlas( newAtlasId );
        myTextureAtlases.add( newAtlas );

        //#if DEBUG_OPENGL
        Log.debug( "new texture atlas created: {}", newAtlas );
        //#endif

        return newAtlas;
        }


    private final ArrayList<FreeAreaTrackingTextureAtlas> myTextureAtlases = new ArrayList<FreeAreaTrackingTextureAtlas>();
    }
