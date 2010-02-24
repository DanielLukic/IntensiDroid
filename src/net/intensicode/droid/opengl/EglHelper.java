package net.intensicode.droid.opengl;

import android.view.SurfaceHolder;
import net.intensicode.util.Log;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL;

final class EglHelper
    {
    public static final int[] CHOOSE_FIRST_AVAILABLE = null;

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

        final EGLConfig[] configurations = new EGLConfig[16];
        final int[] numberOfConfigurations = new int[1];
        myEgl.eglGetConfigs( myDisplay, configurations, configurations.length, numberOfConfigurations );

        //#if DEBUG
        Log.debug( "EGL configurations found: {}", numberOfConfigurations[ 0 ] );
        for ( int idx = 0; idx < numberOfConfigurations[ 0 ]; idx++ )
            {
            Log.debug( "EGL available configuration:" );
            dumpConfiguration( configurations[ idx ] );
            }
        //#endif

        if ( aConfigurationSpec == CHOOSE_FIRST_AVAILABLE )
            {
            myConfiguration = configurations[0];
            }
        else
            {
            final EGLConfig[] configs = new EGLConfig[1];
            final int[] num_config = new int[1];
            myEgl.eglChooseConfig( myDisplay, aConfigurationSpec, configs, 1, num_config );
            myConfiguration = configs[ 0 ];
            }

        Log.debug( "EGL choosen configuration:" );
        dumpConfiguration( myConfiguration );

        myContext = myEgl.eglCreateContext( myDisplay, myConfiguration, EGL10.EGL_NO_CONTEXT, null );
        }

    private void dumpConfiguration( final EGLConfig aConfiguration )
        {
        dumpAttribute( aConfiguration, EGL10.EGL_BUFFER_SIZE, "EGL_BUFFER_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_RED_SIZE, "EGL_RED_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_GREEN_SIZE, "EGL_GREEN_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_BLUE_SIZE, "EGL_BLUE_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_ALPHA_SIZE, "EGL_ALPHA_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_STENCIL_SIZE, "EGL_STENCIL_SIZE" );

        dumpAttribute( aConfiguration, EGL10.EGL_CONFIG_CAVEAT, "EGL_CONFIG_CAVEAT" );
        dumpAttribute( aConfiguration, EGL10.EGL_DEPTH_SIZE, "EGL_DEPTH_SIZE" );
        dumpAttribute( aConfiguration, EGL10.EGL_NATIVE_RENDERABLE, "EGL_NATIVE_RENDERABLE" );
        }

    private void dumpAttribute( final EGLConfig aConfiguration, final int aId, final String aName )
        {
        //#if DEBUG
        final int[] value = new int[1];
        myEgl.eglGetConfigAttrib( myDisplay, aConfiguration, aId, value );
        Log.debug( "{}: {}", aName, value[0] );
        //#endif
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
