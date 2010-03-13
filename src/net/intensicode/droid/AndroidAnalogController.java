package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.core.AnalogControllerBase;
import net.intensicode.util.Assert;

public final class AndroidAnalogController extends AnalogControllerBase
    {
    public final void onTrackballEvent( final MotionEvent aMotionEvent )
        {
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
    }
