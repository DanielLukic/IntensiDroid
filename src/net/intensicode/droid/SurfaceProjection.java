package net.intensicode.droid;

import android.view.SurfaceHolder;
import net.intensicode.util.Log;
import net.intensicode.util.Size;

public final class SurfaceProjection
    {
    public /*readonly*/ SurfaceHolder holder;

    public final /*readonly*/ Size target = new Size();

    public final /*readonly*/ Size screen = new Size();

    public /*readonly*/ float offsetX = 0;

    public /*readonly*/ float offsetY = 0;

    public /*readonly*/ float scaleX = 1f;

    public /*readonly*/ float scaleY = 1f;

    public final void setScreenSize( final int aWidth, final int aHeight )
        {
        if ( screen.width == aWidth && screen.height == aHeight ) return;
        Log.info( "setScreenSize {}x{}", aWidth, aHeight );
        screen.setTo( aWidth, aHeight );
        updateProjection();
        }

    public final void setTargetSize( final int aWidth, final int aHeight )
        {
        if ( target.width == aWidth && target.height == aHeight ) return;
        Log.info( "setTargetSize {}x{}", aWidth, aHeight );
        target.width = aWidth;
        target.height = aHeight;
        updateProjection();
        }

    private void updateProjection()
        {
        if ( target.width == 0 || target.height == 0 || screen.width == 0 || screen.height == 0 )
            {
            scaleX = scaleY = 1f;
            return;
            }

        Log.info( "Target screen size: {}x{}", target.width, target.height );
        Log.info( "Device screen size: {}x{}", screen.width, screen.height );

        final float hFactor = screen.width / (float) target.width;
        final float vFactor = screen.height / (float) target.height;
        final float factor = Math.min( hFactor, vFactor );

        final float targetWidth = target.width * factor;
        final float targetHeight = target.height * factor;

        final float xDelta = screen.width - targetWidth;
        final float yDelta = screen.height - targetHeight;

        offsetX = xDelta / factor / 2f;
        offsetY = yDelta / factor / 2f;

        scaleX = scaleY = factor;

        Log.info( "Offset: {},{}", offsetX, offsetY );
        Log.info( "Scale: {},{}", scaleX, scaleY );
        }
    }
