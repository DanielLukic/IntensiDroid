package net.intensicode;

import android.app.Application;
import android.content.Context;
import net.intensicode.util.Log;

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
        //#if FEINT
        final String name = "${openfeint.name}";
        final String key = "${openfeint.key}";
        final String secret = "${openfeint.secret}";
        final String id = "${openfeint.id}";
        final com.openfeint.api.OpenFeintSettings settings = new com.openfeint.api.OpenFeintSettings( name, key, secret, id );
        final com.openfeint.api.OpenFeintDelegate delegate = new com.openfeint.api.OpenFeintDelegate()
        {
        public void userLoggedIn( final com.openfeint.api.resource.CurrentUser user )
            {
            Log.info( "userLoggedIn" );
            }

        public void userLoggedOut( final com.openfeint.api.resource.User user )
            {
            Log.info( "userLoggedOut" );
            }

        public void onDashboardAppear()
            {
            Log.info( "onDashboardAppear" );
            }

        public void onDashboardDisappear()
            {
            Log.info( "onDashboardDisappear" );
            }

        public boolean showCustomApprovalFlow( final Context ctx )
            {
            Log.info( "showCustomApprovalFlow" );
            return super.showCustomApprovalFlow( ctx );
            }
        };
        com.openfeint.api.OpenFeint.initialize( this, settings, delegate );
        //#endif
        super.onCreate();
        }
    }
