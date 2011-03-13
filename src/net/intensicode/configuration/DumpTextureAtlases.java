package net.intensicode.configuration;

import android.graphics.Bitmap;
import android.os.Environment;
import net.intensicode.ConfigurableActionValue;
import net.intensicode.droid.opengl.AtlasTextureManager;
import net.intensicode.droid.opengl.FreeAreaTrackingTextureAtlas;
import net.intensicode.util.Log;

import java.io.*;
import java.util.ArrayList;

public final class DumpTextureAtlases implements ConfigurableActionValue
    {
    public DumpTextureAtlases( final AtlasTextureManager aTextureManager )
        {
        myTextureManager = aTextureManager;
        }

    public final String getTitle()
        {
        return "Dump Texture Atlases";
        }

    public final String getInfoText()
        {
        return "Dump texture atlases to SD card";
        }

    public final void trigger()
        {
        try
            {
            final File dataDirectory = Environment.getExternalStorageDirectory();
            final File intensigameFolder = new File( dataDirectory, "intensigame" );
            intensigameFolder.mkdirs();

            final ArrayList<FreeAreaTrackingTextureAtlas> atlases = myTextureManager.getTextureAtlases();
            for ( final FreeAreaTrackingTextureAtlas atlas : atlases )
                {
                final File dumpFile = new File( intensigameFolder, "atlas" + atlas.id + ".png" );
                dumpFile.createNewFile();
                final FileOutputStream stream = new FileOutputStream( dumpFile );
                final Bitmap bitmap = atlas.dumpLayout();
                final boolean done = bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream );
                bitmap.recycle();
                if ( !done ) throw new IOException();
                stream.close();
                }
            }
        catch ( final Exception e )
            {
            Log.error( "failed dumping texture atlases", e );
            }
        }

    private final AtlasTextureManager myTextureManager;
    }
