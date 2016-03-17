package lx.af.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lx.af.demo.R;
import lx.af.utils.ScreenUtils;
import lx.af.widget.FlowLayout.FlowLayout;
import lx.af.widget.FlowLayout.TagAdapter;
import lx.af.widget.FlowLayout.TagFlowLayout;

/**
 * author: lx
 * date: 16-3-17
 */
public class FLItemView extends LinearLayout {

    private ImageView mIconView;
    private ImageView mExpandView;
    private TextView mTitleView;
    private TagFlowLayout mTagLayout;

    private TextTagAdapter mAdapter;

    private List<String> mList;
    private boolean mIsExpand = false;

    public FLItemView(Context context) {
        super(context);
        initView(context);
    }

    public FLItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.item_flow_layout, this);
        setOrientation(HORIZONTAL);
        mIconView = (ImageView) findViewById(R.id.item_flow_icon);
        mExpandView = (ImageView) findViewById(R.id.item_flow_btn_expand);
        mTitleView = (TextView) findViewById(R.id.item_flow_title);
        mTagLayout = (TagFlowLayout) findViewById(R.id.item_flow_tag_layout);

        mExpandView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: change expand btn icon
                setExpandTags(!mIsExpand);
            }
        });
    }

    public void setOnTagClickListener(TagFlowLayout.OnTagClickListener l) {
        mTagLayout.setOnTagClickListener(l);
    }

    public void setHeadIcon(int resId) {
        mIconView.setImageResource(resId);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resId) {
        mTitleView.setText(resId);
    }

    public void setFlowTags(List<String> tags) {
        if (tags == null) {
            return;
        }
        mList = tags;
        List<String> list;
        if (!mIsExpand && mList.size() > 5) {
            list = mList.subList(0, 5);
        } else {
            list = mList;
        }
        mAdapter = new TextTagAdapter(getContext(), list);
        mTagLayout.setAdapter(mAdapter);
    }

    public void setFlowTags(String[] tags) {
        ArrayList<String> list = new ArrayList<>(tags.length);
        Collections.addAll(list, tags);
        setFlowTags(list);
    }

    public void setExpandTags(boolean expand) {
        if (mIsExpand == expand) {
            return;
        }
        mIsExpand = expand;
        mTagLayout.setMaxLine(expand ? -1 : 1);
        if (mList == null) {
            // do nothing
        } else if (mAdapter == null || mAdapter.getCount() < mList.size()) {
            setFlowTags(mList);
        } else {
            mAdapter.notifyDataChanged();
        }
    }


    private static class TextTagAdapter extends TagAdapter<String> {

        Context mContext;

        public TextTagAdapter(Context context, List<String> dataList) {
            super(dataList);
            mContext = context;
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            int marginV = ScreenUtils.dip2px(3);
            int marginH = ScreenUtils.dip2px(2);
            TextView tv = new TextView(mContext);
            tv.setBackgroundResource(R.drawable.flow_tag_bkg);
            tv.setTextColor(Color.parseColor("#ff8106"));
            tv.setTextSize(12);
            tv.setText(s);
            MarginLayoutParams params = new MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(marginH, marginV, marginH, marginV);
            tv.setLayoutParams(params);
            return tv;
        }
    }

}
