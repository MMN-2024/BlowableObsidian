package org.minemalia.blowable.support;

import org.minemalia.blowable.BlowablePlugin;

public abstract class MultiVersion implements VersionSupport {

    private static MultiVersion multiVersion;

    public static MultiVersion get() {
        if (multiVersion != null) {
            return multiVersion;
        } else {
            // Support for Minecraft 1.21.4 (version 121)
            if (BlowablePlugin.mc_version >= 19) {
                multiVersion = new mc_newer();
                return multiVersion;
            } else {
                multiVersion = new mc_legacy();
                return multiVersion;
            }
        }
    }
}