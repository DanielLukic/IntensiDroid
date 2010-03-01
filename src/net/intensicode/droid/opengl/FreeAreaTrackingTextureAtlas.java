package net.intensicode.droid.opengl;

import net.intensicode.core.ImageResource;
import net.intensicode.droid.*;
import net.intensicode.util.*;

import java.util.ArrayList;

public final class FreeAreaTrackingTextureAtlas implements TextureAtlas, TexturePurger
    {
    public FreeAreaTrackingTextureAtlas( final int aID )
        {
        this( aID, TextureUtilities.maximumTextureSize, TextureUtilities.maximumTextureSize );
        }

    public FreeAreaTrackingTextureAtlas( final int aID, final int aWidth, final int aHeight )
        {
        myID = aID;
        myWidth = aWidth;
        myHeight = aHeight;
        }

    // From TextureAtlas

    public final boolean enoughRoomFor( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomAtCurrentPosition( aImageResource ) ) return true;
        if ( enoughRoomInNextLane( aImageResource ) ) return true;
        return false;
        }

    private boolean enoughRoomInNextLane( final ImageResource aImageResource )
        {
        if ( myNextX + aImageResource.getWidth() > myWidth ) return false;
        if ( myNextY + aImageResource.getHeight() > myHeight ) return false;
        return true;
        }

    private boolean enoughRoomAtCurrentPosition( final ImageResource aImageResource )
        {
        if ( myCurrentX + aImageResource.getWidth() > myWidth ) return false;
        if ( myCurrentY + aImageResource.getHeight() > myHeight ) return false;
        return true;
        }

    public final void add( final AndroidImageResource aImageResource )
        {
        createAtlasTextureIfNecessary();

        // TODO: Object chain or helper class?
        if ( freeAreaReplaced( aImageResource ) ) return;

        if ( mergedFreeAreaReplaced( aImageResource ) ) return;
        if ( biggerFreeAreaUsedPartially( aImageResource ) ) return;

        if ( placeAtCurrentPosition( aImageResource ) ) return;
        if ( placedInNextLane( aImageResource ) ) return;
        if ( mergedFreeAreaReplaced( aImageResource ) ) return;
        if ( biggerFreeAreaUsedPartially( aImageResource ) ) return;

        //#if DEBUG
        Log.debug( "failed adding {} into {}", aImageResource, this );
        Log.debug( "image size: {}x{}", aImageResource.getWidth(), aImageResource.getHeight() );
        Log.debug( "atlas size: {}x{}", myWidth, myHeight );
        Log.debug( "current pos: {}x{}", myCurrentX, myCurrentY );
        Log.debug( "next pos: {}x{}", myNextX, myNextY );
        //#endif

        throw new IllegalStateException( "texture atlas exhausted" );
        }

    private boolean biggerFreeAreaUsedPartially( final AndroidImageResource aImageResource )
        {
        final Rectangle freeArea = findFreeArea( aImageResource );
        if ( freeArea != null )
            {
            Log.debug( "using bigger free area {} for {}", freeArea, aImageResource );
            useFreeArea( freeArea, aImageResource );
            return true;
            }
        return false;
        }

    private void createAtlasTextureIfNecessary()
        {
        if ( myAtlasTexture != null ) return;
        myAtlasTexture = new TextureAtlasTexture();
        myAtlasTexture.make( myWidth, myHeight );
        }

    private boolean freeAreaReplaced( final AndroidImageResource aImageResource )
        {
        final Rectangle matchedFreeArea = findMatchingFreeArea( aImageResource );
        if ( matchedFreeArea == null ) return false;

        Log.debug( "using matched free area {} for {}", matchedFreeArea, aImageResource );
        useFreeArea( matchedFreeArea, aImageResource );
        return true;
        }

    private void useFreeArea( final Rectangle aRectangle, final AndroidImageResource aImageResource )
        {
        final int x = aRectangle.x;
        final int y = aRectangle.y;

        //#if DEBUG
        Log.debug( "adding {} to texture atlas", aImageResource.resourcePath );
        Log.debug( "texture atlas insert position: free area {}", aRectangle );
        //#endif

        insertImageAndCreateTexture( aImageResource, x, y );

        final int width = aImageResource.getWidth();
        final int height = aImageResource.getHeight();
        myFreeAreas.subtract( aRectangle, width, height );
        }

    private boolean placeAtCurrentPosition( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomAtCurrentPosition( aImageResource ) )
            {
            Log.debug( "using current position {} for {}", new Position( myCurrentX, myCurrentY ), aImageResource );
            useCurrentPosition( aImageResource );
            return true;
            }
        return false;
        }

    private void useCurrentPosition( final AndroidImageResource aImageResource )
        {
        final int x = myCurrentX;
        final int y = myCurrentY;

        //#if DEBUG
        Log.debug( "adding {} to texture atlas", aImageResource.resourcePath );
        Log.debug( "texture atlas insert position: current position {}", new Position( x, y ) );
        //#endif

        insertImageAndCreateTexture( aImageResource, x, y );
        moveCursor( aImageResource.getWidth(), aImageResource.getHeight() );
        }

    private boolean placedInNextLane( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomInNextLane( aImageResource ) )
            {
            Log.debug( "using next position {} for {}", new Position( myNextX, myNextY ), aImageResource );
            moveToNextLane();
            useCurrentPosition( aImageResource );
            return true;
            }
        return false;
        }

    private boolean mergedFreeAreaReplaced( final AndroidImageResource aImageResource )
        {
        Log.debug( "merging free areas for {}", this );
        myFreeAreas.mergeFreeAreas();

        final Rectangle mergedFreeArea = findMatchingFreeArea( aImageResource );
        if ( mergedFreeArea != null )
            {
            Log.debug( "using matching merged free area {} for {}", mergedFreeArea, aImageResource );
            useFreeArea( mergedFreeArea, aImageResource );
            return true;
            }
        return false;
        }

    private Rectangle findMatchingFreeArea( final ImageResource aImageResource )
        {
        final int width = aImageResource.getWidth();
        final int height = aImageResource.getHeight();
        return myFreeAreas.findFreeAreaMatching( width, height );
        }

    private Rectangle findFreeArea( final ImageResource aImageResource )
        {
        final int width = aImageResource.getWidth();
        final int height = aImageResource.getHeight();
        return myFreeAreas.findFreeAreaBigEnoughFor( width, height );
        }

    private void insertImageAndCreateTexture( final AndroidImageResource aImageResource, final int aX, final int aY )
        {
        myAtlasTexture.add( aImageResource.bitmap, aX, aY );

        myAtlasRectangleWorkspace.x = aX;
        myAtlasRectangleWorkspace.y = aY;
        myAtlasRectangleWorkspace.width = aImageResource.getWidth();
        myAtlasRectangleWorkspace.height = aImageResource.getHeight();

        aImageResource.texture = new AtlasTexture( myAtlasTexture, myAtlasRectangleWorkspace );
        aImageResource.texturePurger = this;

        myTexturizedImageResources.add( aImageResource );
        }

    private void moveCursor( final int aWidth, final int aHeight )
        {
        // create free area*s* for skipped vertical area:
        // new next y = max( next y, current y + aIR.getHeight )
        // delta height = new next y - next y
        // done if delta height == 0
        // for every rect in the current lane:
        //   create free area below, same width, delta height

        myCurrentX += aWidth;
        myNextY = Math.max( myNextY, myCurrentY + aHeight );
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
            lastImageResource.dropTexture();
            }

        //#if DEBUG
        Log.debug( "all textures purged from atlas {} - purging atlas texture", this );
        //#endif

        if ( myAtlasTexture != null ) myAtlasTexture.purge();
        myAtlasTexture = null;

        myFreeAreas.clear();

        myCurrentX = myCurrentY = myNextX = myNextY = 0;
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG
        Assert.isTrue( "known image", myTexturizedImageResources.contains( aImageResource ) );
        //#endif

        //#if DEBUG
        Log.debug( "purging {} from {}", aImageResource, this );
        //#endif

        addToFreeList( (AtlasTexture) aImageResource.texture );

        aImageResource.dropTexture();

        myTexturizedImageResources.remove( aImageResource );
        if ( myTexturizedImageResources.size() == 0 ) purge();
        }

    private void addToFreeList( final AtlasTexture aTexture )
        {
        final Rectangle freeRectangle = new Rectangle();
        aTexture.copyAtlasRectTo( freeRectangle );
        myFreeAreas.add( freeRectangle );

        // merging free areas should be done only if nothing is found in 'add'.. here it would be overkill..
        }

    // From Object

    //#if DEBUG

    public final String toString()
        {
        final StringBuilder builder = new StringBuilder( "FreeAreaTrackingTextureAtlas" );
        builder.append( '[' );
        builder.append( myID );
        builder.append( ':' );
        builder.append( myWidth );
        builder.append( 'x' );
        builder.append( myHeight );
        builder.append( ']' );
        return builder.toString();
        }

    //#endif

    // Implementation

    private void moveToNextLane()
        {
        addFreeAreaForRemainingSpaceInLane();
        myCurrentX = myNextX;
        myCurrentY = myNextY;
        }

    private void addFreeAreaForRemainingSpaceInLane()
        {
        if ( myCurrentX < myWidth )
            {
            final int width = myWidth - myCurrentX;
            final int height = myNextY - myCurrentY;
            final Rectangle remaining = new Rectangle( myCurrentX, myCurrentY, width, height );
            myFreeAreas.add( remaining );
            }
        }


    private int myNextX;

    private int myNextY;

    private int myCurrentX;

    private int myCurrentY;

    private TextureAtlasTexture myAtlasTexture;

    private final int myID;

    private final int myWidth;

    private final int myHeight;

    private final FreeAreas myFreeAreas = new FreeAreas();

    private final Rectangle myAtlasRectangleWorkspace = new Rectangle();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();
    }
