//#condition FEINT

package net.intensicode.feint;

import android.content.Context;
import com.openfeint.api.*;
import com.openfeint.api.resource.*;
import com.openfeint.api.ui.Dashboard;
import net.intensicode.core.OnlineAPI;
import net.intensicode.util.Log;

public final class OpenFeintFacade extends OpenFeintDelegate implements OnlineAPI
    {
    public final void earlyInitialize( final Context aContext )
        {
        final OpenFeintSettings settings = createFeintSettingsFromBuildProperties();
        OpenFeint.initialize( aContext, settings, this );
        }

    private static OpenFeintSettings createFeintSettingsFromBuildProperties()
        {
        final String name = "${openfeint.name}";
        final String key = "${openfeint.key}";
        final String secret = "${openfeint.secret}";
        final String id = "${openfeint.id}";
        return new OpenFeintSettings( name, key, secret, id );
        }

    // From OnlineAPI

    public final boolean hasNetworking()
        {
        return OpenFeint.isNetworkConnected();
        }

    public final boolean isLoggedIn()
        {
        return OpenFeint.isUserLoggedIn();
        }

    public final String getUserName()
        {
        return OpenFeint.getCurrentUser().name;
        }

    public final void showDashboard()
        {
        if ( hasNetworking() && !isLoggedIn() ) OpenFeint.userApprovedFeint();
        else Dashboard.open();
        }

    public final void showLeaderboard()
        {
        if ( hasNetworking() && !isLoggedIn() ) OpenFeint.userApprovedFeint();
        else Dashboard.openLeaderboard( "${openfeint.leaderboard_id}" );
        }

    public final void submitScore( final int aScore, final int aLevelNumberStartingAt1 )
        {
        final String text = aScore + " (Level " + aLevelNumberStartingAt1 + ")";
        final Score score = new Score( aScore, text );
        score.submitTo( getLeaderboard(), null );
        }

    private Leaderboard getLeaderboard()
        {
        if ( myLeaderboard == null ) myLeaderboard = new Leaderboard( "${openfeint.leaderboard_id}" );
        return myLeaderboard;
        }

    // From OpenFeintDelegate

    public final void userLoggedIn( final CurrentUser user )
        {
        Log.info( "userLoggedIn" );
        }

    public final void userLoggedOut( final User user )
        {
        Log.info( "userLoggedOut" );
        }

    public final void onDashboardAppear()
        {
        Log.info( "onDashboardAppear" );
        }

    public final void onDashboardDisappear()
        {
        Log.info( "onDashboardDisappear" );
        }

    public final boolean showCustomApprovalFlow( final Context ctx )
        {
        Log.info( "showCustomApprovalFlow" );
        // We don't want to login right away *unless* the user has already logged in before.
        // Therefore we tell feint "true - we will handle approval". But we won't. For now.
        // The user can then later decide to 'Go Online' in the main menu or somewhere else.
        return true;
        }

    private Leaderboard myLeaderboard;
    }
