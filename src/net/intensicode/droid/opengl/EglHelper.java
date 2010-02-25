package net.intensicode.droid.opengl;

import android.view.SurfaceHolder;
import net.intensicode.util.DynamicArray;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL;

final class EglHelper
    {
    public static final int[] CHOOSE_FIRST_AVAILABLE = null;

    public static final int CONTEXT_LOST = 0;

    public static final int CONTEXT_OK = 1;

    public final DynamicArray availableConfigurations = new DynamicArray();

    public String choosenConfiguration;

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

        System.out.println( "EGL configurations found: " + numberOfConfigurations[ 0 ] );
        for ( int idx = 0; idx < numberOfConfigurations[ 0 ]; idx++ )
            {
            final String configurationString = makeConfigurationString( configurations[ idx ] );
            availableConfigurations.add( configurationString );
            System.out.println( "EGL configuration: " + configurationString );
            }

        if ( aConfigurationSpec == CHOOSE_FIRST_AVAILABLE )
            {
            myConfiguration = configurations[ 0 ];
            }
        else
            {
            final EGLConfig[] configs = new EGLConfig[1];
            final int[] num_config = new int[1];
            myEgl.eglChooseConfig( myDisplay, aConfigurationSpec, configs, 1, num_config );
            myConfiguration = configs[ 0 ];
            }

        choosenConfiguration = makeConfigurationString( myConfiguration );
        System.out.println( "EGL configuration choosen: " + choosenConfiguration );

        myContext = myEgl.eglCreateContext( myDisplay, myConfiguration, EGL10.EGL_NO_CONTEXT, null );
        }

    private String makeConfigurationString( final EGLConfig aConfiguration )
        {
        final StringBuffer buffer = new StringBuffer();
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_RED_SIZE, "R" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_GREEN_SIZE, "G" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_BLUE_SIZE, "B" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_ALPHA_SIZE, "A" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_DEPTH_SIZE, "D" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_STENCIL_SIZE, "S" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_CONFIG_CAVEAT, "CFG" );
        dumpAttribute( buffer, aConfiguration, EGL10.EGL_NATIVE_RENDERABLE, "NATIVE" );
        return buffer.toString();
        }

    private void dumpAttribute( final StringBuffer aBuffer, final EGLConfig aConfiguration, final int aId, final String aName )
        {
        final int[] value = new int[1];
        myEgl.eglGetConfigAttrib( myDisplay, aConfiguration, aId, value );
        aBuffer.append( aName );
        aBuffer.append( "=" );
        aBuffer.append( value[ 0 ] );
        aBuffer.append( " " );
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
