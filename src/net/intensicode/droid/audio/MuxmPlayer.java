package net.intensicode.droid.audio;

import android.media.*;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.ModuleEngine;

public final class MuxmPlayer implements AudioTrack.OnPlaybackPositionUpdateListener
    {
    public MuxmPlayer( final ModuleEngine aModuleEngine )
        {
        myModuleEngine = aModuleEngine;

        final int minBufferSize = AudioTrack.getMinBufferSize( DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT );
        final int frameCompatibleBuffer = minBufferSize / FRAME_SIZE_IN_BYTES * FRAME_SIZE_IN_BYTES;

        //#if DEBUG
        Log.debug( "AudioTrack Native Sample Rate: {}", AudioTrack.getNativeOutputSampleRate( AudioTrack.MODE_STREAM ) );
        Log.debug( "AudioTrack Volume Range: {} - {}", AudioTrack.getMinVolume(), AudioTrack.getMaxVolume() );
        Log.debug( "AudioTrack MinBufferSize: {}", minBufferSize );
        Log.debug( "IbxmPlayer BufferSize: {}", frameCompatibleBuffer );
        //#endif

        final AudioTrack audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, frameCompatibleBuffer, AudioTrack.MODE_STREAM );
        audioTrack.setPlaybackRate( DEFAULT_SAMPLE_RATE );
        audioTrack.setPlaybackPositionUpdateListener( this );
        audioTrack.setPositionNotificationPeriod( frameCompatibleBuffer / FRAME_SIZE_IN_BYTES );

        myAudioTrack = audioTrack;
        myBufferSizeInBytes = frameCompatibleBuffer;
        myBufferSizeInFrames = frameCompatibleBuffer / FRAME_SIZE_IN_BYTES;
        myAudioBuffer = new byte[myBufferSizeInBytes];

        myModuleEngine.setSampleRate( DEFAULT_SAMPLE_RATE );
        mySongDuration = myModuleEngine.getSongLength();
        }

    public final void start()
        {
        if ( isPlaying() ) return;
        myAudioTrack.play();
        fillAudioTrackBuffer();
        }

    private void fillAudioTrackBuffer()
        {
        myModuleEngine.getAudio( myAudioBuffer, 0, myBufferSizeInFrames, true );
        myAudioTrack.write( myAudioBuffer, 0, myBufferSizeInBytes );
        myPlayPosition += myBufferSizeInFrames;
        if ( myPlayPosition >= mySongDuration )
            {
            myPlayPosition = 0;
            if ( !myLoopingFlag ) stop();
            }
        }

    public final boolean isPlaying()
        {
        return myAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
        }

    public final void pause()
        {
        if ( isPlaying() ) myAudioTrack.pause();
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myLoopingFlag = aLoopingFlag;
        }

    public final void setVolume( final int aVolumeInPercent )
        {
        final float volume = ( myAudioTrack.getMaxVolume() - myAudioTrack.getMinVolume() ) * aVolumeInPercent / 100.0f;
        myAudioTrack.setStereoVolume( volume, volume );
        }

    public final void stop()
        {
        if ( isStopped() ) return;
        myAudioTrack.stop();
        myAudioTrack.flush();
        }

    private boolean isStopped()
        {
        return myAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED;
        }

    // From AudioTrack.OnPlaybackPositionUpdateListener

    public final void onMarkerReached( final AudioTrack aAudioTrack )
        {
        // Not used..
        }

    public final void onPeriodicNotification( final AudioTrack aAudioTrack )
        {
        fillAudioTrackBuffer();
        }


    private int myPlayPosition;

    private boolean myLoopingFlag;

    private final int mySongDuration;

    private final byte[] myAudioBuffer;

    private final int myBufferSizeInBytes;

    private final int myBufferSizeInFrames;

    private final AudioTrack myAudioTrack;

    private final ModuleEngine myModuleEngine;

    private static final int FRAME_SIZE_IN_BYTES = 4;

    private static final int DEFAULT_SAMPLE_RATE = 11025;
    }
