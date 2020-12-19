package com.voqse.nixieclock.theme;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.NeoThemeDrawer;
import com.voqse.nixieclock.theme.drawer.NewThemeDrawer;
import com.voqse.nixieclock.theme.drawer.OldThemeDrawer;
import com.voqse.nixieclock.theme.drawer.ThemeDrawer;
import com.voqse.nixieclock.theme.drawer.TronThemeDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая информация о теме виджета.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public enum Theme {

    NEW("new", R.string.theme_new, true, 972 * 484 * 4, true),
    NEW_PURE("new", R.string.theme_new_pure, false, 972 * 394 * 4, true),
    NEO("neo", R.string.theme_neo, true, 1032 * 580 * 4, false),
    NEO_PURE("neo", R.string.theme_neo_pure, false, 1032 * 480 * 4, false),
    OLD("old", R.string.theme_old, true, 1032 * 580 * 4, false),
    OLD_PURE("old", R.string.theme_old_pure, false, 1032 * 480 * 4, false),
    TRON("tron", R.string.theme_tron, true, 1032 * 580 * 4, false),
    TRON_PURE("tron", R.string.theme_tron_pure, false, 1032 * 480 * 4, false);

    private static final Map<Theme, ThemeDrawer> DRAWERS = new HashMap<Theme, ThemeDrawer>() {{
        put(NEW, new NewThemeDrawer());
        put(NEW_PURE, new NewThemeDrawer());
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
    public final boolean isItNew;

    Theme(String resources, int nameId, boolean useBasement, int x2BytesSize, boolean isItNew) {
        this.useBasement = useBasement;
        this.resources = resources;
        this.nameId = nameId;
        this.x2BytesSize = x2BytesSize;
        this.isItNew = isItNew;
    }

    public ThemeDrawer newThemeDrawer() {
        return DRAWERS.get(this);
    }
}
