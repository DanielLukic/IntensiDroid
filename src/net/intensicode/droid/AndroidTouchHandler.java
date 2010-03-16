//#condition TOUCH

package net.intensicode.droid;

import android.os.Build;
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
        try
            {
            final int version = Integer.parseInt( Build.VERSION.SDK );
            if ( version <= SDK_VERSION_DONUT ) return false;
            }
        catch ( final Throwable t )
            {
            // Let's be safe in this case..
            return false;
            }
        // Let's assume we can..
        return true;
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

    private static final int SDK_VERSION_DONUT = 4;
    }
