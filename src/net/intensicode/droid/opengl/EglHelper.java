package net.intensicode.droid.opengl;

import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL;

final class EglHelper
    {
    public static final int CONTEXT_LOST = 0;

    public static final int CONTEXT_OK = 1;

    final int[] version = new int[2];


    final boolean isStarted()
        {
        return myContext != null;
        }

    final void start( final int[] aConfigurationSpec )
        {
        if ( isStarted() ) finish();

        myEgl = (EGL10) EGLContext.getEGL();

        myDisplay = myEgl.eglGetDisplay( EGL10.EGL_DEFAULT_DISPLAY );
        myEgl.eglInitialize( myDisplay, version );

        final EGLConfig[] configs = new EGLConfig[1];
        final int[] num_config = new int[1];
        myEgl.eglChooseConfig( myDisplay, aConfigurationSpec, configs, 1, num_config );
        myConfiguration = configs[ 0 ];
        myContext = myEgl.eglCreateContext( myDisplay, myConfiguration, EGL10.EGL_NO_CONTEXT, null );
        }

    final GL createOrUpdateSurface( final SurfaceHolder aSurfaceHolder )
        {
        if ( mySurface != null ) unbindAndDestroyCurrentSurface();

        mySurface = myEgl.eglCreateWindowSurface( myDisplay, myConfiguration, aSurfaceHolder, null );
        myEgl.eglMakeCurrent( myDisplay, mySurface, mySurface, myContext );

        return myContext.getGL();
        }

    final int swapAndReturnContextState()
        {
        myEgl.eglSwapBuffers( myDisplay, mySurface );

        final boolean contextLost = myEgl.eglGetError() == EGL11.EGL_CONTEXT_LOST;
        return contextLost ? CONTEXT_LOST : CONTEXT_OK;
        }

    public void finish()
        {
        if ( mySurface != null ) unbindAndDestroyCurrentSurface();
        if ( myContext != null ) destroyContext();
        if ( myDisplay != null ) destroyDisplay();
        }

    // Implementation

    private void unbindAndDestroyCurrentSurface()
        {
        myEgl.eglMakeCurrent( myDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT );
        myEgl.eglDestroySurface( myDisplay, mySurface );
        mySurface = null;
        }

    private void destroyContext()
        {
        myEgl.eglDestroyContext( myDisplay, myContext );
        myContext = null;
        }

    private void destroyDisplay()
        {
        myEgl.eglTerminate( myDisplay );
        myDisplay = null;
        }


    private EGL10 myEgl;

    private EGLSurface mySurface;

    private EGLContext myContext;

    private EGLDisplay myDisplay;

    private EGLConfig myConfiguration;
    }
