package net.intensicode.droid;

import android.app.Activity;
import android.os.Build;
import android.view.Display;
import net.intensicode.PlatformContext;
import net.intensicode.util.Log;

public final class AndroidUtilities
    {
    public static final int ANDROID_ORIENTATION_PORTRAIT_ID = 0;

    public static final int ANDROID_ORIENTATION_LANDSCAPE_ID = 1;


    public static void showDeviceSpecs()
        {
        Log.info( "Board: " + android.os.Build.BOARD );
        Log.info( "Brand: " + android.os.Build.BRAND );
        Log.info( "Device: " + android.os.Build.DEVICE );
        Log.info( "Display: " + android.os.Build.DISPLAY );
        Log.info( "Model: " + android.os.Build.MODEL );
        Log.info( "Product: " + android.os.Build.PRODUCT );
        Log.info( "Tags: " + android.os.Build.TAGS );
        Log.info( "Type: " + android.os.Build.TYPE );
        }

    public static String determineScreenOrientationId( final Activity aActivity )
        {
        final Display display = aActivity.getWindowManager().getDefaultDisplay();

        final int orientation = display.getOrientation();
        if ( orientation == ANDROID_ORIENTATION_PORTRAIT_ID && looksLikePortrait( aActivity ) )
            {
            return PlatformContext.SCREEN_ORIENTATION_PORTRAIT;
            }
        if ( orientation == ANDROID_ORIENTATION_LANDSCAPE_ID && looksLikeLandscape( aActivity ) )
            {
            return PlatformContext.SCREEN_ORIENTATION_LANDSCAPE;
            }

        if ( looksLikePortrait( aActivity ) ) return PlatformContext.SCREEN_ORIENTATION_PORTRAIT;
        if ( looksLikeLandscape( aActivity ) ) return PlatformContext.SCREEN_ORIENTATION_LANDSCAPE;
        if ( looksLikeSquare( aActivity ) ) return PlatformContext.SCREEN_ORIENTATION_SQUARE;

        return PlatformContext.SCREEN_ORIENTATION_PORTRAIT;
        }

    public static boolean looksLikePortrait( final Activity aActivity )
        {
        final Display display = aActivity.getWindowManager().getDefaultDisplay();
        return display.getWidth() < display.getHeight();
        }

    public static boolean looksLikeLandscape( final Activity aActivity )
        {
        final Display display = aActivity.getWindowManager().getDefaultDisplay();
        return display.getWidth() > display.getHeight();
        }

    public static boolean looksLikeSquare( final Activity aActivity )
        {
        final Display display = aActivity.getWindowManager().getDefaultDisplay();
        return display.getWidth() == display.getHeight();
        }

    public static boolean isEmulator()
        {
        final boolean isGeneric = Build.BRAND.toLowerCase().indexOf( "generic" ) != -1;
        final boolean isSdk = Build.MODEL.toLowerCase().indexOf( "sdk" ) != -1;
        return isGeneric && isSdk;
        }

    public static boolean isSamsungGalaxy()
        {
        final boolean isSamsung = Build.BRAND.toLowerCase().indexOf( "samsung" ) != -1;
        final boolean isGalaxy = Build.MODEL.toLowerCase().indexOf( "galaxy" ) != -1;
        return isSamsung && isGalaxy;
        }

    public static boolean isDroidOrMilestone()
        {
        final boolean isMotorola = Build.BRAND.toLowerCase().indexOf( "moto" ) != -1;
        final boolean isDroid = Build.MODEL.toLowerCase().indexOf( "droid" ) != -1;
        final boolean isMilestone = Build.MODEL.toLowerCase().indexOf( "milestone" ) != -1;
        return isMotorola && ( isDroid || isMilestone );
        }
    }
