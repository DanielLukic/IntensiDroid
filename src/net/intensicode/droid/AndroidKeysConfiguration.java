package net.intensicode.droid;

import android.view.KeyEvent;
import net.intensicode.core.KeysConfiguration;
import net.intensicode.util.Log;

public final class AndroidKeysConfiguration extends KeysConfiguration
    {
    public AndroidKeysConfiguration()
        {
        platformName = "ANDROID";
        softKeyLeft = KeyEvent.KEYCODE_SOFT_LEFT;
        softKeyRight = KeyEvent.KEYCODE_SOFT_RIGHT;
        softKeyDelete = KeyEvent.KEYCODE_DEL;
        softKeyBack = KeyEvent.KEYCODE_BACK;
        softKeyPencil = KeyEvent.KEYCODE_SYM;
        keyUp = KeyEvent.KEYCODE_DPAD_UP;
        keyDown = KeyEvent.KEYCODE_DPAD_DOWN;
        keyLeft = KeyEvent.KEYCODE_DPAD_LEFT;
        keyRight = KeyEvent.KEYCODE_DPAD_RIGHT;
        keyFire = KeyEvent.KEYCODE_DPAD_CENTER;
        keyGameA = KeyEvent.KEYCODE_SPACE;
        keyGameB = KeyEvent.KEYCODE_ENTER;
        keyGameC = KeyEvent.KEYCODE_COMMA;
        keyGameD = KeyEvent.KEYCODE_PERIOD;
        keyNum0 = KeyEvent.KEYCODE_0;
        keyNum1 = KeyEvent.KEYCODE_1;
        keyNum2 = KeyEvent.KEYCODE_2;
        keyNum3 = KeyEvent.KEYCODE_3;
        keyNum4 = KeyEvent.KEYCODE_4;
        keyNum5 = KeyEvent.KEYCODE_5;
        keyNum6 = KeyEvent.KEYCODE_6;
        keyNum7 = KeyEvent.KEYCODE_7;
        keyNum8 = KeyEvent.KEYCODE_8;
        keyNum9 = KeyEvent.KEYCODE_9;
        keyStar = KeyEvent.KEYCODE_STAR;
        keyPound = KeyEvent.KEYCODE_POUND;
        }
    }
