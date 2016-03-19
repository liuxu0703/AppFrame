package lx.af.demo.activity.main;

import lx.af.demo.R;
import lx.af.demo.fragment.MainTab1;
import lx.af.demo.fragment.MainTab2;
import lx.af.demo.fragment.MainTab3;
import lx.af.demo.fragment.MainTab4;

public enum MainTab {

	TAB1(0, R.string.main_tab_1, R.string.main_tab_1_icon, MainTab1.class),
	TAB2(1, R.string.main_tab_2, R.string.main_tab_2_icon, MainTab2.class),
	TAB3(2, R.string.main_tab_3, R.string.main_tab_3_icon, MainTab3.class),
	TAB4(3, R.string.main_tab_4, R.string.main_tab_4_icon, MainTab4.class),
    ;

	private int idx;
	private int resName;
	private int resIcon;
	private Class<?> clz;

	MainTab(int idx, int resName, int resIcon, Class<?> clz) {
		this.idx = idx;
		this.resName = resName;
		this.resIcon = resIcon;
		this.clz = clz;
	}

	public int getIdx() {
		return idx;
	}

	public int getTitleRes() {
		return resName;
	}

	public int getIconRes() {
		return resIcon;
	}

	public Class<?> getClz() {
		return clz;
	}

    public static MainTab getTabByIndex(int idx) {
        for (MainTab tab : values()) {
            if (tab.getIdx() == idx) {
                return tab;
            }
        }
        return null;
    }

}
