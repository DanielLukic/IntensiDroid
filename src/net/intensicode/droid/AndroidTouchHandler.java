package net.intensicode.droid;

import android.view.*;
import net.intensicode.core.*;

public final class AndroidTouchHandler extends TouchHandler implements View.OnTouchListener
    {
    public AndroidTouchHandler( final GameSystem aGameSystem, final DirectScreen aDirectScreen )
        {
        super( aGameSystem );
        myTouchEventWrapper = new AndroidTouchEventWrapper( aDirectScreen );
        }

    public final boolean supportsMultiTouch()
        {
        return false;
        }

    // From OnTouchListener

    public final synchronized boolean onTouch( final View aView, final MotionEvent aMotionEvent )
        {
        myTouchEventWrapper.init( aMotionEvent );
        processTouchEvent( myTouchEventWrapper );

        // It is necessary to return true here. Only then is it possible to get "swipe touch events"..
        // And there really is nobody "underneath" who might care for these events anyway..
        return true;
        }

    private final AndroidTouchEventWrapper myTouchEventWrapper;
    }
