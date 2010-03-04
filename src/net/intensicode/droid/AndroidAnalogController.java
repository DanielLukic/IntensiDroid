package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.core.AnalogController;
import net.intensicode.util.FixedMath;

public final class AndroidAnalogController extends AnalogController
    {
    // TODO: Queue here and add up on tick?

    public final synchronized void onTrackballEvent( final MotionEvent aMotionEvent )
        {
        if ( aMotionEvent.getAction() != MotionEvent.ACTION_MOVE ) return;
        myLatestX += aMotionEvent.getX();
        myLatestY += aMotionEvent.getY();
        myNewDataFlag = true;
        }

    // From AnalogController

    protected final synchronized void mapOrientationToMovement()
        {
        }

    protected final synchronized void mapAccelerationToMovement()
        {
        }

    protected final synchronized boolean hasNewData()
        {
        return myNewDataFlag;
        }

    protected final synchronized void updateDeltaValues()
        {
        xDeltaFixed = (int) ( myLatestX * FixedMath.FIXED_100 );
        yDeltaFixed = (int) ( myLatestY * FixedMath.FIXED_100 );
        myLatestX = myLatestY = 0;
        myNewDataFlag = false;
        }


    private float myLatestX;

    private float myLatestY;

    private boolean myNewDataFlag;
    }
