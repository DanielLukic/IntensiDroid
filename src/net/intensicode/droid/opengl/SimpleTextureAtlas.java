package net.intensicode.droid.opengl;

import net.intensicode.droid.*;
import net.intensicode.util.Log;

public final class SimpleTextureAtlas implements TextureAtlas, TexturePurger
    {
    public SimpleTextureAtlas()
        {
        this( TextureUtilities.maximumTextureSize, TextureUtilities.maximumTextureSize );
        }

    public SimpleTextureAtlas( final int aWidth, final int aHeight )
        {
        myWidth = aWidth;
        myHeight = aHeight;
        }

    // From TextureAtlas

    public final boolean enoughRoomFor( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomAtCurrentPosition( aImageResource ) ) return true;
        if ( enoughRoomInNextColumn( aImageResource ) ) return true;
        return false;
        }

    public final void add( final AndroidImageResource aImageResource )
        {
        createAtlasTextureIfNecessary();

        if ( !enoughRoomAtCurrentPosition( aImageResource ) ) moveToNextColumn( aImageResource );

        //#if DEBUG
        Log.debug( "adding {} to texture atlas", aImageResource.resourcePath );
        Log.debug( "texture atlas insert position: {}x{}", myCurrentX, myCurrentY );
        //#endif

        myAtlasTexture.add( aImageResource, myCurrentX, myCurrentY );
        myCurrentY += aImageResource.getHeight();
        myNextX = Math.max( myNextX, myCurrentX + aImageResource.getWidth() );
        }

    public final void purge()
        {
        throw new UnsupportedOperationException();
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        throw new UnsupportedOperationException();
        }

    // Implementation

    private boolean enoughRoomAtCurrentPosition( final AndroidImageResource aImageResource )
        {
        if ( myCurrentX + aImageResource.getWidth() >= myWidth ) return false;
        if ( myCurrentY + aImageResource.getHeight() >= myHeight ) return false;
        return true;
        }

    private boolean enoughRoomInNextColumn( final AndroidImageResource aImageResource )
        {
        if ( myNextX + aImageResource.getWidth() >= myWidth ) return false;
        if ( myNextY + aImageResource.getHeight() >= myHeight ) return false;
        return true;
        }

    private void createAtlasTextureIfNecessary()
        {
        if ( myAtlasTexture != null ) return;
        myAtlasTexture = new TextureAtlasTexture();
        myAtlasTexture.make( myWidth, myHeight );
        }

    private void moveToNextColumn( final AndroidImageResource aImageResource )
        {
        if ( !enoughRoomInNextColumn( aImageResource ) ) throw new IllegalStateException( "texture atlas exhausted" );

        myCurrentX = myNextX;
        myCurrentY = myNextY;
        }


    private int myNextX;

    private int myNextY = 0;

    private int myCurrentX;

    private int myCurrentY;

    private TextureAtlasTexture myAtlasTexture;

    private final int myWidth;

    private final int myHeight;
    }
