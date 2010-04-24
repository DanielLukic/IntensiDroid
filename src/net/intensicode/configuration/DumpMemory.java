package net.intensicode.configuration;

import android.os.Debug;
import net.intensicode.ConfigurableActionValue;
import net.intensicode.util.Log;

public final class DumpMemory implements ConfigurableActionValue
    {
    public DumpMemory()
        {
        }

    public final String getTitle()
        {
        return "Dump Memory Stats";
        }

    public final String getInfoText()
        {
        return "Dump memory statistics to the log.";
        }

    public final void trigger()
        {
        final Debug.MemoryInfo info = new Debug.MemoryInfo();
        Debug.getMemoryInfo( info );
        Log.info( "dalvikPrivateDirty {}", info.dalvikPrivateDirty );
        Log.info( "dalvikPss          {}", info.dalvikPss );
        Log.info( "dalvikSharedDirty  {}", info.dalvikSharedDirty );
        Log.info( "nativePrivateDirty {}", info.nativePrivateDirty );
        Log.info( "nativePss          {}", info.nativePss );
        Log.info( "nativeSharedDirty  {}", info.nativeSharedDirty );
        Log.info( "otherPrivateDirty  {}", info.otherPrivateDirty );
        Log.info( "otherPss           {}", info.otherPss );
        Log.info( "otherSharedDirty   {}", info.otherSharedDirty );
        Log.info( "native heap size   {}", Debug.getNativeHeapSize() );
        Log.info( "native heap free   {}", Debug.getNativeHeapFreeSize() );
        Log.info( "native heap alloc  {}", Debug.getNativeHeapAllocatedSize() );
        }
    }
