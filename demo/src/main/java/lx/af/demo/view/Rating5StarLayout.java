package lx.af.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import lx.af.demo.R;

/**
 * author: lx
 * date: 16-3-21
 */
public class Rating5StarLayout extends LinearLayout {

    private TextView mTitleView;
    private TextView mScoreView;
    private RatingBar mRatingView;

    private DecimalFormat mDecimalFormat;

    public Rating5StarLayout(Context context) {
        super(context);
        initView(context);
    }

    public Rating5StarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.rating_5_star_layout, this);
        mTitleView = (TextView) findViewById(R.id.rating_layout_title);
        mScoreView = (TextView) findViewById(R.id.rating_layout_score);
        mRatingView = (RatingBar) findViewById(R.id.rating_layout_rating);
        mRatingView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (mDecimalFormat == null) {
                    mDecimalFormat = new DecimalFormat("0.0");
                }
                mScoreView.setText(mDecimalFormat.format((int) rating));
            }
        });
    }

    public Rating5StarLayout setDecimalFormat(DecimalFormat format) {
        mDecimalFormat = format;
        return this;
    }

    public Rating5StarLayout setTitle(String title) {
        mTitleView.setText(title);
        return this;
    }

    public Rating5StarLayout setRating(int rating) {
        mRatingView.setRating(rating);
        mScoreView.setText(mDecimalFormat.format(rating));
        return this;
    }

    public int getRating() {
        return (int) mRatingView.getRating();
    }

}
