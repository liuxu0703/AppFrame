package lx.af.iconify.fonts;

import lx.af.iconify.Icon;
import lx.af.iconify.IconFontDescriptor;

public class SimpleLineIconsModule implements IconFontDescriptor {

    @Override
    public String ttfFileName() {
        return "iconify/android-iconify-simplelineicons.ttf";
    }

    @Override
    public Icon[] characters() {
        return SimpleLineIconsIcons.values();
    }
}
