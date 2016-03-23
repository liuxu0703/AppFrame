package lx.af.demo.view.PostItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lx.af.demo.R;
import lx.af.demo.utils.ImageLoaderHelper;

/**
 * author: lx
 * date: 16-3-11
 */
public class PostUserView extends RelativeLayout {

    private ImageView mAvatarView;
    private TextView mNameView;
    private ImageView mGenderView;

    private int userId;

    public PostUserView(Context context) {
        super(context);
        initView(context);
    }

    public PostUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.post_user_view, this);
        mAvatarView = (ImageView) findViewById(R.id.post_user_avatar);
        mNameView = (TextView) findViewById(R.id.post_user_name);
        mGenderView = (ImageView) findViewById(R.id.post_user_gender);
        setOnClickListener(mClickListener);
    }

    public void refreshView(String avatar, String name, boolean isMale) {
        ImageLoaderHelper.displayAvatar(mAvatarView, avatar);
        mNameView.setText(name);
        mGenderView.setImageResource(isMale ? R.drawable.ic_male : R.drawable.ic_female);
    }

    public void setData(int userId) {
        this.userId = userId;
    }

    public RefreshBuilder edit() {
        return new RefreshBuilder(this);
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (userId != 0) {
                // start user detail activity
            }
        }
    };

    public static class RefreshBuilder {

        private PostUserView mUserView;

        private String avatar = null;
        private String name = null;
        private boolean isMale = true;
        private int userId = 0;

        public RefreshBuilder(PostUserView view) {
            mUserView = view;
        }

        public void refresh() {
            mUserView.refreshView(avatar, name, isMale);
            mUserView.setData(userId);
        }

        public RefreshBuilder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public RefreshBuilder avatar(String key) {
            this.avatar = key;
            return this;
        }

        public RefreshBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RefreshBuilder gender(boolean isMale) {
            this.isMale = isMale;
            return this;
        }

    }

}
