package net.intensicode.droid;

import net.intensicode.util.Timing;

public final class AndroidTiming
    {
    public static void start()
        {
        Timing.start( makeCodeHintString() );
        }

    public static void end()
        {
        Timing.end( makeCodeHintString() );
        }

    private static String makeCodeHintString()
        {
        final RuntimeException notThrownException = new RuntimeException();
        final StackTraceElement[] stackTrace = notThrownException.getStackTrace();
        for ( int i = 0; i < stackTrace.length; i++ )
            {
            final StackTraceElement element = stackTrace[ i ];
            final String classNameWithPackage = element.getClassName();
            if ( classNameWithPackage.endsWith( "AndroidTiming" ) ) continue;
            final int lastDotPos = classNameWithPackage.lastIndexOf( '.' );
            final String classNameOnly = classNameWithPackage.substring( lastDotPos + 1, classNameWithPackage.length() );
            final StringBuffer buffer = new StringBuffer();
            buffer.append( classNameOnly );
            buffer.append( "#" );
            buffer.append( element.getMethodName() );
            return buffer.toString();
            }
        throw new RuntimeException();
        }
    }
