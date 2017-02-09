package com.voqse.nixieclock.theme;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.NeoThemeDrawer;
import com.voqse.nixieclock.theme.drawer.OldThemeDrawer;
import com.voqse.nixieclock.theme.drawer.ThemeDrawer;
import com.voqse.nixieclock.theme.drawer.TronThemeDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public enum Theme {

    NEO("neo", R.string.theme_neo, true),
    NEO_PURE("neo", R.string.theme_neo_pure, false),
    OLD("old", R.string.theme_old, true),
    OLD_PURE("old", R.string.theme_old_pure, false),
    TRON("tron", R.string.theme_tron, true),
    TRON_PURE("tron", R.string.theme_tron_pure, false);

    private static final Map<Theme, ThemeDrawer> DRAWERS = new HashMap<Theme, ThemeDrawer>() {{
        put(NEO, new NeoThemeDrawer());
        put(NEO_PURE, new NeoThemeDrawer());
        put(OLD, new OldThemeDrawer());
        put(OLD_PURE, new OldThemeDrawer());
        put(TRON, new TronThemeDrawer());
        put(TRON_PURE, new TronThemeDrawer());
    }};
    public final boolean useBasement;
    public final String resources;
    public final int nameId;

    Theme(String resources, int nameId, boolean useBasement) {
        this.useBasement = useBasement;
        this.resources = resources;
        this.nameId = nameId;
    }

    public ThemeDrawer newThemeDrawer() {
        return DRAWERS.get(this);
    }
}
