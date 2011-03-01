package net.intensicode.droid;

import android.os.Build;
import net.intensicode.core.GameSystem;
import net.intensicode.droid.opengl.OpenglGameView;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.util.DynamicArray;
import net.intensicode.util.StringUtils;

public final class AndroidGameSystem extends GameSystem
    {
    protected void throwWrappedExceptionToTellCallingSystemAboutBrokenGameSystem( final Exception aException )
        {
        throw new RuntimeException( "failed showing error screen", aException );
        }

    protected final void fillInformationStrings()
        {
        myInformationStrings.add( "Android Device Info:" );
        myInformationStrings.add( Build.BRAND );
        myInformationStrings.add( Build.MODEL );
        myInformationStrings.add( Build.DEVICE );
        myInformationStrings.add( Build.DISPLAY );
        myInformationStrings.add( Build.PRODUCT );

        if ( screen instanceof OpenglGameView )
            {
            final OpenglGameView view = (OpenglGameView) screen;
            view.openglGraphics.addOpenglStrings( myInformationStrings );
            }

        if ( graphics instanceof OpenglGraphics )
            {
            final OpenglGraphics opengl = (OpenglGraphics) graphics;
            myInformationStrings.add( "OpenGL Vendor:" );
            myInformationStrings.add( opengl.vendor );
            myInformationStrings.add( "OpenGL Renderer:" );
            myInformationStrings.add( opengl.renderer );
            myInformationStrings.add( "OpenGL Version:" );
            myInformationStrings.add( opengl.version );

            myInformationStrings.add( "OpenGL Extensions:" );
            final DynamicArray extensions = StringUtils.splitString( opengl.extensions, " ", true );
            for ( int idx = 0; idx < extensions.size; idx++ )
                {
                myInformationStrings.add( extensions.get( idx ) );
                }
            }
        }
    }
