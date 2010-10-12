package net.intensicode.droid.audio;

import android.media.MediaPlayer;
import net.intensicode.core.AudioResourceEx;
import net.intensicode.util.Log;

final class MediaPlayerAudioResource implements AudioResourceEx, MediaPlayer.OnErrorListener
    {
    public MediaPlayerAudioResource( final MediaPlayer aPlayer, final String aResourcePath )
        {
        myPlayer = aPlayer;
        myPlayer.setOnErrorListener( this );
        myResourcePath = aResourcePath;
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

    public final void enable()
        {
        if ( myEnabledFlag ) return;

        myEnabledFlag = true;

        if ( myPlayingFlag ) myPlayer.start();
        }

    public final void disable()
        {
        if ( !myEnabledFlag ) return;

        myPlayingFlag = myPlayer.isPlaying();
        myEnabledFlag = false;

        if ( myPlayer.isPlaying() ) myPlayer.pause();
        }

    // From AudioResource

    public final void setLoopForever()
        {
        myPlayer.setLooping( true );
        }

    public final void setVolume( final int aVolumeInPercent )
        {
        myCurrentVolume = aVolumeInPercent;

        final float scaledVolume = aVolumeInPercent * 1.0f / 100;
        myPlayer.setVolume( scaledVolume, scaledVolume );
        }

    public final void mute()
        {
        myPlayer.setVolume( 0, 0 );
        }

    public final void unmute()
        {
        setVolume( myCurrentVolume );
        }

    public final void start()
        {
        if ( myPlayer.isPlaying() ) stop();
        if ( myEnabledFlag ) myPlayer.start();
        else triggerPlayAfterEnable();
        }

    public final void stop()
        {
        if ( myPlayer.isPlaying() ) myPlayer.stop();
        }

    public final void pause()
        {
        if ( myPlayer.isPlaying() ) myPlayer.pause();
        }

    public final void resume()
        {
        if ( myEnabledFlag ) myPlayer.start();
        else triggerPlayAfterEnable();
        }

    // Implementation

    private void triggerPlayAfterEnable()
        {
        myPlayingFlag = true;
        }


    private int myCurrentVolume;

    private boolean myPlayingFlag;

    private boolean myEnabledFlag = true;

    private final MediaPlayer myPlayer;

    private final String myResourcePath;
    }
