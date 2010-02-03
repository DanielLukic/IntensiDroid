package net.intensicode.droid;

import net.intensicode.util.Log;

public final class AndroidLog extends Log
    {
    //#ifdef DEBUG

    protected final void doTrace()
        {
        final StringBuffer buffer = new StringBuffer( "TRACE" );
        prependCodeHint( buffer );
        android.util.Log.i( LOG_TAG, buffer.toString() );
        }

    protected final void doDebug( final StringBuffer aBufferWithMessage )
        {
        prependCodeHint( aBufferWithMessage );
        android.util.Log.d( LOG_TAG, aBufferWithMessage.toString(), null );
        }

    //#endif

    protected final void doError( final StringBuffer aBufferWithMessage, final Throwable aThrowable )
        {
        prependCodeHint( aBufferWithMessage );
        android.util.Log.e( LOG_TAG, aBufferWithMessage.toString(), aThrowable );
        }

    // Implementation

    private static void prependCodeHint( final StringBuffer aBuffer )
        {
        aBuffer.insert( 0, ":\n" );
        aBuffer.insert( 0, makeCodeHintString() );
        }

    private static String makeCodeHintString()
        {
        final RuntimeException notThrownException = new RuntimeException();
        final StackTraceElement[] stackTrace = notThrownException.getStackTrace();
        for ( int i = 0; i < stackTrace.length; i++ )
            {
            final StackTraceElement element = stackTrace[ i ];
            if ( element.getClassName().endsWith( "Log" ) ) continue;
            final StringBuffer buffer = new StringBuffer();
            buffer.append( element.getClassName() );
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
    }
