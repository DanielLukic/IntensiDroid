package net.intensicode;

import android.app.Application;
import net.intensicode.feint.OpenFeintFacade;

//#if ACRA
@org.acra.annotation.ReportsCrashes( formKey = "${android.acra.form_key}" )
//#endif
public final class IntensiApp extends Application
    {
    //#if FEINT
    public static final OpenFeintFacade OPEN_FEINT_FACADE = new OpenFeintFacade();
    //#endif

    @Override
    public final void onCreate()
        {
        //#if ACRA
        org.acra.ACRA.init( this );
        //#endif
        //#if FEINT
        OPEN_FEINT_FACADE.earlyInitialize( this );
        //#endif
        super.onCreate();
        }
    }
