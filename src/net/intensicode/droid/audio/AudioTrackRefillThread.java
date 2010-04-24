package net.intensicode.droid.audio;

import android.media.AudioTrack;
import net.intensicode.util.Log;
import org.muforge.musound.muxm.ModuleEngine;

import java.util.Hashtable;

final class AudioTrackRefillThread extends Thread implements AudioTrack.OnPlaybackPositionUpdateListener
    {
    public static final int FRAME_SIZE_IN_BYTES = 4;

    public boolean buffer = true;


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

    public final void fillAudioTrackBuffer()
        {
        //#if DEBUG_AUDIO
        Log.debug( "fillAudioTrackBuffer audiotrack thread: {}", Thread.currentThread() );
        //#endif

        if ( buffer ) playBuffered();
        else playDirect();

        myPlayPosition += myBufferSizeInFrames;
        if ( myPlayPosition >= mySongDuration )
            {
            myPlayPosition = 0;
            if ( !myLoopingFlag ) stopAudioTrack();
            }

        myAudioTrack.setNotificationMarkerPosition( myBufferSizeInFrames * 3 / 4 );
        }

    // Implementation

    private Hashtable myCachedBuffers = new Hashtable();

    private void playBuffered()
        {
        final Integer key = new Integer( myPlayPosition );
        if ( !myCachedBuffers.containsKey( key ) )
            {
            final byte[] buffer = new byte[myAudioBuffer.length];
            myModuleEngine.getAudio( buffer, 0, myBufferSizeInFrames, true );
            myCachedBuffers.put( key, buffer );
            //#if DEBUG_AUDIO
            Log.info( "cached buffers: {}", myCachedBuffers.size() );
            Log.info( "cached buffers size: {} KB", myCachedBuffers.size() * buffer.length / 1024f );
            //#endif
            }

        final byte[] buffer = (byte[]) myCachedBuffers.get( key );
        myAudioTrack.write( buffer, 0, buffer.length );
        }

    private void playDirect()
        {
        myModuleEngine.getAudio( myAudioBuffer, 0, myBufferSizeInFrames, true );
        myAudioTrack.write( myAudioBuffer, 0, myAudioBuffer.length );
        myCachedBuffers.clear();
        }


    private int myPlayPosition;

    private boolean myLoopingFlag;

    private final int mySongDuration;

    private final byte[] myAudioBuffer;

    private final AudioTrack myAudioTrack;

    private final int myBufferSizeInFrames;

    private final ModuleEngine myModuleEngine;
    }
