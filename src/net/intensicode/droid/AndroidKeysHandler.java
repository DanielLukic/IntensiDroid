package net.intensicode.droid;

import android.view.*;
import net.intensicode.core.KeysHandler;

public final class AndroidKeysHandler extends KeysHandler implements View.OnKeyListener
    {
    public AndroidKeysHandler()
        {
        super( new AndroidKeysConfiguration() );
        }

    // From OnKeyListener

    public final boolean onKey( final View aView, final int aKeyCode, final KeyEvent aKeyEvent )
        {
        final int keyID = mapKeyCodeToKeyId( aKeyCode );
        if ( keyID == INVALID ) return false;

        final int action = aKeyEvent.getAction();
        if ( action == KeyEvent.ACTION_DOWN ) queueSetEvent( keyID );
        if ( action == KeyEvent.ACTION_UP ) queueClearEvent( keyID );
        if ( action != KeyEvent.ACTION_MULTIPLE ) return true;
        if ( aKeyEvent.getKeyCode() == KeyEvent.KEYCODE_UNKNOWN ) return true;

        final int count = aKeyEvent.getRepeatCount();
        for ( int idx = 0; idx < count; idx++ )
            {
            queueSetEvent( keyID );
            queueClearEvent( keyID );
            }

        return true;
        }

    // Implementation

    private int mapKeyCodeToKeyId( final int aKeyCode )
        {
        lastCode = aKeyCode;
        lastAction = 0;

        if ( aKeyCode == leftCode ) return LEFT;
        if ( aKeyCode == rightCode ) return RIGHT;
        if ( aKeyCode == upCode ) return UP;
        if ( aKeyCode == downCode ) return DOWN;
        if ( aKeyCode == fireCode ) return STICK_DOWN;
        if ( aKeyCode == fireCodeA ) return FIRE1;
        if ( aKeyCode == fireCodeB ) return FIRE2;
        if ( aKeyCode == fireCodeC ) return FIRE3;
        if ( aKeyCode == fireCodeD ) return FIRE4;
        if ( aKeyCode == starCode ) return FIRE1;
        if ( aKeyCode == poundCode ) return FIRE2;
        if ( aKeyCode == softLeftCode ) return LEFT_SOFT;
        if ( aKeyCode == softRightCode ) return RIGHT_SOFT;
        if ( aKeyCode == softPauseCode ) return PAUSE_KEY;

        //#if DEBUG
        //# net.intensicode.util.Log.debug( "Unhandled keycode: {}", aKeyCode );
        //# lastInvalidCode = aKeyCode;
        //#endif

        return INVALID;
        }
    }
