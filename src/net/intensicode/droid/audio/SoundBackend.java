package net.intensicode.droid.audio;

import net.intensicode.core.*;

import java.io.IOException;

public interface SoundBackend
    {
    int numberOfChannels();

    MusicResource loadMusic( String aMusicName ) throws IOException;

    SoundResource loadSound( String aSoundName ) throws IOException;
    }
