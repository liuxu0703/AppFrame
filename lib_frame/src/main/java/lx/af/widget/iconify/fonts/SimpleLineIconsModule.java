package lx.af.widget.iconify.fonts;

import lx.af.widget.iconify.Icon;
import lx.af.widget.iconify.IconFontDescriptor;

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
