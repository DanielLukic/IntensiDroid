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
        try
            {
            final Class hooksClass = Class.forName( "${android_platform_hooks.classname}" );
            return (AndroidPlatformHooks) hooksClass.newInstance();
            }
        catch ( final ClassNotFoundException e )
            {
            // Ignore this..
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

    public void onCreate( final GameSystem aGameSystem )
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

    public void trackState( final String aCategory, final String aAction, final String aLabel )
        {
        }

    public void trackPageView( final String aPageId )
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

    private static AndroidPlatformHooks theInstance;
    }
