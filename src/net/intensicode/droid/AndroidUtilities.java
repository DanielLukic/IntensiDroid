package net.intensicode.droid;

import android.app.Activity;
import android.os.Build;
import android.view.Display;

public final class AndroidUtilities
    {
    public static final int ORIENTATION_PORTRAIT = 0;

    public static final int ORIENTATION_LANDSCAPE = 1;

    public static final String SUB_FOLDER_SQUARE = "s";

    public static final String SUB_FOLDER_PORTRAIT = "p";

    public static final String SUB_FOLDER_LANDSCAPE = "l";

    public static final String NO_SUB_FOLDER = null;


    public static void showDeviceSpecs()
        {
        System.out.println( "Board: " + android.os.Build.BOARD );
        System.out.println( "Brand: " + android.os.Build.BRAND );
        System.out.println( "Device: " + android.os.Build.DEVICE );
        System.out.println( "Display: " + android.os.Build.DISPLAY );
        System.out.println( "Model: " + android.os.Build.MODEL );
        System.out.println( "Product: " + android.os.Build.PRODUCT );
        System.out.println( "Tags: " + android.os.Build.TAGS );
        System.out.println( "Type: " + android.os.Build.TYPE );
        }

    public static String determineResourcesSubFolder( final Activity aActivity )
        {
        final Display display = aActivity.getWindowManager().getDefaultDisplay();
        final int orientation = display.getOrientation();
        if ( orientation == ORIENTATION_PORTRAIT && looksLikePortrait( aActivity ) ) return SUB_FOLDER_PORTRAIT;
        if ( orientation == ORIENTATION_LANDSCAPE && looksLikeLandscape( aActivity ) ) return SUB_FOLDER_LANDSCAPE;
        if ( looksLikePortrait( aActivity ) ) return SUB_FOLDER_PORTRAIT;
        if ( looksLikeLandscape( aActivity ) ) return SUB_FOLDER_LANDSCAPE;
        if ( looksLikeSquare( aActivity ) ) return SUB_FOLDER_SQUARE;
        return NO_SUB_FOLDER;
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
        final boolean isGeneric= Build.BRAND.toLowerCase().indexOf( "generic" ) != -1;
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
