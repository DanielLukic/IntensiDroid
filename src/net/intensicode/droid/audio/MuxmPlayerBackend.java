package net.intensicode.droid.audio;

import android.content.res.AssetManager;
import net.intensicode.ReleaseProperties;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.*;

import java.io.*;

public final class MuxmPlayerBackend implements AudioBackend
    {
    public MuxmPlayerBackend( final AssetManager aAssetManager )
        {
        myAssetManager = aAssetManager;
        }

    // From AudioBackend

    public final AudioResourceEx loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.MUSIC_FOLDER, aMusicName, ReleaseProperties.MUSIC_FORMAT_SUFFIX );
        final MuxmPlayer player = createPlayerForModule( resourceFilePath );
        return new MuxmPlayerAudioResource( player );
        }

    public final AudioResourceEx loadSound( final String aSoundName )
        {
        throw new UnsupportedOperationException();
        }

    // Implementation

    private String makeResourceFilePath( final String aResourceFolder, final String aMusicName, final String aFileNameSuffix )
        {
        final StringBuilder builder = new StringBuilder();
        builder.append( aResourceFolder );
        builder.append( "/" );
        builder.append( aMusicName );
        builder.append( aFileNameSuffix );
        return builder.toString();
        }

    private MuxmPlayer createPlayerForModule( final String aMusicResourceFilePath ) throws IOException
        {
        //#if DEBUG
        Log.debug( "loading module {}", aMusicResourceFilePath );
        //#endif
        final InputStream stream = myAssetManager.open( aMusicResourceFilePath );
        final Module module = Loader.load( stream );
        final ModuleEngine engine = new ModuleEngine( module );
        return new MuxmPlayer( engine );
        }


    private final AssetManager myAssetManager;
    }
