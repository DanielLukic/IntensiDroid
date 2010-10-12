package net.intensicode.droid.audio;

final class ModplugModuleEngine implements ModuleEngine
    {
    public ModplugModuleEngine()
        {
        System.loadLibrary( "modplug" );
        }

    public final void setSampleRate( final int aSampleRate )
        {
        mySampleRate = aSampleRate;
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myLoopingFlag = aLoopingFlag;
        setLoop( myModuleHandle, aLoopingFlag );
        }

    public final void load( final byte[] aModuleData )
        {
        if ( myModuleHandle != 0 ) unload( myModuleHandle );
        myModuleHandle = load( aModuleData, aModuleData.length, mySampleRate, myLoopingFlag );
        }

    public final int getSampleRate()
        {
        return DEFAULT_SAMPLE_RATE;
        }

    public final int getStereoAudio( final byte[] aOutputBuffer )
        {
        return getSoundData( myModuleHandle, aOutputBuffer, aOutputBuffer.length );
        }

    public final void restart()
        {
        restart( myModuleHandle );
        }

    private int mySampleRate = DEFAULT_SAMPLE_RATE;

    private long myModuleHandle;

    private boolean myLoopingFlag;

    private native long load( byte[] aModuleData, int aSizeInBytes, int aSampleRate, boolean aLoopingFlag );

    private native void setLoop( long aModuleHandle, boolean aLoopingFlag );

    private native void unload( long aModuleHandle );

    private native int getSoundData( long aModuleHandle, byte[] aOutputBuffer, int aSizeInBytes );

    private native int restart( long aModuleHandle );

    private static final int DEFAULT_SAMPLE_RATE = 44100 / 4;
    }
