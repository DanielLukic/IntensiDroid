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

    public AndroidResourcesManager( final AssetManager aAssetManager, final String aSubFolderOrNull )
        {
        myAssetManager = aAssetManager;
        mySubFolderOrNull = aSubFolderOrNull;
        }

    public void switchSubFolder( final String aSubFolderOrNull )
        {
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

    public final int maxImageResourceSize()
        {
        return MAX_IMAGE_RESOURCE_SIZE;
        }

    public final ImageResource createImageResource( final int aWidth, final int aHeight )
        {
        return AndroidImageResource.createFrom( aWidth, aHeight );
        }

    public final ImageResource loadImageResource( final String aResourcePath )
        {
        final InputStream resourceStream = openStream( aResourcePath );
        if ( resourceStream == null ) throw new NullPointerException( aResourcePath );
        return AndroidImageResource.createFrom( aResourcePath, resourceStream );
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


    private String mySubFolderOrNull;

    private final AssetManager myAssetManager;
    }
