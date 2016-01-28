package lx.af.widget.iconify.fonts;

import lx.af.widget.iconify.Icon;
import lx.af.widget.iconify.IconFontDescriptor;

public class MaterialModule implements IconFontDescriptor {

    @Override
    public String ttfFileName() {
        return "iconify/android-iconify-material.ttf";
    }

    @Override
    public Icon[] characters() {
        return MaterialIcons.values();
    }
}
