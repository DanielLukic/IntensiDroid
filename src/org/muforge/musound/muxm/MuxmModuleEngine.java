package org.muforge.musound.muxm;

import net.intensicode.droid.audio.ModuleEngine;
import net.intensicode.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/* ================================================================
 * MuXM - MOD/XM/S3M player library for J2ME/J2SE
 * Copyright (C) 2005 Martin Cameron, Guillaume Legris
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * ================================================================
 */

/**
 * Main class for playing modules (MOD, XM, S3M).
 *
 * @author Martin Cameron
 */
public final class MuxmModuleEngine implements ModuleEngine
    {
    static final String VERSION = "Alpha 31d (IntensiCode)";

    static final int CHANNELS_FOR_STEREO = 2;

    static final int BYTES_PER_CHANNEL = 2; // 16 bit

    static final int FRAME_SIZE_IN_BYTES = CHANNELS_FOR_STEREO * BYTES_PER_CHANNEL;

    static final int FP_SHIFT = 15;

    static final int FP_ONE = 1 << FP_SHIFT;

    static final int FP_MASK = FP_ONE - 1;

    public MuxmModuleEngine()
        {
        Log.info( "MuXM " + VERSION + " (c)2005 mumart@gmail.com" );
        setSampleRate( 22050 );
        setGain( FP_ONE >> 2 ); // 0.25
        setModule( new Module() );
        }

    public final void setSampleRate( final int aSampleRate )
        {
        mySampleRate = aSampleRate;
        myLeftMixBuffer = new int[mySampleRate / 10];
        myRightMixBuffer = new int[mySampleRate / 10];
        }

    public final void setLooping( final boolean aLoopingFlag )
        {
        myLoopingFlag = aLoopingFlag;
        }

    public final void load( final byte[] aModuleData ) throws IOException
        {
        final Module module = Loader.load( new ByteArrayInputStream( aModuleData ) );
        setModule( module );
        }

    public final int getSampleRate()
        {
        return mySampleRate;
        }

    public final int getStereoAudio( final byte[] aOutputBuffer )
        {
        final int tickLength = getTickLength();

        int bpos = 0;
        int remainingFrames = aOutputBuffer.length / FRAME_SIZE_IN_BYTES;
        while ( remainingFrames > 0 )
            {
            final int tickRem = tickLength - myTickPos;

            int count = remainingFrames;
            if ( count > tickRem ) count = tickRem;

            for ( int n = 0; n < count; n++ )
                {
                final int l = myLeftMixBuffer[ myTickPos + n ];
                final int r = myRightMixBuffer[ myTickPos + n ];
                aOutputBuffer[ bpos++ ] = (byte) ( l & 0xFF );
                aOutputBuffer[ bpos++ ] = (byte) ( l >> 8 );
                aOutputBuffer[ bpos++ ] = (byte) ( r & 0xFF );
                aOutputBuffer[ bpos++ ] = (byte) ( r >> 8 );
                }

            myTickPos += count;
            if ( myTickPos >= tickLength )
                {
                final boolean songEnd = getTick();
                if ( songEnd && !myLoopingFlag ) break;
                }

            remainingFrames -= count;
            }

        return bpos;
        }

    public final void restart()
        {
        reset();
        }

    private void setGain( final int aGain )
        {
        myGain = aGain;
        }

    private void setModule( final Module aModule )
        {
        myModule = aModule;
        myChannels = new Channel[aModule.patterns[ 0 ].channels];
        reset();
        }

    private void reset()
        {
        final Note gvol = new Note();
        gvol.vol = 64;
        for ( int n = 0; n < myChannels.length; n++ )
            {
            myChannels[ n ] = new Channel( n, mySampleRate, gvol, myModule.amiga, myModule.xm, myModule.linear );
            }
        row = nextRow = 0;
        pattern = nextPattern = 0;
        loopCount = loopChan = 0;
        tick = tempo = myModule.tempo;
        bpm = myModule.bpm;
        row();
        getTick();
        }

    private boolean row()
        {
        boolean songEnd = false;
        if ( nextPattern < pattern ) songEnd = true;
        if ( nextPattern == pattern && nextRow <= row && loopCount <= 0 ) songEnd = true;

        pattern = nextPattern;
        row = nextRow;

        nextRow = row + 1;
        if ( nextRow >= myModule.patterns[ myModule.patternOrder[ pattern ] ].rows )
            {
            nextPattern = pattern + 1;
            nextRow = 0;
            }

        for ( int n = 0; n < myChannels.length; n++ )
            {
            myModule.patterns[ myModule.patternOrder[ pattern ] ].getNote( myNote, row, n );

            Instrument i = null;
            if ( myNote.inst > 0 && myNote.inst < myModule.instruments.length ) i = myModule.instruments[ myNote.inst ];
            myChannels[ n ].row( myNote.key, i, myNote.vol, myNote.fx, myNote.fp );

            final int fp = myNote.fp & 0xFF;
            final int fp1 = fp >> 4;
            final int fp2 = fp & 0xF;
            switch ( myNote.fx )
                {
                case Channel.FX_SET_SPEED:
                    if ( fp < 32 ) tick = tempo = myNote.fp;
                    else bpm = myNote.fp;
                    break;
                case Channel.FX_PAT_JUMP:
                    if ( loopCount <= 0 )
                        {
                        nextPattern = fp;
                        nextRow = 0;
                        }
                    break;
                case Channel.FX_PAT_BREAK:
                    if ( loopCount <= 0 )
                        {
                        nextPattern = pattern + 1;
                        nextRow = fp1 * 10 + fp2;
                        }
                    break;
                case Channel.FX_EXTENDED:
                    switch ( fp & 0xF0 )
                        {
                        case Channel.EFX_PAT_DELAY:
                            tick = tempo + tempo * fp2;
                            break;
                        case Channel.EFX_PAT_LOOP:
                            if ( fp2 == 0 ) myChannels[ n ].loopMark = row;
                            if ( fp2 > 0 && myChannels[ n ].loopMark < row )
                                {
                                if ( loopCount <= 0 )
                                    {
                                    loopCount = fp2;
                                    loopChan = n;
                                    nextRow = myChannels[ n ].loopMark;
                                    nextPattern = pattern;
                                    }
                                else if ( loopChan == n )
                                    {
                                    if ( loopCount == 1 )
                                        {
                                        myChannels[ n ].loopMark = row + 1;
                                        }
                                    else
                                        {
                                        nextRow = myChannels[ n ].loopMark;
                                        nextPattern = pattern;
                                        }
                                    loopCount--;
                                    }
                                }
                            break;
                        }
                    break;
                }
            }

        // Make sure next row and pattern are valid.
        if ( nextPattern >= myModule.patternOrder.length ) nextPattern = myModule.restart;
        if ( myModule.patternOrder[ nextPattern ] >= myModule.patterns.length ) nextPattern = 0;
        if ( nextRow >= myModule.patterns[ myModule.patternOrder[ nextPattern ] ].rows ) nextRow = 0;

        return songEnd;
        }

    private int getTickLength()
        {
        return ( mySampleRate * 2 + mySampleRate / 2 ) / bpm;
        }

    private boolean getTick()
        {
        final int tickLength = getTickLength();
        myTickPos = 0;

        final int rlen = tickLength + VRAMP_LEN;
        for ( int n = 0; n < rlen; n++ )
            {
            myLeftMixBuffer[ n ] = myRightMixBuffer[ n ] = 0;
            }

        for ( int n = 0; n < myChannels.length; n++ )
            {
            final Channel c = myChannels[ n ];
            resample( c, rlen );
            c.sampleFrac += c.step * tickLength;
            c.samplePos += c.sampleFrac >> FP_SHIFT;
            c.sampleFrac &= FP_MASK;
            }

        for ( int n = 0; n < VRAMP_LEN; n++ )
            {
            final int va = VRAMP_LEN - n;
            final int vn = myLeftMixBuffer.length - va;
            myLeftMixBuffer[ n ] = myLeftMixBuffer[ n ] * n + myLeftMixBuffer[ vn ] * va >> VRAMP_SHIFT;
            myRightMixBuffer[ n ] = myRightMixBuffer[ n ] * n + myRightMixBuffer[ vn ] * va >> VRAMP_SHIFT;
            myLeftMixBuffer[ vn ] = myLeftMixBuffer[ tickLength + n ];
            myRightMixBuffer[ vn ] = myRightMixBuffer[ tickLength + n ];
            }

        return tick();
        }

    private boolean tick()
        {
        tick--;
        if ( tick <= 0 )
            {
            tick = tempo;
            return row();
            }
        for ( int n = 0; n < myChannels.length; n++ )
            {
            myChannels[ n ].tick();
            }
        return false;
        }

    private void resample( final Channel c, int len )
        {
        int ampl = c.ampl;
        if ( ampl == 0 ) return;

        int offset = 0;
        ampl = ampl * myGain >> FP_SHIFT;
        final int pann = c.pann;
        final int lvol = ampl * ( FP_ONE - pann ) >> FP_SHIFT;
        final int rvol = ampl * ( pann ) >> FP_SHIFT;
        final int step = c.step;
        int spos = c.samplePos;
        int frac = c.sampleFrac;

        final int maxlen = INPUT_SAMPLES * FP_ONE / step;
        while ( len > 0 )
            {
            int count = maxlen;
            if ( count > len ) count = len;

            final int isam = count * step >> FP_SHIFT;
            c.sample.getSamples( spos - OVERLAP_SAMPLES + 1, myInputBuffer, 0, isam + OVERLAP_SAMPLES * 2 );
            resample( frac, step, lvol, rvol, offset, count );
            frac += step * count;
            spos += frac >> FP_SHIFT;
            frac &= FP_MASK;
            offset += count;
            len -= count;
            }
        }

    private void resample( int inputFrac, final int step, final int lAmp, final int rAmp, int pos, final int count )
        {
        int inputPos = OVERLAP_SAMPLES - 1;
        for ( int n = 0; n < count; n++ )
            {
            int out = myInputBuffer[ inputPos ];
            out += ( myInputBuffer[ inputPos + 1 ] - out ) * inputFrac >> FP_SHIFT;
            myLeftMixBuffer[ pos ] += out * lAmp >> FP_SHIFT;
            myRightMixBuffer[ pos ] += out * rAmp >> FP_SHIFT;
            pos++;
            inputFrac += step;
            inputPos += inputFrac >> FP_SHIFT;
            inputFrac &= FP_MASK;
            }
        }


    private int tick;

    private int tempo;

    private int bpm;

    private int row;

    private int myGain;

    private int nextRow;

    private int pattern;

    private int nextPattern;

    private int loopCount;

    private int loopChan;

    private int myTickPos;

    private Module myModule;

    private int mySampleRate;

    private boolean myLoopingFlag;

    private int[] myLeftMixBuffer;

    private int[] myRightMixBuffer;

    private Channel[] myChannels = new Channel[0];

    private final Note myNote = new Note();

    private final int INPUT_SAMPLES = 512;

    private final int OVERLAP_SAMPLES = 64;

    private final int VRAMP_SHIFT = 4;

    private final int VRAMP_LEN = 1 << VRAMP_SHIFT;

    private final short[] myInputBuffer = new short[INPUT_SAMPLES + OVERLAP_SAMPLES * 2];
    }
