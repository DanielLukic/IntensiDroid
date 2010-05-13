package net.intensicode.droid;

import android.content.res.AssetManager;
import android.graphics.*;
import net.intensicode.core.*;
import net.intensicode.graphics.*;
import net.intensicode.util.Assert;

import java.io.*;

public final class AndroidResourcesManager extends ResourcesManager
    {
    public static final int MAX_IMAGE_RESOURCE_SIZE = 512;

    public AndroidResourcesManager( final AssetManager aAssetManager )
        {
        myAssetManager = aAssetManager;
        }

    // From ResourcesManager

    public final FontGenerator getSmallDefaultFont()
        {
        final Paint paint = new Paint();
        paint.setTypeface( Typeface.DEFAULT );
        paint.setTextAlign( Paint.Align.LEFT );
        //#if DEBUG
        Assert.isTrue( "default text size is usable", paint.getTextSize() > 0 );
        //#endif
        final AndroidFontResource fontResource = new AndroidFontResource( paint );
        return new SystemFontGenerator( fontResource );
        }

    public final int maxImageResourceSize()
        {
        return MAX_IMAGE_RESOURCE_SIZE;
        }

    public final ImageResource createImageResource( final int aWidth, final int aHeight )
        {
        return AndroidImageResource.createFrom( aWidth, aHeight );
        }

    protected final ImageResource loadImageResourceDo( final String aResourceId, final InputStream aInputStream )
        {
        return AndroidImageResource.createFrom( aResourceId, aInputStream );
        }

    protected final InputStream openStreamDo( final String aResourcePath )
        {
        try
            {
            return myAssetManager.open( aResourcePath, AssetManager.ACCESS_STREAMING );
            }
        catch ( final IOException e1 )
            {
            return null;
            }
        }


    private final AssetManager myAssetManager;
    }
