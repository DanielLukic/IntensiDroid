package net.intensicode;

import net.intensicode.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.me.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Updater
    {
    public static void check( final String aBaseUrl, final int aVersionNumber, final UpdateCallback aCallback )
        {
        new Thread()
        {
        public void run()
            {
            checkBlocking( aBaseUrl, aVersionNumber, aCallback );
            }
        }.start();
        }

    public static void checkBlocking( final String aBaseUrl, final int aVersionNumber, final UpdateCallback aCallback )
        {
        try
            {
            final String updateInfo = get( aBaseUrl );
            Log.info( "update info received: {}", updateInfo );

            final JSONObject json = new JSONObject( updateInfo );
            final int updateVersion = json.getInt( "version" );
            if ( updateVersion <= aVersionNumber )
                {
                aCallback.noUpdateAvailable();
                }
            else
                {
                aCallback.onUpdateAvailable( new BasicUpdateContext( json ), updateVersion );
                }
            }
        catch ( Exception e )
            {
            aCallback.updateCheckFailed( e );
            }
        }

    public static String get( final String aUrl ) throws IOException
        {
        Log.info( "using update url {}", aUrl );
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpResponse response = client.execute( new HttpGet( aUrl ) );
        final StatusLine statusLine = response.getStatusLine();
        if ( statusLine.getStatusCode() == HTTP_OK )
            {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            response.getEntity().writeTo( bytes );
            return new String( bytes.toByteArray() );
            }
        throw new IOException( "update check failed: " + statusLine );
        }

    private Updater()
        {
        // nop
        }

    private static final int HTTP_OK = 200;
    }
