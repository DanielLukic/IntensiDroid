package net.intensicode.droid.opengl;

import net.intensicode.util.*;

public final class AtlasHints
    {
    public static AtlasHints parse( final String aHints )
        {
        Log.info( "AtlasHints#parse {}", aHints );
        final int positionDelimiter = aHints.indexOf( "," );
        final boolean idAndPosition = positionDelimiter != -1;
        final AtlasHints hints = new AtlasHints();
        if ( idAndPosition )
            {
            hints.atlasId = aHints.substring( 0, positionDelimiter );
            final int ordinateDelimiter = aHints.indexOf( "x", positionDelimiter );
            final String x = aHints.substring( positionDelimiter + 1, ordinateDelimiter );
            final String y = aHints.substring( ordinateDelimiter + 1, aHints.length() );
            hints.position = new Position( Integer.parseInt( x ), Integer.parseInt( y ) );
            Log.info( "AtlasHints: {} {}", hints.atlasId, hints.position.x + "x" + hints.position.y );
            }
        else
            {
            hints.atlasId = aHints;
            Log.info( "AtlasHints: {} NO POSITION", hints.atlasId );
            }
        return hints;
        }

    public String atlasId;

    public Position position;
    }
