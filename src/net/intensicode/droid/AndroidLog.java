package net.intensicode.droid;

import net.intensicode.util.Log;

public final class AndroidLog extends Log
    {
    public static final void activate()
        {
        if ( Log.theLog == null || !( Log.theLog instanceof AndroidLog ) ) Log.theLog = new AndroidLog();
        }

    //#if TRACE

    protected final void doTrace()
        {
        final StringBuffer buffer = new StringBuffer( "TRACE" );
        addCodeHint( buffer );
        android.util.Log.i( LOG_TAG, buffer.toString() );
        }

    //#endif

    //#if INFO

    protected final void doInfo( final StringBuffer aBufferWithMessage )
        {
        addCodeHint( aBufferWithMessage );
        android.util.Log.d( LOG_TAG, aBufferWithMessage.toString(), null );
        }

    //#endif

    //#ifdef DEBUG

    protected final void doDebug( final StringBuffer aBufferWithMessage )
        {
        addCodeHint( aBufferWithMessage );
        android.util.Log.d( LOG_TAG, aBufferWithMessage.toString(), null );
        }

    //#endif

    protected final void doError( final StringBuffer aBufferWithMessage, final Throwable aThrowable )
        {
        addCodeHint( aBufferWithMessage );
        android.util.Log.e( LOG_TAG, aBufferWithMessage.toString(), aThrowable );
        }

    // Implementation

    private static void addCodeHint( final StringBuffer aBuffer )
        {
        while ( aBuffer.length() < FORMATTED_MESSAGE_MAX_LENGTH ) aBuffer.append( ' ' );
        aBuffer.append( ' ' );
        aBuffer.append( makeCodeHintString() );
        }

    private static String makeCodeHintString()
        {
        final RuntimeException notThrownException = new RuntimeException();
        final StackTraceElement[] stackTrace = notThrownException.getStackTrace();
        for ( int i = 0; i < stackTrace.length; i++ )
            {
            final StackTraceElement element = stackTrace[ i ];
            final String classNameWithPackage = element.getClassName();
            if ( classNameWithPackage.endsWith( "Log" ) ) continue;
            final int lastDotPos = classNameWithPackage.lastIndexOf( '.' );
            final String classNameOnly = classNameWithPackage.substring( lastDotPos + 1, classNameWithPackage.length() );
            final StringBuffer buffer = new StringBuffer();
            buffer.append( classNameOnly );
            buffer.append( "#" );
            buffer.append( element.getMethodName() );
            buffer.append( "[" );
            buffer.append( element.getLineNumber() );
            buffer.append( "] " );
            return buffer.toString();
            }
        return EMPTY_STRING;
        }

    private static String EMPTY_STRING = "";

    private static final String LOG_TAG = "INTENSIGAME";

    private static final int FORMATTED_MESSAGE_MAX_LENGTH = 80;
    }
