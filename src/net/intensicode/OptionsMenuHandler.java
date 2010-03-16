package net.intensicode;

import android.content.Context;
import android.view.*;
import net.intensicode.core.GameSystem;
import net.intensicode.configuration.ConfigurationDialogBuilder;
import net.intensicode.util.*;

import java.io.IOException;
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

        aMenu.add( "Save" );
        aMenu.add( "Load" );
        }

    private void addMenuEntries( final int aBaseGroupId, final Menu aMenu, final ConfigurationElementsTree aConfigurationElementsTree )
        {
        final int numberOfEntries = aConfigurationElementsTree.numberOfEntries();
        if ( numberOfEntries == 0 ) return;

        myAvailableTrees.add( aConfigurationElementsTree );

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
        if ( "Save".equals( aMenuItem.getTitle() ) )
            {
            saveConfiguration();
            }
        else if ( "Load".equals( aMenuItem.getTitle() ) )
            {
            loadConfiguration();
            }
        else
            {
            final String key = Integer.toString( aMenuItem.getGroupId() ) + "." + Integer.toString( aMenuItem.getItemId() );

            final ConfigurableValue value = (ConfigurableValue) myMappedConfigurationValues.get( key );
            if ( value == null ) return false;

            new ConfigurationDialogBuilder( myContext ).using( value ).createDialog();
            }
        return true;
        }

    private void saveConfiguration()
        {
        for ( int idx = 0; idx < myAvailableTrees.size; idx++ )
            {
            final ConfigurationElementsTree tree = (ConfigurationElementsTree) myAvailableTrees.get( idx );
            try
                {
                myGameSystem.storage.save( new ConfigurationElementsTreeIO( tree ) );
                }
            catch ( IOException e )
                {
                Log.error( "failed saving configuration elements tree {}", tree.label, e );
                }
            }
        }

    private void loadConfiguration()
        {
        for ( int idx = 0; idx < myAvailableTrees.size; idx++ )
            {
            final ConfigurationElementsTree tree = (ConfigurationElementsTree) myAvailableTrees.get( idx );
            try
                {
                myGameSystem.storage.load( new ConfigurationElementsTreeIO( tree ) );
                }
            catch ( IOException e )
                {
                Log.error( "failed loading configuration elements tree {}", tree.label, e );
                }
            }
        }


    private final Context myContext;

    private final GameSystem myGameSystem;

    private final DynamicArray myAvailableTrees = new DynamicArray();

    private final Hashtable myMappedConfigurationValues = new Hashtable();
    }
