package lx.af.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.BindView;
import lx.af.demo.R;
import lx.af.demo.adapter.DemoActionAdapter;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.base.BaseFragment;
import lx.af.demo.consts.ActionModel;

/**
 * author: lx
 * date: 16-1-5
 */
public abstract class MainTabList extends BaseFragment {

    @BindView(R.id.fragment_tab_list_view)
    ListView mListView;

    DemoActionAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_list, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new DemoActionAdapter(getActivity(), getActionModelArray());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
    }

    protected abstract ActionModel[] getActionModelArray();

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ActionModel model = mAdapter.getItem(position);
            if (model.activity != null) {
                Intent intent = new Intent(getActivity(), model.activity);
                intent.putExtra(BaseActivity.EXTRA_ACTIVITY_TITLE, model.title);
                getActivity().startActivity(intent);
            } else {
                toastShort(model.title);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
