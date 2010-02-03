package net.intensicode.droid;

import android.content.Context;
import net.intensicode.core.*;
import net.intensicode.io.StorageIO;
import net.intensicode.util.Log;

import java.io.*;

public class AndroidStorageManager implements StorageManager
    {
    public AndroidStorageManager( final Context aContext )
        {
        myContext = aContext;
        }

    // From StorageManager

    public final boolean hasData( final StorageIO aStorageIO )
        {
        final String name = aStorageIO.getName();
        return doesFileExist( name );
        }

    public void erase( final StorageIO aStorageIO )
        {
        final String name = aStorageIO.getName();

        //#if DEBUG
        Log.debug( "Erasing {}", name );
        //#endif

        myContext.deleteFile( name );
        }

    public void load( final StorageIO aStorageIO ) throws IOException
        {
        final String name = aStorageIO.getName();

        //#if DEBUG
        Log.debug( "Loading {}", name );
        //#endif

        final FileInputStream stream = myContext.openFileInput( name );
        final byte[] data = ResourcesManager.loadStream( stream );
        stream.close();

        if ( data == null || data.length == 0 ) return;

        final ByteArrayInputStream bytes = new ByteArrayInputStream( data );
        aStorageIO.loadFrom( new DataInputStream( bytes ) );
        }

    public void save( final StorageIO aStorageIO ) throws IOException
        {
        final String name = aStorageIO.getName();

        //#if DEBUG
        Log.debug( "Saving {}", name );
        //#endif

        final FileOutputStream stream = myContext.openFileOutput( name, 0 );
        aStorageIO.saveTo( new DataOutputStream( stream ) );
        stream.close();
        }

    // Implementation

    private boolean doesFileExist( final String aName )
        {
        final String[] listOfFileNames = myContext.fileList();
        for ( int idx = 0; idx < listOfFileNames.length; idx++ )
            {
            if ( listOfFileNames[ idx ].equals( aName ) ) return true;
            }
        return false;
        }


    private final Context myContext;
    }
