package net.intensicode.droid;

import android.graphics.*;
import net.intensicode.core.FontResource;

public final class AndroidFontResource implements FontResource
    {
    public final Paint paint;

    public AndroidFontResource( final Paint aPaint )
        {
        paint = aPaint;
        myLineSpacing = aPaint.getFontMetrics( myFontMetrics );
        }

    // From FontResource

    public final int substringWidth( final String aString, final int aOffset, final int aLength )
        {
        paint.getTextBounds( aString, aOffset, aOffset + aLength, mySizeRect );
        return mySizeRect.width();
        }

    public final int charWidth( final char aCharCode )
        {
        paint.getTextBounds( new char[]{ aCharCode }, 0, 1, mySizeRect );
        return mySizeRect.width();
        }

    public final int getHeight()
        {
        return (int) ( myFontMetrics.bottom - myFontMetrics.top );
        }

    private final float myLineSpacing;

    private final Rect mySizeRect = new Rect();

    private final Paint.FontMetrics myFontMetrics = new Paint.FontMetrics();
    }
