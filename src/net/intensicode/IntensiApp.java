package net.intensicode;

import android.app.Application;

//#if ACRA
@org.acra.annotation.ReportsCrashes( formKey = "${android.acra.form_key}" )
//#endif
public final class IntensiApp extends Application
    {
    //#if FEINT
    public static final net.intensicode.feint.OpenFeintFacade OPEN_FEINT_FACADE = new net.intensicode.feint.OpenFeintFacade();
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
