package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.utils.ViewInject.ViewInject;
import lx.af.widget.SelectImageWidget;

/**
 * author: lx
 * date: 16-3-19
 */
public class SelectImageViewDemo extends BaseActivity implements
        ActionBar.Default {

    @ViewInject(id = R.id.activity_select_image_view)
    private SelectImageWidget mSelectImageWidget;
    @ViewInject(id = R.id.activity_select_image_text)
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image_widget);

        findViewById(R.id.activity_select_image_btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTextView();
            }
        });

        mSelectImageWidget.setImageListChangeListener(new SelectImageWidget.ImageListChangeListener() {
            @Override
            public void onImageListChanged(ArrayList<String> imageList) {
                StringBuilder sb = new StringBuilder();
                sb.append("select changed, image count: ").append(imageList.size())
                        .append("/").append(mSelectImageWidget.getMaxImageCount()).append("\n");
                mTextView.setText(sb.toString());
            }
        });
    }

    private void refreshTextView() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> imageList = mSelectImageWidget.getImagePathList();
        sb.append("selected result, image count: ").append(imageList.size())
                .append("/").append(mSelectImageWidget.getMaxImageCount()).append("\n");
        sb.append("selected result, image path:").append("\n");
        if (imageList.size() > 0) {
            for (String path : imageList) {
                sb.append(path).append("\n");
            }
        }
        mTextView.setText(sb.toString());
    }
}
