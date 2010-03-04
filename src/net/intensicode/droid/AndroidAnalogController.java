package net.intensicode.droid;

import android.view.MotionEvent;
import net.intensicode.core.AnalogController;
import net.intensicode.util.FixedMath;

public final class AndroidAnalogController extends AnalogController
    {
    public AndroidAnalogController( final AndroidSensorsManager aSensorsManager )
        {
        mySensorsManager = aSensorsManager;
        }

    public final synchronized void onTrackballEvent( final MotionEvent aMotionEvent )
        {
        if ( aMotionEvent.getAction() != MotionEvent.ACTION_MOVE ) return;
        myLatestX += aMotionEvent.getX();
        myLatestY += aMotionEvent.getY();
        myNewDataFlag = true;
        }

    // From AnalogController

    protected final void mapOrientationToMovement()
        {
        myLatestX = mySensorsManager.orientation.pitchFixed * -( 1f / 180f) / FixedMath.FIXED_1;
        myLatestY = mySensorsManager.orientation.rollFixed * ( 1f / 90f) / FixedMath.FIXED_1;
        myNewDataFlag = true;
        }

    protected final void mapAccelerationToMovement()
        {
        }

    protected final boolean hasNewData()
        {
        return myNewDataFlag;
        }

    protected final synchronized void updateDeltaValues()
        {
        xDeltaFixed = FixedMath.mul( xSensitivityFixed, (int) ( myLatestX * FixedMath.FIXED_10 ) );
        yDeltaFixed = FixedMath.mul( ySensitivityFixed, (int) ( myLatestY * FixedMath.FIXED_10 ) );
        myLatestX = myLatestY = 0;
        myNewDataFlag = false;
        }


    private float myLatestX;

    private float myLatestY;

    private boolean myNewDataFlag;

    private final AndroidSensorsManager mySensorsManager;
    }
