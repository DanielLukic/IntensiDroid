package net.intensicode.droid.opengl;

import android.graphics.*;
import net.intensicode.core.ImageResource;
import net.intensicode.droid.AndroidImageResource;
import net.intensicode.droid.TexturePurger;
import net.intensicode.util.*;

import java.util.ArrayList;

public final class FreeAreaTrackingTextureAtlas implements TextureAtlas, TexturePurger
    {
    public final String id;


    public FreeAreaTrackingTextureAtlas( final TextureUtilities aUtilities, final String aID )
        {
        this( aUtilities, aID, TextureUtilities.maximumTextureSize, TextureUtilities.maximumTextureSize );
        }

    public FreeAreaTrackingTextureAtlas( final TextureUtilities aUtilities, final String aID, final int aWidth, final int aHeight )
        {
        id = aID;
        myUtilities = aUtilities;
        myWidth = aWidth;
        myHeight = aHeight;
        }

    public final boolean is( final String aAtlasId )
        {
        return id.equalsIgnoreCase( aAtlasId );
        }

    // From TextureAtlas

    public final boolean enoughRoomFor( final AndroidImageResource aImageResource )
        {
        if ( enoughRoomAtCurrentPosition( aImageResource ) ) return true;
        if ( enoughRoomInNextLane( aImageResource ) ) return true;
        return freeAreaAvailableFor( aImageResource );
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

    private boolean freeAreaAvailableFor( final AndroidImageResource aImageResource )
        {
        final Rectangle rectangle = myFreeAreas.findFreeAreaBigEnoughFor( aImageResource.getWidth(), aImageResource.getHeight() );
        return rectangle != null;
        }

    public final void add( final AndroidImageResource aImageResource, final Position aInsertPosition )
        {
        createAtlasTextureIfNecessary();
        insertImageAndCreateTexture( aImageResource, aInsertPosition.x, aInsertPosition.y );
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

        // Let's try again. Moving to next lane creates new areas below potentially purged old areas!
        if ( mergedFreeAreaReplaced( aImageResource ) ) return;
        if ( biggerFreeAreaUsedPartially( aImageResource ) ) return;

        //#if DEBUG_OPENGL
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
        if ( freeArea == null ) return false;

        //#if DEBUG_OPENGL
        Log.debug( "using bigger free area {} for {}", freeArea, aImageResource );
        //#endif
        useFreeArea( freeArea, aImageResource );
        return true;
        }

    private void createAtlasTextureIfNecessary()
        {
        if ( myAtlasTexture != null ) return;
        myAtlasTexture = new TextureAtlasTexture( myUtilities );
        myAtlasTexture.make( myWidth, myHeight );
        }

    private boolean freeAreaReplaced( final AndroidImageResource aImageResource )
        {
        final Rectangle matchedFreeArea = findMatchingFreeArea( aImageResource );
        if ( matchedFreeArea == null ) return false;

        //#if DEBUG_OPENGL
        Log.debug( "using matched free area {} for {}", matchedFreeArea, aImageResource );
        //#endif
        useFreeArea( matchedFreeArea, aImageResource );
        return true;
        }

    private void useFreeArea( final Rectangle aRectangle, final AndroidImageResource aImageResource )
        {
        final int x = aRectangle.x;
        final int y = aRectangle.y;

        //#if DEBUG_OPENGL
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
        if ( !enoughRoomAtCurrentPosition( aImageResource ) ) return false;

        //#if DEBUG_OPENGL
        Log.debug( "using current position {} for {}", new Position( myCurrentX, myCurrentY ), aImageResource );
        //#endif
        useCurrentPosition( aImageResource );
        return true;
        }

    private void useCurrentPosition( final AndroidImageResource aImageResource )
        {
        final int x = myCurrentX;
        final int y = myCurrentY;

        //#if DEBUG_OPENGL
        Log.debug( "adding {} to texture atlas", aImageResource.resourcePath );
        Log.debug( "texture atlas insert position: current position {}", new Position( x, y ) );
        //#endif

        insertImageAndCreateTexture( aImageResource, x, y );
        moveCursor( aImageResource.getWidth(), aImageResource.getHeight() );
        }

    private boolean placedInNextLane( final AndroidImageResource aImageResource )
        {
        if ( !enoughRoomInNextLane( aImageResource ) ) return false;

        //#if DEBUG_OPENGL
        Log.debug( "using next position {} for {}", new Position( myNextX, myNextY ), aImageResource );
        //#endif
        moveToNextLane();
        useCurrentPosition( aImageResource );
        return true;
        }

    private boolean mergedFreeAreaReplaced( final AndroidImageResource aImageResource )
        {
        //#if DEBUG_OPENGL
        Log.debug( "merging free areas for {}", this );
        //#endif
        myFreeAreas.mergeFreeAreas();

        final Rectangle mergedFreeArea = findMatchingFreeArea( aImageResource );
        if ( mergedFreeArea == null ) return false;

        //#if DEBUG_OPENGL
        Log.debug( "using matching merged free area {} for {}", mergedFreeArea, aImageResource );
        //#endif
        useFreeArea( mergedFreeArea, aImageResource );
        return true;
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
        myCurrentX += aWidth;
        myNextY = Math.max( myNextY, myCurrentY + aHeight );
        }

    public final void purge()
        {
        Log.debug( "purging {} texturized image resources", myTexturizedImageResources.size() );

        while ( myTexturizedImageResources.size() > 0 )
            {
            final int lastIndex = myTexturizedImageResources.size() - 1;
            final AndroidImageResource lastImageResource = myTexturizedImageResources.remove( lastIndex );
            lastImageResource.dropTexture();
            }

        Log.debug( "all textures purged from atlas {} - purging atlas texture", this );

        if ( myAtlasTexture != null ) myAtlasTexture.purge();
        myAtlasTexture = null;

        myFreeAreas.clear();

        myCurrentX = myCurrentY = myNextX = myNextY = 0;
        }

    public final Bitmap dumpLayout()
        {
        //#if DEBUG_OPENGL
        Log.debug( "dumping texture atlas {}", this );
        //#endif

        final Bitmap bitmap = Bitmap.createBitmap( myWidth, myHeight, Bitmap.Config.ARGB_8888 );
        final Canvas canvas = new Canvas( bitmap );
        final Paint paint = new Paint();
        paint.setStyle( Paint.Style.FILL );
        paint.setColor( 0xFF000000 );
        canvas.drawRect( 0, 0, myWidth, myHeight, paint );

        paint.setColor( 0x4000FF00 );
        final Rectangle atlasRect = new Rectangle();
        final int numberOfImages = myTexturizedImageResources.size();
        for ( int idx = 0; idx < numberOfImages; idx++ )
            {
            final AndroidImageResource imageResource = myTexturizedImageResources.get( idx );
            final AtlasTexture texture = (AtlasTexture) imageResource.texture;
            texture.copyAtlasRectTo( atlasRect );
            final int left = atlasRect.x;
            final int top = atlasRect.y;
            final int right = left + atlasRect.width;
            final int bottom = top + atlasRect.height;
            canvas.drawBitmap( imageResource.bitmap, left, top, paint );
            canvas.drawRect( left, top, right, bottom, paint );
            }

        final ArrayList<Rectangle> freeList = myFreeAreas.accessFreeAreasList();
        final int numberOfFreeAreas = freeList.size();
        for ( int idx = 0; idx < numberOfFreeAreas; idx++ )
            {
            final Rectangle freeArea = freeList.get( idx );
            final int left = freeArea.x;
            final int top = freeArea.y;
            final int right = left + freeArea.width;
            final int bottom = top + freeArea.height;

            paint.setColor( 0x40FF0000 );
            paint.setStyle( Paint.Style.FILL );
            canvas.drawRect( left, top, right, bottom, paint );

            paint.setColor( 0x40FFFFFF );
            paint.setStyle( Paint.Style.STROKE );
            canvas.drawRect( left, top, right, bottom, paint );
            canvas.drawLine( left, top, right, bottom, paint );
            canvas.drawLine( left, bottom, right, top, paint );
            }

        return bitmap;
        }

    // From TexturePurger

    public final void purge( final AndroidImageResource aImageResource )
        {
        //#if DEBUG_OPENGL
        Assert.isTrue( "known image", myTexturizedImageResources.contains( aImageResource ) );
        //#endif

        //#if DEBUG_OPENGL
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
        }

    // From Object

    //#if DEBUG

    public final String toString()
        {
        final StringBuilder builder = new StringBuilder( "FreeAreaTrackingTextureAtlas" );
        builder.append( '[' );
        builder.append( id );
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
        addFreeAreasBelowUsedAreasOfThisLane();
        addFreeAreaForRemainingSpaceInLane();
        myCurrentX = myNextX;
        myCurrentY = myNextY;
        }

    private void addFreeAreasBelowUsedAreasOfThisLane()
        {
        final Rectangle checkRectangle = new Rectangle();
        for ( int idx = 0; idx < myTexturizedImageResources.size(); idx++ )
            {
            final AndroidImageResource imageResource = myTexturizedImageResources.get( idx );
            final AtlasTexture texture = (AtlasTexture) imageResource.texture;
            texture.copyAtlasRectTo( checkRectangle );
            if ( checkRectangle.y != myCurrentY ) continue;
            if ( checkRectangle.height >= myNextY ) continue;
            addFreeAreaBelow( checkRectangle );
            }
        }

    private void addFreeAreaBelow( final Rectangle aRectangle )
        {
        final Rectangle freeBelow = new Rectangle();
        freeBelow.x = aRectangle.x;
        freeBelow.y = aRectangle.y + aRectangle.height;
        freeBelow.width = aRectangle.width;
        freeBelow.height = myNextY - myCurrentY - aRectangle.height;
        myFreeAreas.add( freeBelow );
        }

    private void addFreeAreaForRemainingSpaceInLane()
        {
        if ( myCurrentX >= myWidth ) return;
        final int width = myWidth - myCurrentX;
        final int height = myNextY - myCurrentY;
        final Rectangle remaining = new Rectangle( myCurrentX, myCurrentY, width, height );
        myFreeAreas.add( remaining );
        }


    private int myNextX;

    private int myNextY;

    private int myCurrentX;

    private int myCurrentY;

    private TextureAtlasTexture myAtlasTexture;

    private final int myWidth;

    private final int myHeight;

    private final TextureUtilities myUtilities;

    private final FreeAreas myFreeAreas = new FreeAreas();

    private final Rectangle myAtlasRectangleWorkspace = new Rectangle();

    private final ArrayList<AndroidImageResource> myTexturizedImageResources = new ArrayList<AndroidImageResource>();
    }
