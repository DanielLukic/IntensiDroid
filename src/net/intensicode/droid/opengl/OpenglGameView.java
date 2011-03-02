package net.intensicode.droid.opengl;

import android.content.Context;
import android.view.SurfaceHolder;
import net.intensicode.droid.AndroidGameView;
import net.intensicode.util.Assert;


public final class OpenglGameView extends AndroidGameView
    {
    public OpenglGraphics openglGraphics;

    public OpenglGameView( final Context aContext )
        {
        super( aContext, SurfaceHolder.SURFACE_TYPE_GPU );
        }

    // Internal API (DirectScreen)

    public final void beginFrame() throws InterruptedException
        {
        openglGraphics.targetSize.setTo( myTargetSize );
        openglGraphics.screenSize.setTo( getWidth(), getHeight() );

        system.graphics.beginFrame();
        }

    public final void endFrame()
        {
        system.graphics.endFrame();
        }

    public final void initialize()
        {
        Assert.isTrue( "AndroidGameView initialized", isInitialized() );
        openglGraphics.targetOffset = myTargetOffset;
        openglGraphics.targetScale = myTargetScale;
        openglGraphics.surfaceHolder = mySurfaceHolder;
        openglGraphics.initializeTriggered = true;
        }

    public final void cleanup()
        {
        openglGraphics.cleanupTriggered = true;
        }
    }
