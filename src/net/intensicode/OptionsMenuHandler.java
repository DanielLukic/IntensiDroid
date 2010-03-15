package net.intensicode;

import android.content.Context;
import android.view.*;
import net.intensicode.core.GameSystem;
import net.intensicode.dialogs.ConfigurationDialogBuilder;
import net.intensicode.util.Assert;

import java.util.Hashtable;

public final class OptionsMenuHandler
    {
    public OptionsMenuHandler( final Context aContext, final GameSystem aGameSystem )
        {
        myContext = aContext;
        myGameSystem = aGameSystem;
        }

    public final void onCreateOptionsMenu( final Menu aMenu )
        {
        Assert.equals( "number of mappings", 0, myMappedConfigurationValues.size() );

        final SystemContext context = myGameSystem.context;

        final ConfigurationElementsTree platformValues = context.getPlatformValues();
        addMenuEntries( 100, aMenu, platformValues );

        final ConfigurationElementsTree systemValues = context.getSystemValues();
        addMenuEntries( 200, aMenu, systemValues );

        final ConfigurationElementsTree applicationValues = context.getApplicationValues();
        addMenuEntries( 300, aMenu, applicationValues );

        final SubMenu debugMenu = aMenu.addSubMenu( "DEBUG" );
        debugMenu.add( "Dump Texture Atlases" );
        debugMenu.add( "Select Dump Target" );

        //#if CONSOLE
        final SubMenu consoleMenu = aMenu.addSubMenu( "CONSOLE" );
        consoleMenu.add( "Show console" ).setCheckable( true );
        consoleMenu.add( "Set entry stay time" );
        //#endif
        }

    private void addMenuEntries( final int aBaseGroupId, final Menu aMenu, final ConfigurationElementsTree aConfigurationElementsTree )
        {
        final int numberOfEntries = aConfigurationElementsTree.numberOfEntries();
        if ( numberOfEntries == 0 ) return;

        for ( int idx = 0; idx < numberOfEntries; idx++ )
            {
            final ConfigurationElementsTree entry = aConfigurationElementsTree.getEntry( idx );
            if ( entry.isLeaf() )
                {
                final ConfigurableValue value = entry.value;
                final String title = value.getTitle();
                aMenu.add( aBaseGroupId, idx, 0, title );

                final String key = Integer.toString( aBaseGroupId ) + "." + Integer.toString( idx );
                myMappedConfigurationValues.put( key, value );
                }
            else
                {
                final String label = entry.label;
                final SubMenu subMenu = aMenu.addSubMenu( label );
                addMenuEntries( aBaseGroupId + 1 + idx, subMenu, entry );
                }
            }
        }

    public final boolean onOptionsItemSelected( final MenuItem aMenuItem )
        {
        final String key = Integer.toString( aMenuItem.getGroupId() ) + "." + Integer.toString( aMenuItem.getItemId() );

        final ConfigurableValue value = (ConfigurableValue) myMappedConfigurationValues.get( key );
        if ( value == null ) return false;

        new ConfigurationDialogBuilder( myContext ).using( value ).createDialog();
        return true;
        }


    private final Context myContext;

    private final GameSystem myGameSystem;

    private final Hashtable myMappedConfigurationValues = new Hashtable();
    }