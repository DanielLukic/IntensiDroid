package net.intensicode.droid.opengl;

import net.intensicode.core.ImageResource;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import java.util.ArrayList;

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

    public final void setVerticalStrategy()
        {
        myStrategy = VERTICAL;
        }

    public final void setHorizontalStrategy()
        {
        myStrategy = HORIZONTAL;
        }

    private interface LayoutStrategy
        {
        void moveCursor( ImageResource aImageResource );
        }

    private final class VerticalStrategy implements LayoutStrategy
        {
        public final void moveCursor( final ImageResource aImageResource )
            {
            myCurrentY += aImageResource.getHeight();
            myNextX = Math.max( myNextX, myCurrentX + aImageResource.getWidth() );
            }
        }

    private final class HorizontalStrategy implements LayoutStrategy
        {
        public final void moveCursor( final ImageResource aImageResource )
            {
            myCurrentX += aImageResource.getWidth();
            myNextY = Math.max( myNextY, myCurrentY + aImageResource.getHeight() );
            }
        }

    private final LayoutStrategy VERTICAL = new VerticalStrategy();

    private final LayoutStrategy HORIZONTAL = new HorizontalStrategy();

    // From TextureAtlas

    public final boolean enoughRoomFor( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomAtCurrentPosition( aImageResource ) ) return true;
        if ( enoughRoomInNextPosition( aImageResource ) ) return true;
        return false;
        }

    public final void add( final AndroidImageResource aImageResource )
        {
        createAtlasTextureIfNecessary();

        if ( !enoughRoomAtCurrentPosition( aImageResource ) ) moveToNextLane( aImageResource );

        //#if DEBUG
        Log.debug( "adding {} to texture atlas", aImageResource.resourcePath );
        Log.debug( "texture atlas insert position: {}x{}", myCurrentX, myCurrentY );
        //#endif

        myAtlasTexture.add( aImageResource.bitmap, myCurrentX, myCurrentY );

        myAtlasRectangleWorkspace.x = myCurrentX;
        myAtlasRectangleWorkspace.y = myCurrentY;
        myAtlasRectangleWorkspace.width = aImageResource.getWidth();
        myAtlasRectangleWorkspace.height = aImageResource.getHeight();

        aImageResource.texture = new AtlasTexture( myAtlasTexture, myAtlasRectangleWorkspace );
        aImageResource.texturePurger = this;

        myTexturizedImageResources.add( aImageResource );

        myStrategy.moveCursor( aImageResource );

        //#if DEBUG
        Log.debug( "texture atlas {} has {} textures now", this, myTexturizedImageResources.size() );
        //#endif
        }

    public final void purge()
        {
        //#if DEBUG
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );
        //#endif
        while ( myTexturizedImageResources.size() > 0 )
            {
            final int lastIndex = myTexturizedImageResources.size() - 1;
            final AndroidImageResource lastImageResource = myTexturizedImageResources.remove( lastIndex );
            purge( lastImageResource );
            }

        if ( myAtlasTexture != null ) myAtlasTexture.purge();
        myAtlasTexture = null;
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Assert.isTrue( "known image", myTexturizedImageResources.contains( aImageResource ) );
        //#endif

        myTexturizedImageResources.remove( aImageResource );

        aImageResource.texture = null;
        aImageResource.texturePurger = null;

        if ( myTexturizedImageResources.size() == 0 )
            {
            //#if DEBUG
            Log.debug( "all textures purged from atlas {} - purging atlas texture", this );
            //#endif
            purge();
            myCurrentX = myCurrentY = myNextX = myNextY = 0;
            }
        }

    // Implementation

    private boolean enoughRoomAtCurrentPosition( final AndroidImageResource aImageResource )
        {
        if ( myCurrentX + aImageResource.getWidth() > myWidth ) return false;
        if ( myCurrentY + aImageResource.getHeight() > myHeight ) return false;
        return true;
        }

    private boolean enoughRoomInNextPosition( final AndroidImageResource aImageResource )
        {
        if ( myNextX + aImageResource.getWidth() > myWidth ) return false;
        if ( myNextY + aImageResource.getHeight() > myHeight ) return false;
        return true;
        }

    private void createAtlasTextureIfNecessary()
        {
        if ( myAtlasTexture != null ) return;
        myAtlasTexture = new TextureAtlasTexture();
        myAtlasTexture.make( myWidth, myHeight );
        }

    private void moveToNextLane( final AndroidImageResource aImageResource )
        {
        if ( !enoughRoomInNextPosition( aImageResource ) )
            {
            //#if DEBUG
            Log.debug( "failed adding {} into {}", aImageResource, this );
            Log.debug( "image size: {}x{}", aImageResource.getWidth(), aImageResource.getHeight() );
            Log.debug( "atlas size: {}x{}", myWidth, myHeight );
            Log.debug( "current pos: {}x{}", myCurrentX, myCurrentY );
            Log.debug( "next pos: {}x{}", myNextX, myNextY );
            //#endif
            throw new IllegalStateException( "texture atlas exhausted" );
            }

        myCurrentX = myNextX;
        myCurrentY = myNextY;
        }


    private int myNextX;

    private int myNextY;

    private int myCurrentX;

    private int myCurrentY;

    private LayoutStrategy myStrategy = HORIZONTAL;

    private TextureAtlasTexture myAtlasTexture;

    private final int myWidth;

    private final int myHeight;

    private final Rectangle myAtlasRectangleWorkspace = new Rectangle();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();
    }
