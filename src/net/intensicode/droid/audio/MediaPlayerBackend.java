package net.intensicode.droid.audio;

import android.content.res.*;
import android.media.MediaPlayer;
import net.intensicode.ReleaseProperties;
import net.intensicode.core.*;
import net.intensicode.util.Log;

import java.io.IOException;

public final class MediaPlayerBackend implements AudioBackend
    {
    public static final int NUMBER_OF_CHANNELS = 1;


    public MediaPlayerBackend( final AssetManager aAssetManager )
        {
        myAssetManager = aAssetManager;
        }

    // From AudioBackend

    public final int numberOfChannels()
        {
        return NUMBER_OF_CHANNELS;
        }

    public final MusicResource loadMusic( final String aMusicName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.MUSIC_FOLDER, aMusicName, ReleaseProperties.MUSIC_FORMAT_SUFFIX );
        final MediaPlayer player = createAndPrepareMediaPlayer( resourceFilePath );
        return new MediaPlayerAudioResource( player );
        }

    public final SoundResource loadSound( final String aSoundName ) throws IOException
        {
        final String resourceFilePath = makeResourceFilePath( ReleaseProperties.SOUND_FOLDER, aSoundName, ReleaseProperties.SOUND_FORMAT_SUFFIX );
        final MediaPlayer player = createAndPrepareMediaPlayer( resourceFilePath );
        return new MediaPlayerAudioResource( player );
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
        player.setDataSource( fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength() );
        player.prepare();
        return player;
        }


    private final AssetManager myAssetManager;
    }
