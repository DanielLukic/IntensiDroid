package net.intensicode;

import android.app.Application;

//#if ACRA
@org.acra.annotation.ReportsCrashes( formKey = "${android.acra.form_key}" )
//#endif
public final class IntensiApp extends Application
    {
    @Override
    public final void onCreate()
        {
        //#if ACRA
        org.acra.ACRA.init( this );
        //#endif
        super.onCreate();
        }
    }
