//#condition SENSORS

package net.intensicode.droid;

import android.content.Context;
import android.hardware.*;
import net.intensicode.core.SensorsManager;
import net.intensicode.util.*;

import java.util.List;

public final class AndroidSensorsManager extends SensorsManager implements SensorEventListener
    {
    // TODO: Queue events, too. Handle in engine control tick..

    public AndroidSensorsManager( final Context aContext )
        {
        myService = (SensorManager) aContext.getSystemService( Context.SENSOR_SERVICE );

        listAvailableSensors();
        initSupportedSensors();
        }

    // From SensorsManager

    public final boolean hasOrientation()
        {
        return myOrientation != null;
        }

    public final boolean hasAcceleration()
        {
        return myAcceleration != null;
        }

    public final synchronized void onControlTick()
        {
        acceleration.x = myPreviousAcceleration[ 0 ];
        acceleration.y = myPreviousAcceleration[ 1 ];
        acceleration.z = myPreviousAcceleration[ 2 ];

        orientation.azimuth = myPreviousOrientation[ 0 ];
        orientation.pitch = myPreviousOrientation[ 1 ];
        orientation.roll = myPreviousOrientation[ 2 ];
        }

    public final void enable()
        {
        if ( myAcceleration != null )
            {
            Log.debug( "enabling acceleration sensor" );
            myService.registerListener( this, myAcceleration, SensorManager.SENSOR_DELAY_GAME );
            }
        if ( myOrientation != null )
            {
            Log.debug( "enabling orientation sensor" );
            myService.registerListener( this, myOrientation, SensorManager.SENSOR_DELAY_GAME );
            }
        }

    public final void disable()
        {
        if ( myAcceleration != null || myOrientation != null )
            {
            Log.debug( "disabling sensors processing" );
            myService.unregisterListener( this );
            }
        }

    // From SensorEventListener

    public final synchronized void onSensorChanged( final SensorEvent aSensorEvent )
        {
        if ( aSensorEvent.sensor == myAcceleration ) updateAccelerometerIfChanged( aSensorEvent.values );
        if ( aSensorEvent.sensor == myOrientation ) updateOrientationIfChanged( aSensorEvent.values );
        }

    private void updateAccelerometerIfChanged( final float[] aValues )
        {
        if ( !valuesHaveChanged( aValues, myPreviousAcceleration ) ) return;
        copyFromTo( aValues, myPreviousAcceleration );
        }

    private void updateOrientationIfChanged( final float[] aValues )
        {
        if ( !valuesHaveChanged( aValues, myPreviousOrientation ) ) return;
        copyFromTo( aValues, myPreviousOrientation );
        }

    private boolean valuesHaveChanged( final float[] aValues, final float[] aPreviousValues )
        {
        Assert.equals( "size of value array matches", aValues.length, aPreviousValues.length );
        for ( int idx = 0; idx < aPreviousValues.length; idx++ )
            {
            if ( aValues[ idx ] != aPreviousValues[ idx ] ) return true;
            }
        return false;
        }

    private void copyFromTo( final float[] aValues, final float[] aPreviousValues )
        {
        for ( int idx = 0; idx < aPreviousValues.length; idx++ )
            {
            aPreviousValues[ idx ] = aValues[ idx ];
            }
        }

    public final void onAccuracyChanged( final Sensor aSensor, final int aNewAccuracy )
        {
        }

    // Implementation

    private void listAvailableSensors()
        {
        final List<Sensor> sensors = myService.getSensorList( Sensor.TYPE_ALL );
        for ( final Sensor sensor : sensors )
            {
            System.out.println( "sensor found: " + sensor );
            System.out.println( "type: " + getTypeName( sensor ) );
            System.out.println( "name: " + sensor.getName() );
            System.out.println( "vendor: " + sensor.getVendor() );
            }
        }

    private void initSupportedSensors()
        {
        if ( AndroidUtilities.isEmulator() ) return;
        myAcceleration = myService.getDefaultSensor( SensorManager.SENSOR_ACCELEROMETER );
        myOrientation = myService.getDefaultSensor( SensorManager.SENSOR_ORIENTATION );
        }

    private String getTypeName( final Sensor aSensor )
        {
        final int type = aSensor.getType();
        if ( type == Sensor.TYPE_ACCELEROMETER ) return "accelerometer";
        if ( type == Sensor.TYPE_GYROSCOPE ) return "gyroscope";
        if ( type == Sensor.TYPE_LIGHT ) return "light";
        if ( type == Sensor.TYPE_MAGNETIC_FIELD ) return "magnetic field";
        if ( type == Sensor.TYPE_ORIENTATION ) return "orientation";
        if ( type == Sensor.TYPE_PRESSURE ) return "pressure";
        if ( type == Sensor.TYPE_PROXIMITY ) return "proximity";
        if ( type == Sensor.TYPE_TEMPERATURE ) return "temperature";
        throw new IllegalArgumentException();
        }


    private Sensor myOrientation;

    private Sensor myAcceleration;

    private final SensorManager myService;

    private final float[] myPreviousOrientation = new float[3];

    private final float[] myPreviousAcceleration = new float[3];
    }
