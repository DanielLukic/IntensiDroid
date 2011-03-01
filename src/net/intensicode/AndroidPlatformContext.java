package net.intensicode;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import net.intensicode.core.*;
import net.intensicode.droid.AndroidUtilities;
import net.intensicode.droid.opengl.OpenglGameView;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.util.DynamicArray;
import net.intensicode.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

final class AndroidPlatformContext implements PlatformContext
    {
    public AndroidPlatformContext( final Activity aActivity, final GameSystem aGameSystem )
        {
        myActivity = aActivity;
        myGameSystem = aGameSystem;
        myHandler = new Handler( aActivity.getMainLooper() );
        myErrorDialogBuilder = new ErrorDialogBuilder( aActivity, myGameSystem );
        }

    // From PlatformContext

    public final long compatibleTimeInMillis()
        {
        return SystemClock.uptimeMillis();
        }

    public final void openWebBrowser( final String aURL )
        {
        myActivity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( aURL ) ) );
        }

    public final void sendEmail( final EmailData aEmailData )
        {
        final Intent emailIntent = new Intent( Intent.ACTION_SEND );
        emailIntent.setType( "text/plain" );
        emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{ aEmailData.to } );
        emailIntent.putExtra( Intent.EXTRA_SUBJECT, aEmailData.subject );
        emailIntent.putExtra( Intent.EXTRA_TEXT, aEmailData.text );
        myActivity.startActivity( Intent.createChooser( emailIntent, "Send mail" ) );
        }

    public final String screenOrientationId()
        {
        return AndroidUtilities.determineScreenOrientationId( myActivity );
        }

    public final String getPlatformSpecString()
        {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( Build.BRAND );
        buffer.append( " * " );
        buffer.append( Build.MODEL );
        buffer.append( " * " );
        buffer.append( Build.DEVICE );
        buffer.append( " * " );
        buffer.append( Build.DISPLAY );
        buffer.append( " * " );
        buffer.append( Build.PRODUCT );
        try
            {
            buffer.append( " * " );
            buffer.append( Build.VERSION.SDK );
            buffer.append( " * " );
            buffer.append( Build.VERSION.RELEASE );
            buffer.append( " * " );
            buffer.append( Build.VERSION.INCREMENTAL );
            }
        catch ( final Exception e )
            {
            Log.error( "failed adding version information. ignored.", e );
            }
        return buffer.toString();
        }

    public final String getGraphicsSpecString()
        {
        final DirectGraphics graphics = myGameSystem.graphics;
        if ( graphics instanceof OpenglGraphics )
            {
            final OpenglGraphics opengl = (OpenglGraphics) graphics;

            final StringBuffer buffer = new StringBuffer();
            buffer.append( opengl.vendor );
            buffer.append( " * " );
            buffer.append( opengl.renderer );
            buffer.append( " * " );
            buffer.append( opengl.version );

            try
                {
                final DynamicArray strings = new DynamicArray();
                final OpenglGameView view = (OpenglGameView) myGameSystem.screen;
                view.openglGraphics.addOpenglStrings( strings );

                for ( int idx = 0; idx < strings.size; idx++ )
                    {
                    buffer.append( "\n" );
                    buffer.append( strings );
                    }
                }
            catch ( final Throwable t )
                {
                Log.error( t );
                }

            return buffer.toString();
            }
        else
            {
            return "AndroidCanvasGraphics";
            }
        }

    public final String getExtendedExceptionData( final Throwable aException )
        {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter( stringWriter );
        writer.print( "MESSAGE: " );
        writer.println( String.valueOf( aException.getMessage() ) );
        writer.print( "STACK: " );
        aException.printStackTrace( writer );
        writer.println();
        if ( aException.getCause() != null )
            {
            writer.print( "CAUSE: " );
            writer.println( String.valueOf( aException.getCause() ) );
            }
        return stringWriter.toString();
        }

    public void showError( final String aMessage, final Throwable aOptionalThrowable )
        {
        Log.error( "system error: {}", aMessage, aOptionalThrowable );
        postDialog( aMessage, aOptionalThrowable, false );
        }

    public void showCriticalError( final String aMessage, final Throwable aOptionalThrowable )
        {
        Log.error( "critical system error: {}", aMessage, aOptionalThrowable );
        postDialog( aMessage, aOptionalThrowable, true );
        }

    private void postDialog( final String aMessage, final Throwable aOptionalThrowable, final boolean aCritical )
        {
        Log.info( "stopping game system while showing error dialog" );
        myGameSystem.stop();

        myHandler.post( new Runnable()
        {
        public final void run()
            {
            myErrorDialogBuilder.setTitle( "IntensiGame Error Report" );
            myErrorDialogBuilder.setMessage( aMessage );
            if ( aCritical )
                {
                myErrorDialogBuilder.setCritical( aCritical );
                }
            if ( aOptionalThrowable != null )
                {
                final String exceptionText = getExtendedExceptionData( aOptionalThrowable );
                myErrorDialogBuilder.setCause( exceptionText );
                }
            myErrorDialogBuilder.createDialog();
            }
        } );
        }

    public final void storePreferences( final String aPreferencesId, final String aPropertyKey, final boolean aValue )
        {
        final SharedPreferences preferences = myActivity.getSharedPreferences( aPreferencesId, Context.MODE_PRIVATE );
        preferences.edit().putBoolean( aPropertyKey, true ).commit();
        }

    public final void register( final String aComponentName, final String aClassName )
        {
        Log.info( "registering {} => {}", aComponentName, aClassName );
        myRegisteredComponents.put( aComponentName, aClassName );
        }

    public final Object component( final String aComponentName )
        {
        final Object classNameOrInstance = myRegisteredComponents.get( aComponentName );
        Log.info( "component request {} => {}", aComponentName, classNameOrInstance );

        if ( classNameOrInstance == null ) throw new IllegalArgumentException( "component not registered: " + aComponentName );

        if ( classNameOrInstance instanceof String )
            {
            try
                {
                final Object instance = Class.forName( (String) classNameOrInstance ).newInstance();
                if ( instance instanceof PlatformComponent )
                    {
                    final PlatformComponent component = (PlatformComponent) instance;
                    component.initialize( myActivity, myGameSystem.context, myGameSystem.platform );
                    }
                myRegisteredComponents.put( aComponentName, instance );
                }
            catch ( final Exception e )
                {
                Log.error( e );
                throw new ChainedException( e );
                }
            }

        return myRegisteredComponents.get( aComponentName );
        }

    public final PlatformHooks hooks()
        {
        return AndroidPlatformHooks.getInstance();
        }


    private final Handler myHandler;

    private final Activity myActivity;

    private final GameSystem myGameSystem;

    private final ErrorDialogBuilder myErrorDialogBuilder;

    private final HashMap myRegisteredComponents = new HashMap();
    }
