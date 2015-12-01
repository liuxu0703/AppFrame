package lx.af.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lx.af.R;
import lx.af.base.BaseFragment;
import lx.af.dialog.LoadingDialog;
import lx.af.dialog.MessageDialog;
import lx.af.manager.GlobalThreadManager;
import lx.af.utils.PathUtils;
import lx.af.utils.log.Log;
import lx.af.view.MultiImageSelector.adapter.ImageGridAdapter;
import lx.af.view.MultiImageSelector.bean.Folder;
import lx.af.view.MultiImageSelector.bean.Image;
import lx.af.view.MultiImageSelector.utils.TimeUtils;
import lx.af.view.MultiImageSelector.view.FolderListView;
import lx.af.view.MultiImageSelector.view.ImageGridView;
import lx.af.view.MultiImageSelector.view.ImageItemView;

/**
 * 图片选择Fragment
 * Created by Nereo on 2015/4/7.
 *
 * import and modified by liuxu on 2015.04.22
 * https://github.com/lovetuzitong/MultiImageSelector
 */
public class MultiImageSelectorFragment extends BaseFragment {

    private static final String TAG = "MultiImageSelector";

    /** 最大图片选择次数，int类型 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，int类型 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，boolean类型 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 默认选择的数据集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;
    // delay time for time line text to hide
    private static final int TIME_LINE_HIDE_DELAY = 1500;

    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    // 图片Grid
    private ImageGridView mGridView;
    // folder list
    private FolderListView mFolderListView;
    // 时间线
    private TextView mTimeLineText;
    // 类别
    private TextView mCategoryText;
    // 预览按钮
    private Button mPreviewBtn;

    private ImageGridAdapter mImageAdapter;
    private Callback mCallback;
    private File mCameraFile;
    private Animation mAnimTimeLineHide;
    private LoadingDialog mLoadingDialog;

    private int mDesireImageCount;
    private boolean mIsShowCamera;
    private boolean mIsFolderGenerated = false;

    private Handler mUIHandler = new Handler();

    private Runnable mHideTimeLineRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAnimTimeLineHide == null) {
                mAnimTimeLineHide = new AlphaAnimation(0.7f, 0f);
                mAnimTimeLineHide.setDuration(300);
            }
            mTimeLineText.setVisibility(View.GONE);
            mTimeLineText.startAnimation(mAnimTimeLineHide);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "The Activity must implement MultiImageSelectorFragment.Callback interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mis_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);

        if (mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }

        mFolderListView = (FolderListView) view.findViewById(R.id.mis_fragment_folder_list);
        initFolderList();

        mTimeLineText = (TextView) view.findViewById(R.id.mis_fragment_time_line);
        hideTimeLineDelayed();

        mCategoryText = (TextView) view.findViewById(R.id.mis_fragment_btn_show_folder);
        mCategoryText.setText(R.string.mis_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFolderListView.switchShowHide();
            }
        });

        mPreviewBtn = (Button) view.findViewById(R.id.mis_fragment_btn_preview);
        if (resultList == null || resultList.size()<=0) {
            mPreviewBtn.setText(R.string.mis_preview);
            mPreviewBtn.setEnabled(false);
        }

        mGridView = (ImageGridView) view.findViewById(R.id.mis_fragment_img_grid_view);
        initImageGridView(mode);
    }

    private void initImageGridView(final int mode) {
        mImageAdapter = new ImageGridAdapter(mGridView, mIsShowCamera);
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mGridView.setSelector(R.drawable.bkg_clickable);
        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                if (state != SCROLL_STATE_IDLE) {
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
                hideTimeLineDelayed();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTimeLineText.getVisibility() == View.VISIBLE) {
                    int count = view.getAdapter().getCount();
                    Image image = null;
                    if (mIsShowCamera) {
                        int index = (firstVisibleItem + 1 == count) ?
                                view.getAdapter().getCount() - 1 :
                                firstVisibleItem + 1;
                        image = (Image) view.getAdapter().getItem(index);
                    } else {
                        if (count != 0) {
                            image = (Image) view.getAdapter().getItem(firstVisibleItem);
                        }
                    }
                    if (image != null) {
                        mTimeLineText.setText(TimeUtils.formatPhotoDate(image.path));
                    }
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mImageAdapter.isShowCamera() && i == 0) {
                    startCamera();
                } else {
                    Image image = (Image) adapterView.getAdapter().getItem(i);
                    ImageItemView itemView = (ImageItemView) view;
                    selectImageFromGrid(itemView, image, mode);
                }
            }
        });
    }

    private void initFolderList() {
        mFolderListView.hide();

        mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getActivity().getSupportLoaderManager().restartLoader(
                            LOADER_ALL, null, mLoaderCallback);
                    mCategoryText.setText(R.string.mis_folder_all);
                    if (mIsShowCamera) {
                        mImageAdapter.setShowCamera(true);
                    }
                } else {
                    Folder folder = (Folder) parent.getAdapter().getItem(position);
                    if (null != folder) {
                        Bundle args = new Bundle();
                        args.putString("path", folder.path);
                        getActivity().getSupportLoaderManager().restartLoader(
                                LOADER_CATEGORY, args, mLoaderCallback);
                        mCategoryText.setText(folder.name);
                    }
                    mImageAdapter.setShowCamera(false);
                }
                mFolderListView.setSelectIndex(position);
                mFolderListView.hide();

                mGridView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (mCameraFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mCameraFile);
                    }
                }
            }
        }
    }

    private void hideTimeLineDelayed() {
        mUIHandler.removeCallbacks(mHideTimeLineRunnable);
        mUIHandler.postDelayed(mHideTimeLineRunnable, TIME_LINE_HIDE_DELAY);
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            String imgPath = PathUtils.generateGallerySavePath();
            if (!PathUtils.ensurePathExistsWithErrorToast(imgPath, false)) {
                return;
            }
            mCameraFile = new File(imgPath);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getActivity(), R.string.mis_toast_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择图片操作
     */
    private void selectImageFromGrid(ImageItemView view, Image image, int mode) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if (resultList.size() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(getResources().getString(R.string.mis_preview) + "(" + resultList.size() + ")");
                    } else {
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.mis_preview);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == resultList.size()) {
                        Toast.makeText(getActivity(), R.string.mis_toast_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image.path);
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setText(getResources().getString(R.string.mis_preview) + "(" + resultList.size() + ")");
                    if (mCallback != null) {
                        mCallback.onImageSelected(image.path);
                    }
                }
                mImageAdapter.select(image);
                view.toggleCheck();
            } else if(mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    // by liuxu, use thumbnail when possible
    private String getImageThumbnail(long id) {
        if (getActivity() == null) {
            return null;
        }
        final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
        final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;
        String[] projection = {thumb_DATA, thumb_IMAGE_ID};
        String selection =
                thumb_IMAGE_ID + "=" + id + " AND " +
                MediaStore.Images.Thumbnails.KIND + "=" + MediaStore.Images.Thumbnails.MINI_KIND;
        String thumbPath = null;
        Cursor thumbCursor = null;

        try {
            thumbCursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, null, null);
            if (thumbCursor != null && thumbCursor.getCount() > 0) {
                thumbCursor.moveToFirst();
                int idx = thumbCursor.getColumnIndex(thumb_DATA);
                thumbPath = thumbCursor.getString(idx);
            }
        } finally {
            if (thumbCursor != null) {
                thumbCursor.close();
            }
        }

        return thumbPath;
    }

    private LoadingDialog getLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(getActivity(), R.string.dlg_loading_default_message);
            mLoadingDialog.setCancelable(true);
            mLoadingDialog.setLoadingTimeout(15 * 1000, new LoadingDialog.OnTimeoutListener() {
                @Override
                public void onTimeout() {
                    Log.w(TAG, "loading timeout");
                    new MessageDialog.Builder(getActivity())
                            .setMessage(R.string.mis_error_load_timeout)
                            .setCancelListener(null)
                            .setConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().finish();
                                }
                            })
                            .create().show();
                }
            });
        }
        return mLoadingDialog;
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID };

        private File mPathFile;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String path = args == null ? null : args.getString("path");
            mPathFile = path == null ? null : new File(path);

            if (id == LOADER_ALL) {
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null,
                        IMAGE_PROJECTION[2] + " DESC");
            } else if (id == LOADER_CATEGORY) {
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0]+ " like '%" + path + "%'", null,
                        IMAGE_PROJECTION[2] + " DESC");
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data == null || data.getCount() <= 0) {
                return;
            }

            final LoadingDialog loadingDialog = getLoadingDialog();
            loadingDialog.show();

            GlobalThreadManager.runInThreadPool(new Runnable() {
                @Override
                public void run() {
                    final List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        if (TextUtils.isEmpty(path)) {
                            continue;
                        }
                        if (mPathFile != null && !isSubFile(path)) {
                            // we are loading image files of a dir (not all image files),
                            // if path is not first level sub file of the given path, ignore.
                            continue;
                        }

                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        long id = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        Image image = new Image(path, name, dateTime, getImageThumbnail(id));
                        images.add(image);

                        if (!mIsFolderGenerated) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.cover = image;
                            folder.path = folderFile.getAbsolutePath();

                            if (!mResultFolder.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (!data.isClosed() && data.moveToNext());

                    mIsFolderGenerated = true;
                    GlobalThreadManager.runInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            mImageAdapter.setData(images);
                            if (resultList != null && resultList.size() > 0) {
                                mImageAdapter.setDefaultSelected(resultList);
                            }
                            mFolderListView.setData(mResultFolder);
                        }
                    });
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        private boolean isSubFile(String filePath) {
            File file = new File(filePath);
            return file.getParentFile().equals(mPathFile);
        }
    };

    /**
     * 回调接口
     */
    public interface Callback{
        public void onSingleImageSelected(String path);
        public void onImageSelected(String path);
        public void onImageUnselected(String path);
        public void onCameraShot(File imageFile);
    }
}
