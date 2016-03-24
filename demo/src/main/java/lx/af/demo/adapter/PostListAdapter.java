package lx.af.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import lx.af.adapter.AbsViewHolder;
import lx.af.adapter.AbsViewHolderAdapter;
import lx.af.demo.R;
import lx.af.demo.model.PostModel;
import lx.af.demo.view.PostItem.PostUserView;
import lx.af.utils.ActivityLauncher.ImageBrowserLauncher;
import lx.af.utils.TimeFormatUtils;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.widget.NineGrid.NineGridLayout;
import lx.af.widget.NineGrid.NineImageUILAdapter;

/**
 * author: lx
 * date: 16-3-22
 */
public class PostListAdapter extends AbsViewHolderAdapter<PostModel> {

    public PostListAdapter(Context context) {
        super(context);
    }

    @Override
    public View createItemView(Context context) {
        return View.inflate(context, R.layout.item_post_list, null);
    }

    @Override
    public AbsViewHolder<PostModel> createViewHolder(View itemView) {
        return new PostViewHolder(itemView);
    }


    private static class PostViewHolder extends AbsViewHolder<PostModel> {

        @ViewInject(id = R.id.item_post_user_view)
        private PostUserView mUserView;
        @ViewInject(id = R.id.item_post_content)
        private TextView mContentView;
        @ViewInject(id = R.id.item_post_image_grid)
        private NineGridLayout mImageGrid;
        @ViewInject(id = R.id.item_post_address)
        private TextView mAddressView;
        @ViewInject(id = R.id.item_post_time)
        private TextView mTimeView;

        private NineImageUILAdapter mImageAdapter;

        public PostViewHolder(View root) {
            super(root);
            mImageAdapter = new NineImageUILAdapter(mImageGrid);
            mImageAdapter.setOnItemClickListener(new NineImageUILAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(View view, NineImageUILAdapter adapter, int position) {
                    ImageBrowserLauncher.of(view.getContext()).tapExit(true)
                            .uris(adapter.getImageUris())
                            .currentUri(adapter.getData(position))
                            .currentView(view)
                            .start();
                }
            });
        }

        @Override
        public void setData(PostModel data) {
            mUserView.edit()
                    .avatar(data.user.avatarUri)
                    .name(data.user.name)
                    .gender(data.user.isMale).refresh();
            mContentView.setVisibility(TextUtils.isEmpty(data.content) ? View.GONE : View.VISIBLE);
            mContentView.setText(data.content);
            mImageAdapter.refreshForAdapterView(data.picList);
            mAddressView.setVisibility(TextUtils.isEmpty(data.address) ? View.GONE : View.VISIBLE);
            mAddressView.setText(data.address);
            mTimeView.setText(TimeFormatUtils.getDisplayTime(data.createTime));
        }
    }

}
