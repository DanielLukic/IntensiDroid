package net.intensicode.droid.audio;

import android.content.res.*;
import android.media.MediaPlayer;
import net.intensicode.core.*;
import net.intensicode.util.Log;

import java.io.IOException;

public final class MediaPlayerBackend implements SoundBackend, MediaPlayer.OnErrorListener
    {
    public static final int NUMBER_OF_CHANNELS = 1;

    public String musicFolder = "music";

    public String musicSuffix = ".mp3";

    public String soundFolder = "sound";

    public String soundSuffix = ".mp3";


    public MediaPlayerBackend( final AssetManager aAssetManager )
        {
        myAssetManager = aAssetManager;
        }

    // From SoundBackend

    public final int numberOfChannels()
        {
        return NUMBER_OF_CHANNELS;
        }

    public final MusicResource loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( musicFolder, aMusicName, musicSuffix );
        final MediaPlayer player = createAndPrepareMediaPlayer( resourceFilePath );
        return new MediaPlayerAudioResource( player );
        }

    public final SoundResource loadSound( final String aSoundName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( soundFolder, aSoundName, soundSuffix );
        final MediaPlayer player = createAndPrepareMediaPlayer( resourceFilePath );
        return new MediaPlayerAudioResource( player );
        }

    // From OnErrorListener

    public boolean onError( final MediaPlayer aMediaPlayer, final int i, final int i1 )
        {
        //#if DEBUG
        Log.debug( "media player error {} {}", i, i1 );
        //#endif
        return false;
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

    private MediaPlayer createAndPrepareMediaPlayer( final String aMusicResourceFilePath ) throws IOException
        {
        //#if DEBUG
        Log.debug( "creating media player for {}", aMusicResourceFilePath );
        //#endif
        final AssetFileDescriptor fd = myAssetManager.openFd( aMusicResourceFilePath );
        final MediaPlayer player = new MediaPlayer();
        player.setOnErrorListener( this );
        player.setDataSource( fd.getFileDescriptor() );
        player.prepare();
        return player;
        }


    private final AssetManager myAssetManager;
    }
