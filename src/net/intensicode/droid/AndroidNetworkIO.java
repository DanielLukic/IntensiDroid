package net.intensicode.droid;

import net.intensicode.core.*;

public final class AndroidNetworkIO implements NetworkIO
    {
    public final boolean isOnline()
        {
        return true;
        }

    public final void sendAndReceive( final String aURL, final byte[] aBody, final NetworkCallback aCallback )
        {
        }

    public final void process( final NetworkRequest aRequest, final NetworkCallback aNetworkCallback )
        {
        }
    }
