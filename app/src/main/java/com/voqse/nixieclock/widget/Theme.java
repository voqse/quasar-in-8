package com.voqse.nixieclock.widget;

import com.voqse.nixieclock.R;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public enum Theme {

    QUASAR_1(R.string.theme_quasar_1),
    QUASAR_2(R.string.theme_quasar_2),
    QUASAR_3(R.string.theme_quasar_3);

    public final int nameId;

    Theme(int nameId) {
        this.nameId = nameId;
    }
}
