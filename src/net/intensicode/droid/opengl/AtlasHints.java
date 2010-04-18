package net.intensicode.droid.opengl;

import net.intensicode.util.Position;

public final class AtlasHints
    {
    public static AtlasHints parse( final String aHints )
        {
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
            }
        else
            {
            hints.atlasId = aHints;
            }
        return hints;
        }

    public String atlasId;

    public Position position;
    }
