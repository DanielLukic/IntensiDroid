package net.intensicode;

import android.content.Context;
import android.view.*;
import net.intensicode.core.GameSystem;
import net.intensicode.dialogs.*;
import net.intensicode.util.Log;

public final class OptionsMenuHandler
    {
    public OptionsMenuHandler( final Context aContext, final GameSystem aGameSystem )
        {
        myContext = aContext;
        myGameSystem = aGameSystem;
        }

    public final void onCreateOptionsMenu( final Menu aMenu )
        {
        final SubMenu trackballMenu = aMenu.addSubMenu( "TRACKBALL" );
        trackballMenu.add( "forcedSilenceBetweenEventsInMillis" );
        trackballMenu.add( "silenceBeforeUpdateInMillis" );
        trackballMenu.add( "multiEventThresholdInMillis" );
        trackballMenu.add( "directionIgnoreFactorFixed" );
        trackballMenu.add( "initialTicksThreshold" );
        trackballMenu.add( "multiTicksThreshold" );
        trackballMenu.add( "additionalMultiTicksThreshold" );

        final SubMenu debugMenu = aMenu.addSubMenu( "DEBUG" );
        debugMenu.add( "Dump Texture Atlases" );
        debugMenu.add( "Select Dump Target" );

        //#if CONSOLE
        final SubMenu consoleMenu = aMenu.addSubMenu( "CONSOLE" );
        consoleMenu.add( "Show console" ).setCheckable( true );
        consoleMenu.add( "Set entry stay time" );
        //#endif
        }

    public final void onPrepareOptionsMenu( final Menu aMenu )
        {
        }

    public final boolean onOptionsItemSelected( final MenuItem aMenuItem )
        {
        if ( aMenuItem.getTitle().equals( "forcedSilenceBetweenEventsInMillis" ) )
            {
            new forcedSilenceBetweenEventsInMillis( myContext, myGameSystem ).createDialog();
            }
        else if ( aMenuItem.getTitle().equals( "silenceBeforeUpdateInMillis" ) )
            {
            new silenceBeforeUpdateInMillis( myContext, myGameSystem ).createDialog();
            }
        else if ( aMenuItem.getTitle().equals( "multiEventThresholdInMillis" ) )
            {
            new multiEventThresholdInMillis( myContext, myGameSystem ).createDialog();
            }
        else if ( aMenuItem.getTitle().equals( "directionIgnoreFactorFixed" ) )
                {
                new directionIgnoreFactorFixed( myContext, myGameSystem ).createDialog();
                }
            else if ( aMenuItem.getTitle().equals( "initialTicksThreshold" ) )
                    {
                    new initialTicksThreshold( myContext, myGameSystem ).createDialog();
                    }
                else if ( aMenuItem.getTitle().equals( "multiTicksThreshold" ) )
                        {
                        new multiTicksThreshold( myContext, myGameSystem ).createDialog();
                        }
                    else if ( aMenuItem.getTitle().equals( "additionalMultiTicksThreshold" ) )
                            {
                            new additionalMultiTicksThreshold( myContext, myGameSystem ).createDialog();
                            }
                        else
                            {
                            Log.debug( "item {}", aMenuItem );
                            return false;
                            }
        return true;
        }


    private final Context myContext;

    private final GameSystem myGameSystem;
    }
