package net.intensicode.droid.audio;

import android.media.MediaPlayer;
import net.intensicode.core.*;

public final class MediaPlayerAudioResource implements MusicResource, SoundResource, AudioResource
    {
    public MediaPlayerAudioResource( final MediaPlayer aPlayer )
        {
        myPlayer = aPlayer;
        setVolume( 50 );
        }

    // From MusicResource

    public final void setVolume( final int aVolumeInPercent )
        {
        final float scaledVolume = aVolumeInPercent * 1.0f / 100;
        myPlayer.setVolume( scaledVolume, scaledVolume );
        myCurrentVolume = aVolumeInPercent;
        }

    public final void mute()
        {
        myPlayer.setVolume( 0, 0 );
        }

    public final void unmute()
        {
        setVolume( myCurrentVolume );
        }

    public final void play()
        {
        if ( myPlayer.isPlaying() ) stop();
        myPlayer.start();
        }

    public final void stop()
        {
        myPlayer.stop();
        }

    public final void pause()
        {
        myPlayer.pause();
        }

    public final void resume()
        {
        myPlayer.start();
        }


    private int myCurrentVolume;

    private final MediaPlayer myPlayer;
    }
