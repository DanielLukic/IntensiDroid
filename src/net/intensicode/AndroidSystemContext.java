package net.intensicode;

import android.app.Activity;
import android.os.Handler;
import net.intensicode.configuration.*;
import net.intensicode.core.DirectGraphics;
import net.intensicode.core.GameSystem;
import net.intensicode.droid.AndroidKeysHandler;
import net.intensicode.droid.opengl.OpenglGraphics;
import net.intensicode.droid.opengl.OpenglRenderer;
import net.intensicode.screens.ScreenBase;

final class AndroidSystemContext implements SystemContext
    {
    public AndroidSystemContext( final Activity aActivity, final IntensiGameContext aIntensiGameContext )
        {
        myIntensiGameContext = aIntensiGameContext;
        myActivity = aActivity;
        myHelper = aIntensiGameContext.helper();
        myGameSystem = aIntensiGameContext.system();
        myHandler = new Handler( aActivity.getMainLooper() );
        }

    // From SystemContext

    public final ScreenBase createMainScreen() throws Exception
        {
        return myIntensiGameContext.createMainScreen();
        }

    public String determineResourcesFolder( final int aWidth, final int aHeight, final String aScreenOrientationId )
        {
        return myHelper.determineResourcesFolder( aWidth, aHeight, aScreenOrientationId );
        }

    public final GameSystem system()
        {
        return myGameSystem;
        }

    public final void fillEmailData( final EmailData aEmailData )
        {
        }

    public final ConfigurationElementsTree getPlatformValues()
        {
        final ConfigurationElementsTree platform = new ConfigurationElementsTree( "Platform" );

        try
            {
            final ConfigurationElementsTree ui = platform.addSubTree( "UI" );
            ui.addLeaf( new CaptureBackKey( (AndroidKeysHandler) myGameSystem.keys ) );

            //#if PROFILING
            final ConfigurationElementsTree profiling = platform.addSubTree( "Profiling" );
            profiling.addLeaf( new StartProfiling() );
            profiling.addLeaf( new StopProfiling() );
            profiling.addLeaf( new DumpHprofData() );
            //#endif

            //#if !RELEASE

            final DirectGraphics graphics = myGameSystem.graphics;
            if ( graphics instanceof OpenglGraphics )
                {
                final OpenglGraphics openglGraphics = (OpenglGraphics) graphics;
                final OpenglRenderer openglRenderer = openglGraphics.renderer;

                final ConfigurationElementsTree opengl = platform.addSubTree( "OpenGL" );
                opengl.addLeaf( new DumpTextureAtlases( openglRenderer.getAtlasTextureManager() ) );
                }

            platform.addLeaf( new DumpMemory() );

            //#endif
            }
        catch ( final Exception e )
            {
            system().showError( "failed preparing platform values for configuration menu. ignored.", e );
            }

        return platform;
        }

    public final ConfigurationElementsTree getSystemValues()
        {
        return myGameSystem.getSystemValues();
        }

    public ConfigurationElementsTree getApplicationValues()
        {
        return myIntensiGameContext.getApplicationValues();
        }

    public final void loadConfigurableValues()
        {
        myHelper.loadConfiguration( getPlatformValues() );
        myHelper.loadConfiguration( getSystemValues() );
        myHelper.loadConfiguration( getApplicationValues() );
        }

    public final void saveConfigurableValues()
        {
        myHelper.saveConfiguration( getPlatformValues() );
        myHelper.saveConfiguration( getSystemValues() );
        myHelper.saveConfiguration( getApplicationValues() );
        }

    public void onFramesDropped()
        {
        myIntensiGameContext.onFramesDropped();
        }

    public void onInfoTriggered()
        {
        myIntensiGameContext.onInfoTriggered();
        }

    public void onDebugTriggered()
        {
        myHelper.toggleDebugScreen();
        }

    public void onCheatTriggered()
        {
        myHelper.toggleCheatScreen();
        }

    public void onPauseApplication()
        {
        myIntensiGameContext.onPauseApplication();
        }

    public void onDestroyApplication()
        {
        myIntensiGameContext.onDestroyApplication();
        }

    //#if ORIENTATION_DYNAMIC

    public void onOrientationChanged()
        {
        myIntensiGameContext.onOrientationChanged();
        }

    //#endif

    public final void triggerConfigurationMenu()
        {
        myHandler.post( new Runnable()
        {
        public final void run()
            {
            myActivity.openOptionsMenu();
            }
        } );
        }

    public void terminateApplication()
        {
        myActivity.finish();
        }

    private final Handler myHandler;

    private final Activity myActivity;

    private final GameSystem myGameSystem;

    private final IntensiGameHelper myHelper;

    private final IntensiGameContext myIntensiGameContext;
    }
