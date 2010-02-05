package net.intensicode.droid.audio;

import net.intensicode.core.AudioResourceEx;

import java.io.IOException;

public interface AudioBackend
    {
    int numberOfChannels();

    AudioResourceEx loadMusic( String aMusicName ) throws IOException;

    AudioResourceEx loadSound( String aSoundName ) throws IOException;
    }
