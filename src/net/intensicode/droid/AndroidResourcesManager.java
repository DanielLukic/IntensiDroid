package net.intensicode.droid;

import android.content.res.AssetManager;
import android.graphics.*;
import net.intensicode.core.*;
import net.intensicode.graphics.*;
import net.intensicode.util.*;

import java.io.*;

public final class AndroidResourcesManager extends ResourcesManager
    {
    public AndroidResourcesManager( final AssetManager aAssetManager, final String aSubFolderOrNull )
        {
        myAssetManager = aAssetManager;
        mySubFolderOrNull = aSubFolderOrNull;
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

    public final ImageResource createImageResource( final int aWidth, final int aHeight )
        {
        final Bitmap bitmap = Bitmap.createBitmap( aWidth, aHeight, Bitmap.Config.ARGB_8888 );
        return new AndroidImageResource( bitmap );
        }

    public final ImageResource loadImageResource( final String aResourcePath ) throws IOException
        {
        final InputStream resourceStream = openStream( aResourcePath );
        if ( resourceStream == null ) throw new NullPointerException( aResourcePath );
        final Bitmap bitmap = BitmapFactory.decodeStream( resourceStream );
        if ( resourceStream == null ) throw new NullPointerException( aResourcePath );
        return new AndroidImageResource( aResourcePath, bitmap );
        }

    public final InputStream openStream( final String aResourcePath )
        {
        try
            {
            final String assetPath = getAssetPath( aResourcePath );
            return myAssetManager.open( assetPath, AssetManager.ACCESS_STREAMING );
            }
        catch ( final IOException e )
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
        }

    // Implementation

    private String getAssetPath( final String aResourcePath )
        {
        if ( mySubFolderOrNull == null ) return aResourcePath;
        return new File( mySubFolderOrNull, aResourcePath ).getPath();
        }


    private final String mySubFolderOrNull;

    private final AssetManager myAssetManager;
    }
