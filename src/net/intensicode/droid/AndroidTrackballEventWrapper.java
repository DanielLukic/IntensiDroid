//#condition TRACKBALL

package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.trackball.TrackballEvent;
import net.intensicode.util.Position;

public class AndroidTrackballEventWrapper extends TrackballEvent
    {
    public final void init( final MotionEvent aMotionEvent, final int aX, final int aY )
        {
        myMotionEvent = aMotionEvent;
        myEventPosition.x = aX;
        myEventPosition.y = aY;
        }

    // From TouchEvent

    public final long timestamp()
        {
        return myMotionEvent.getEventTime();
        }

    public final boolean isPress()
        {
        return myMotionEvent.getAction() == MotionEvent.ACTION_DOWN;
        }

    public final boolean isSwipe()
        {
        return myMotionEvent.getAction() == MotionEvent.ACTION_MOVE;
        }

    public final boolean isRelease()
        {
        return myMotionEvent.getAction() == MotionEvent.ACTION_UP;
        }

    public final int getX()
        {
        return myEventPosition.x;
        }

    public final int getY()
        {
        return myEventPosition.y;
        }

    // From Object

    public final String toString()
        {
        return myMotionEvent.toString();
        }


    private MotionEvent myMotionEvent;

    private final Position myEventPosition = new Position();
    }
