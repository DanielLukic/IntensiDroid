package net.intensicode;

import android.app.Activity;

public interface PlatformComponent
    {
    void initialize( Activity aActivity, SystemContext aSystemContext, PlatformContext aPlatformContext );
    }
