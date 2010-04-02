//#condition TOUCH

package net.intensicode.droid;

import android.os.Build;
import android.view.*;
import net.intensicode.core.*;
import net.intensicode.util.Log;

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
        //#if DEBUG_TOUCH
        dumpMotionEvent( aMotionEvent );
        //#endif

        myTouchEventWrapper.init( aMotionEvent );
        processTouchEvent( myTouchEventWrapper );

        // It is necessary to return true here. Only then is it possible to get "swipe touch events"..
        // And there really is nobody "underneath" who might care for these events anyway..
        return true;
        }

    //#if DEBUG_TOUCH

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
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_DOWN ) Log.info( "MOTIONEVENT DOWN");
        Log.info( buffer.toString() );
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_UP ) Log.info( "MOTIONEVENT UP");
        if ( aMotionEvent.getAction() == MotionEvent.ACTION_CANCEL ) Log.info( "MOTIONEVENT CANCEL");
        myPreviousTimestamp = aMotionEvent.getEventTime();
        }

    private long myPreviousTimestamp;

    //#endif

    private final AndroidTouchEventWrapper myTouchEventWrapper;

    private static final int SDK_VERSION_DONUT = 4;
    }
