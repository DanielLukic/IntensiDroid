package net.intensicode.droid.audio;

import android.media.MediaPlayer;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.util.Log;

public final class MediaPlayerAudioResource implements AudioResourceEx, MediaPlayer.OnErrorListener
    {
    public MediaPlayerAudioResource( final MediaPlayer aPlayer )
        {
        myPlayer = aPlayer;
        myPlayer.setOnErrorListener( this );
        setVolume( 75 );
        }

    // From OnErrorListener

    public boolean onError( final MediaPlayer aMediaPlayer, final int aWhat, final int aExtra )
        {
        if ( aWhat == MediaPlayer.MEDIA_ERROR_SERVER_DIED )
            {
            //#if DEBUG
            Log.debug( "media server died - should wait and start playing if necessary" );
            //#endif
            }
        return true;
        }

    // From AudioResourceEx

    public void enable()
        {
        }

    public void disable()
        {
        }

    // From AudioResource

    public void setLoopForever()
        {
        myPlayer.setLooping( true );
        }

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
