package net.intensicode;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import net.intensicode.core.GameSystem;
import net.intensicode.util.Log;

public class AndroidPlatformHooks implements PlatformHooks
    {
    public static AndroidPlatformHooks getInstance()
        {
        if ( theInstance == null ) theInstance = createInstance();
        return theInstance;
        }

    private static AndroidPlatformHooks createInstance()
        {
        final String classname = "${android_platform_hooks.classname}";
        try
            {
            final Class hooksClass = Class.forName( classname );
            final AndroidPlatformHooks instance = (AndroidPlatformHooks) hooksClass.newInstance();
            Log.info( "created AndroidPlatformHooks instance using {}", hooksClass );
            return instance;
            }
        catch ( final ClassNotFoundException e )
            {
            // Ignore this..
            Log.info( "failed creating {} instance", classname );
            }
        catch ( final Exception e )
            {
            Log.error( e );
            }
        return new AndroidPlatformHooks();
        }

    public void onCreate( final Application aApplication )
        {
        }

    public void onCreate( final Activity aActivity )
        {
        }

    public void onStart( final Activity aActivity )
        {
        }

    public void onCreate( final GameSystem aGameSystem )
        {
        myGameSystem = aGameSystem;
        }

    public void onStop( final Activity aActivity )
        {
        }

    public void onDestroy( final Activity aActivity )
        {
        }

    public void setContentView( final Activity aActivity, final View aView )
        {
        aActivity.setContentView( aView );
        }

    // From PlatformHooks

    public void checkForUpdate( final String aUpdateUrl, final int aVersionNumber, final UpdateCallback aCallback )
        {
        //#if UPDATE
        Updater.check( aUpdateUrl, aVersionNumber, aCallback );
        //#endif
        }

    public void trackState( final String aCategory, final String aAction, final String aLabel )
        {
        }

    public void trackPageView( final String aPageId )
        {
        }

    public void trackException( final String aErrorId, final String aMessage, final Throwable aOptionalThrowable )
        {
        }

    public void showBannerAd()
        {
        }

    public void hideBannerAd()
        {
        }

    public boolean hasBannerAds()
        {
        return false;
        }

    public int getBannerAdHeight()
        {
        return 0;
        }

    public void positionAdBanner( final int aVerticalPosition )
        {
        }

    public void triggerNewBannerAd()
        {
        }

    public boolean hasFullscreenAds()
        {
        return false;
        }

    public void preloadFullscreenAd()
        {
        }

    public void triggerNewFullscreenAd()
        {
        }

    private GameSystem myGameSystem;

    private static AndroidPlatformHooks theInstance;
    }
