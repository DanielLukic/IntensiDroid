package net.intensicode.droid.audio;

import java.io.IOException;

public interface ModuleEngine
    {
    void setSampleRate( int aSampleRate );

    void setLooping( boolean aLoopingFlag );

    void load( byte[] aModuleData ) throws IOException;

    int getSampleRate();

    int getStereoAudio( byte[] aOutputBuffer );

    void restart();
    }
