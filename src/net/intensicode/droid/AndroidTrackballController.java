package net.intensicode.droid;

import android.os.SystemClock;
import android.view.MotionEvent;
import net.intensicode.core.TrackballControllerBase;
import net.intensicode.util.Assert;

public final class AndroidTrackballController extends TrackballControllerBase
    {
    public final void onTrackballEvent( final MotionEvent aMotionEvent )
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

        if ( aMotionEvent.getAction() != MotionEvent.ACTION_MOVE ) return;

        Assert.equals( "x precision", 6, aMotionEvent.getXPrecision(), 0.0001f );
        Assert.equals( "y precision", 6, aMotionEvent.getYPrecision(), 0.0001f );

        final float xMove = aMotionEvent.getX() * aMotionEvent.getXPrecision();
        final float yMove = aMotionEvent.getY() * aMotionEvent.getYPrecision();

        final int xSteps = Math.round( xMove );
        final int ySteps = Math.round( yMove );

        Assert.equals( "non-fractional steps", 0, xMove - xSteps, 0.0001f );
        Assert.equals( "non-fractional y steps", 0, yMove - ySteps, 0.0001f );

        onSystemUpdateEvent( xSteps, ySteps, aMotionEvent.getEventTime() );
        }

    // From TrackballControllerBase

    protected long systemSpecificNowInMillis()
        {
        return SystemClock.uptimeMillis();
        }
    }
