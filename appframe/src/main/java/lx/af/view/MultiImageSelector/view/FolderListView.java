package lx.af.view.MultiImageSelector.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import lx.af.R;
import lx.af.view.MultiImageSelector.adapter.FolderAdapter;
import lx.af.view.MultiImageSelector.bean.Folder;

/**
 * Created by liuxu on 15-4-23.
 * dir list for multi image selector.
 */
public class FolderListView extends LinearLayout {

    private static final int ANIM_DURATION = 300;
    private static final int BKG_ALPHA = 80;  // xx%

    private ListView mListView;
    private View mPlaceHolderView;
    private FolderAdapter mFolderAdapter;
    private Animation mAnimListIn;
    private Animation mAnimListOut;
    private Animation mAnimFadeIn;
    private Animation mAnimFadeOut;

    private float mBkgAlphaFloat = (float) BKG_ALPHA / 100;
    private boolean mIsScrolling = false;

    public FolderListView(Context context) {
        super(context);
        initView();
    }

    public FolderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        Context context = getContext();
        inflate(context, R.layout.mis_folder_list, this);
        setOrientation(VERTICAL);
        setBackgroundColor(getBkgColor());
        initAnim();

        mListView = (ListView) findViewById(R.id.mis_folder_list_view);
        mPlaceHolderView = findViewById(R.id.mis_folder_list_placeholder);
        mPlaceHolderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        mFolderAdapter = new FolderAdapter(this);
        mListView.setAdapter(mFolderAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mIsScrolling = (scrollState != SCROLL_STATE_IDLE);
                if (scrollState == SCROLL_STATE_IDLE) {
                    mFolderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void setData(ArrayList<Folder> data) {
        mFolderAdapter.setData(data);
    }

    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    public void show() {
        int index = mFolderAdapter.getSelectIndex();
        index = index == 0 ? index : index - 1;
        mListView.setSelection(index);
        mListView.startAnimation(mAnimListIn);
        setVisibility(View.VISIBLE);
        startAnimation(mAnimFadeIn);
    }

    public void hide() {
        mListView.startAnimation(mAnimListOut);
    }

    public void switchShowHide() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    public boolean isScrolling() {
        return mIsScrolling;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setSelectIndex(int index) {
        mFolderAdapter.setSelectIndex(index);
    }

    private int getBkgColor() {
        String alphaHex = Integer.toHexString(BKG_ALPHA * 255 / 100);
        String colorStr = "#" + alphaHex + "000000";
        return Color.parseColor(colorStr);
    }

    private void initAnim() {
        mAnimListIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in);
        mAnimListIn.setDuration(ANIM_DURATION);
        mAnimListIn.setStartOffset(ANIM_DURATION);
        mAnimListIn.setFillAfter(true);

        mAnimListOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out);
        mAnimListOut.setDuration(ANIM_DURATION);
        mAnimListOut.setFillAfter(true);
        mAnimListOut.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                startAnimation(mAnimFadeOut);
            }
        });

        mAnimFadeIn = new AlphaAnimation(0f, mBkgAlphaFloat);
        mAnimFadeIn.setDuration(ANIM_DURATION);

        mAnimFadeOut = new AlphaAnimation(mBkgAlphaFloat, 0f);
        mAnimFadeOut.setDuration(ANIM_DURATION);
    }

    private static abstract class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
