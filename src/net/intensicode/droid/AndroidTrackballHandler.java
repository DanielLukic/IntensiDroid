//#condition TRACKBALL

package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.trackball.TrackballHandler;
import net.intensicode.util.*;

public final class AndroidTrackballHandler extends TrackballHandler
    {
    public final boolean onTrackballEvent( final MotionEvent aMotionEvent )
        {
        // collect system events until some 'silence in ms' has passed.
        // how to check passed time?
        // push them down and have the onControlTick check timestamps.
        // so: dont queue up here. pass down right away.

        // anyway: detecting separate strokes is the problem.

        // timestamp is probably the easy part. the user should be able
        // to set a 'silence in ms'. if this happens between two events,
        // the accumulated events before the 'gap' should be made into
        // an update.

        // in this 'event stroke turned into an update' there should be
        // some normalizing. like: for example six events make up the
        // stroke. Only one has a x-delta. the x-delta should be nulled.

        if ( aMotionEvent.getAction() != MotionEvent.ACTION_MOVE ) return false;

        Assert.equals( "x precision", 6, aMotionEvent.getXPrecision(), 0.0001f );
        Assert.equals( "y precision", 6, aMotionEvent.getYPrecision(), 0.0001f );

        final float xMove = aMotionEvent.getX() * aMotionEvent.getXPrecision();
        final float yMove = aMotionEvent.getY() * aMotionEvent.getYPrecision();

        final int xSteps = Math.round( xMove );
        final int ySteps = Math.round( yMove );

        Assert.equals( "non-fractional steps", 0, xMove - xSteps, 0.0001f );
        Assert.equals( "non-fractional y steps", 0, yMove - ySteps, 0.0001f );

        //#if DEBUG_TRACKBALL
        dumpMotionEvent( aMotionEvent );
        //#endif

        myEventWrapper.init( aMotionEvent, xSteps, ySteps );
        processTrackballEvent( myEventWrapper );

        // It is necessary to return false here. Only then will Android create key events
        // from the trackball. These are required in non-game screens.
        return false;
        }

    //#if DEBUG_TRACKBALL

    private void dumpMotionEvent( final MotionEvent aMotionEvent )
        {
        final long delta = aMotionEvent.getEventTime() - myPreviousTimestamp;
        final StringBuffer buffer = new StringBuffer( "MOTIONEVENT " );
        buffer.append( "delta=" ).append( delta );
        buffer.append( ",action=" ).append( aMotionEvent.getAction() );
        buffer.append( ",downtime=" ).append( aMotionEvent.getDownTime() );
        buffer.append( ",edgeflags=" ).append( aMotionEvent.getEdgeFlags() );
        buffer.append( ",eventtime=" ).append( aMotionEvent.getEventTime() );
        buffer.append( ",pressure=" ).append( aMotionEvent.getPressure() );
        buffer.append( ",rawx=" ).append( aMotionEvent.getRawX() );
        buffer.append( ",rawy=" ).append( aMotionEvent.getRawY() );
        buffer.append( ",size=" ).append( aMotionEvent.getSize() );
        buffer.append( ",x=" ).append( aMotionEvent.getX() );
        buffer.append( ",xprec=" ).append( aMotionEvent.getXPrecision() );
        buffer.append( ",y=" ).append( aMotionEvent.getY() );
        buffer.append( ",yprec=" ).append( aMotionEvent.getXPrecision() );
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_DOWN ) Log.info( "MOTIONEVENT DOWN" );
        Log.info( buffer.toString() );
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_UP ) Log.info( "MOTIONEVENT UP" );
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_CANCEL ) Log.info( "MOTIONEVENT CANCEL" );
        myPreviousTimestamp = aMotionEvent.getEventTime();
        }

    private long myPreviousTimestamp;

    //#endif

    private final AndroidTrackballEventWrapper myEventWrapper = new AndroidTrackballEventWrapper();
    }
