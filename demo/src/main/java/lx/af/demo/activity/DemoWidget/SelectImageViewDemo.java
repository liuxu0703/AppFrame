package lx.af.demo.activity.DemoWidget;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import lx.af.demo.R;
import lx.af.demo.base.ActionBar;
import lx.af.demo.base.BaseActivity;
import lx.af.demo.view.Rating5StarLayout;
import lx.af.widget.SelectImageWidget;

/**
 * author: lx
 * date: 16-3-19
 */
public class SelectImageViewDemo extends BaseActivity implements
        View.OnClickListener,
        ActionBar.Default {

    @InjectView(R.id.activity_select_rating_1)
    Rating5StarLayout mRating1;
    @InjectView(R.id.activity_select_rating_2)
    Rating5StarLayout mRating2;
    @InjectView(R.id.activity_select_rating_3)
    Rating5StarLayout mRating3;
    @InjectView(R.id.activity_select_rating_4)
    Rating5StarLayout mRating4;
    @InjectView(R.id.activity_select_editor)
    EditText mEditor;
    @InjectView(R.id.activity_select_image_view)
    SelectImageWidget mSelectImageWidget;
    @InjectView(R.id.activity_select_image_text)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image_widget);
        ButterKnife.inject(this);

        mRating1.setTitle("Rating 1").setRating(2);
        mRating2.setTitle("Rating 2").setRating(3);
        mRating3.setTitle("Rating 3").setRating(4);
        mRating4.setTitle("Rating 4").setRating(3);

        findViewById(R.id.activity_select_image_btn_get).setOnClickListener(this);
        mTextView.setOnClickListener(this);

        mSelectImageWidget.setImageListChangeListener(new SelectImageWidget.ImageListChangeListener() {
            @Override
            public void onImageListChanged(ArrayList<String> imageList) {
                StringBuilder sb = new StringBuilder();
                sb.append("select image count: ").append(imageList.size())
                        .append("/").append(mSelectImageWidget.getMaxImageCount());
                toastShort(sb.toString());
            }
        });
    }

    private void refreshTextView() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> imageList = mSelectImageWidget.getImagePathList();
        sb.append("rating 1:     ").append(mRating1.getRating()).append("\n");
        sb.append("rating 2:     ").append(mRating2.getRating()).append("\n");
        sb.append("rating 3:     ").append(mRating3.getRating()).append("\n");
        sb.append("rating 4:     ").append(mRating4.getRating()).append("\n");
        sb.append("\n");

        sb.append("content: ").append("\n").append(mEditor.getText().toString()).append("\n\n");

        sb.append("selected result, image count: ").append(imageList.size())
                .append("/").append(mSelectImageWidget.getMaxImageCount()).append("\n");
        sb.append("selected result, image path:").append("\n");
        if (imageList.size() > 0) {
            for (String path : imageList) {
                sb.append(path).append("\n");
            }
        }

        mTextView.setText(sb.toString());
        mTextView.setVisibility(View.VISIBLE);
    }

    @Override
    @OnClick({
            R.id.activity_select_image_btn_get,
            R.id.activity_select_image_text,
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_select_image_btn_get: {
                refreshTextView();
                break;
            }
            case R.id.activity_select_image_text: {
                mTextView.setVisibility(View.GONE);
                break;
            }
        }
    }
}
