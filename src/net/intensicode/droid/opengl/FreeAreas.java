package net.intensicode.droid.opengl;

import net.intensicode.util.*;

import java.util.ArrayList;

public final class FreeAreas
    {
    public final void clear()
        {
        myFreeList.clear();
        }

    public final void add( final Rectangle aRectangle )
        {
        myFreeList.add( aRectangle );
        }

    public final void subtract( final Rectangle aRectangle, final int aWidth, final int aHeight )
        {
        if ( aWidth > aRectangle.width ) throw new IllegalArgumentException();
        if ( aHeight > aRectangle.height ) throw new IllegalArgumentException();

        remove( aRectangle );

        if ( aWidth == aRectangle.width && aHeight == aRectangle.height ) return;

        createNewFreeAreasForRemainingSpace( aRectangle, aWidth, aHeight );
        }

    private void createNewFreeAreasForRemainingSpace( final Rectangle aRectangle, final int aWidth, final int aHeight )
        {
        if ( aWidth == aRectangle.width )
            {
            createFreeAreaWithFullWidthBelow( aRectangle, aWidth, aHeight );
            }
        else if ( aHeight == aRectangle.height )
            {
            createFreeAreaToTheLeft( aRectangle, aWidth, aHeight );
            }
        else
            {
            createFreeAreaToTheLeft( aRectangle, aWidth, aHeight );
            createFreeAreaWithFullWidthBelow( aRectangle, aWidth, aHeight );
            }
        }

    private void createFreeAreaWithFullWidthBelow( final Rectangle aRectangle, final int aWidth, final int aHeight )
        {
        final Rectangle below = new Rectangle();
        below.x = aRectangle.x;
        below.y = aRectangle.y + aHeight;
        below.width = aRectangle.width;
        below.height = aRectangle.height - aHeight;
        add( below );
        }

    private void createFreeAreaToTheLeft( final Rectangle aRectangle, final int aWidth, final int aHeight )
        {
        final Rectangle toTheLeft = new Rectangle();
        toTheLeft.x = aRectangle.x + aWidth;
        toTheLeft.y = aRectangle.y;
        toTheLeft.width = aRectangle.width - aWidth;
        toTheLeft.height = aHeight;
        add( toTheLeft );
        }

    public final void remove( final Rectangle aRectangle )
        {
        final boolean removed = myFreeList.remove( aRectangle );
        //#if DEBUG
        Assert.isTrue( "known free area", removed );
        //#endif
        }

    public final void mergeFreeAreas()
        {
        while ( tryMergingFreeAreas() ) ;
        }

    private boolean tryMergingFreeAreas()
        {
        final int numberOfFreeAreas = myFreeList.size();
        for ( int idx = 0; idx < numberOfFreeAreas; idx++ )
            {
            final Rectangle rectangle = myFreeList.get( idx );
            final Rectangle adjacent = findAdjacentRectangle( rectangle );
            if ( adjacent == null ) continue;
            mergeFreeAreas( rectangle, adjacent );
            return true;
            }
        return false;
        }

    private Rectangle findAdjacentRectangle( final Rectangle aRectangle )
        {
        final int numberOfFreeAreas = myFreeList.size();
        for ( int idx = 0; idx < numberOfFreeAreas; idx++ )
            {
            final Rectangle other = myFreeList.get( idx );
            if ( other.isAdjacent( aRectangle ) ) return other;
            }
        return null;
        }

    private void mergeFreeAreas( final Rectangle aFirst, final Rectangle aSecond )
        {
        aFirst.uniteWith( aSecond );

        remove( aSecond );
        }

    public final Rectangle findFreeAreaMatching( final int aWidth, final int aHeight )
        {
        final int numberOfFreeAreas = myFreeList.size();
        for ( int idx = 0; idx < numberOfFreeAreas; idx++ )
            {
            final Rectangle rectangle = myFreeList.get( idx );
            if ( rectangle.width == aWidth && rectangle.height == aHeight ) return rectangle;
            }
        return null;
        }

    public final Rectangle findFreeAreaBigEnoughFor( final int aWidth, final int aHeight )
        {
        Rectangle bestMatch = null;

        final int numberOfFreeAreas = myFreeList.size();
        for ( int idx = 0; idx < numberOfFreeAreas; idx++ )
            {
            final Rectangle rectangle = myFreeList.get( idx );
            if ( rectangle.width >= aWidth && rectangle.height >= aHeight )
                {
                if ( bestMatch == null )
                    {
                    bestMatch = rectangle;
                    }
                else if ( rectangle.width <= bestMatch.width && rectangle.height <= bestMatch.height )
                    {
                    bestMatch = rectangle;
                    }
                }
            }

        return bestMatch;
        }


    private final ArrayList<Rectangle> myFreeList = new ArrayList<Rectangle>();
    }
