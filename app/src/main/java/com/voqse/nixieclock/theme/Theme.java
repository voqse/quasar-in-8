package com.voqse.nixieclock.theme;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.NeoThemeDrawer;
import com.voqse.nixieclock.theme.drawer.OldThemeDrawer;
import com.voqse.nixieclock.theme.drawer.ThemeDrawer;
import com.voqse.nixieclock.theme.drawer.TronThemeDrawer;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public enum Theme {

    NEO(R.string.theme_neo, false) {
        @Override
        public ThemeDrawer newThemeDrawer() {
            return new NeoThemeDrawer();
        }
    },
    OLD(R.string.theme_old, true) {
        @Override
        public ThemeDrawer newThemeDrawer() {
            return new OldThemeDrawer();
        }
    },
    TRON(R.string.theme_tron, true) {
        @Override
        public ThemeDrawer newThemeDrawer() {
            return new TronThemeDrawer();
        }
    };

    public final int nameId;
    public final boolean encryptedResources;

    Theme(int nameId, boolean encryptedResources) {
        this.nameId = nameId;
        this.encryptedResources = encryptedResources;
    }

    public abstract ThemeDrawer newThemeDrawer();

}
