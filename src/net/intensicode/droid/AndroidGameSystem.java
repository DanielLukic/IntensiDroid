package net.intensicode.droid;

import net.intensicode.core.*;

public final class AndroidGameSystem extends GameSystem
    {
    public AndroidGameSystem( final SystemContext aSystemContext )
        {
        super( aSystemContext );
        }

    // From GameSystem

    protected void throwWrappedExceptionToTellCallingSystemAboutBrokenGameSystem( final Exception aException )
        {
        throw new RuntimeException( "failed showing error screen", aException );
        }
    }
