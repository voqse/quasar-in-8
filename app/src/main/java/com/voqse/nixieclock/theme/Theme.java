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

    NEO("neo", R.string.theme_neo, true, 1032 * 580 * 4),
    NEO_PURE("neo", R.string.theme_neo_pure, false, 1032 * 480 * 4),
    OLD("old", R.string.theme_old, true, 1032 * 580 * 4),
    OLD_PURE("old", R.string.theme_old_pure, false, 1032 * 480 * 4),
    TRON("tron", R.string.theme_tron, true, 1032 * 580 * 4),
    TRON_PURE("tron", R.string.theme_tron_pure, false, 1032 * 480 * 4);

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
    public final int x2BytesSize;

    Theme(String resources, int nameId, boolean useBasement, int x2BytesSize) {
        this.useBasement = useBasement;
        this.resources = resources;
        this.nameId = nameId;
        this.x2BytesSize = x2BytesSize;
    }

    public ThemeDrawer newThemeDrawer() {
        return DRAWERS.get(this);
    }
}
