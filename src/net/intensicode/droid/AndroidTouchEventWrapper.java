package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.core.*;
import net.intensicode.util.Position;

public class AndroidTouchEventWrapper extends TouchEvent
    {
    public AndroidTouchEventWrapper( final DirectScreen aDirectScreen )
        {
        myDirectScreen = aDirectScreen;
        }

    public final void init( final MotionEvent aMotionEvent )
        {
        myMotionEvent = aMotionEvent;

        final float x = aMotionEvent.getX();
        final float y = aMotionEvent.getY();
        final Position transformed = myDirectScreen.toTarget( Math.round( x ), Math.round( y ) );
        myEventPosition.setTo( transformed );
        }

    // From TouchEvent

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

    private final DirectScreen myDirectScreen;

    private final Position myEventPosition = new Position();
    }
