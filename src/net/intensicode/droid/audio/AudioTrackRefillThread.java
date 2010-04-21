package net.intensicode.droid.audio;

import android.media.AudioTrack;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.ModuleEngine;

final class AudioTrackRefillThread extends Thread implements AudioTrack.OnPlaybackPositionUpdateListener
    {
    public static final int FRAME_SIZE_IN_BYTES = 4;


    public AudioTrackRefillThread( final AudioTrack aAudioTrack, final byte[] aAudioBuffer, final ModuleEngine aModuleEngine )
        {
        super( "AudioTrackRefillThread" );
        myAudioTrack = aAudioTrack;
        myAudioBuffer = aAudioBuffer;
        myModuleEngine = aModuleEngine;
        mySongDuration = myModuleEngine.getSongLength();
        myBufferSizeInFrames = aAudioBuffer.length / FRAME_SIZE_IN_BYTES;
        myAudioTrack.setPlaybackPositionUpdateListener( this );
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myLoopingFlag = aLoopingFlag;
        }

    public final void startAudioTrack()
        {
        myAudioTrack.play();
        fillAudioTrackBuffer();
        }

    public final void stopAudioTrack()
        {
        myAudioTrack.stop();
        myAudioTrack.flush();
        }

    // From Thread

    public void run()
        {
        while ( true )
            {
            try
                {
                synchronized ( this )
                    {
                    wait();
                    }
                fillAudioTrackBuffer();
                }
            catch ( final InterruptedException e )
                {
                //#if DEBUG_AUDIO
                Log.debug( "AudioTrackRefillThread interrupted. Bailing out.." );
                //#endif
                break;
                }
            }
        }

    // From AudioTrack.OnPlaybackPositionUpdateListener

    public final void onMarkerReached( final AudioTrack aAudioTrack )
        {
        //#if DEBUG_AUDIO
        Log.debug( "onMarkerReached audiotrack thread: {}", Thread.currentThread() );
        //#endif
        synchronized ( this )
            {
            notify();
            }
        }

    public final void onPeriodicNotification( final AudioTrack aAudioTrack )
        {
        //#if DEBUG_AUDIO
        Log.debug( "onPeriodicNotification audiotrack thread: {}", Thread.currentThread() );
        //#endif
        }

    // Implementation

    public final void fillAudioTrackBuffer()
        {
        //#if DEBUG_AUDIO
        Log.debug( "fillAudioTrackBuffer audiotrack thread: {}", Thread.currentThread() );
        //#endif

        myModuleEngine.getAudio( myAudioBuffer, 0, myBufferSizeInFrames, true );
        myAudioTrack.write( myAudioBuffer, 0, myAudioBuffer.length );
        myPlayPosition += myBufferSizeInFrames;
        if ( myPlayPosition >= mySongDuration )
            {
            myPlayPosition = 0;
            if ( !myLoopingFlag ) stopAudioTrack();
            }

        myAudioTrack.setNotificationMarkerPosition( myBufferSizeInFrames * 3 / 4 );
        }


    private int myPlayPosition;

    private boolean myLoopingFlag;

    private final int mySongDuration;

    private final byte[] myAudioBuffer;

    private final AudioTrack myAudioTrack;

    private final int myBufferSizeInFrames;

    private final ModuleEngine myModuleEngine;
    }
