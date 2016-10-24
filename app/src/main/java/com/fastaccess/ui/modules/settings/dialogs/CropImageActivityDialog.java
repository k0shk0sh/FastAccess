package com.fastaccess.ui.modules.settings.dialogs;

import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 20 Oct 2016, 8:29 PM
 */

public class CropImageActivityDialog extends BaseActivity {
//
//    @BindView(R.id.done) ForegroundImageView done;
//    @BindView(R.id.cutterImageView) CookieCutterImageView cutterImageView;
//    @State Uri uri;
//
//    @OnClick(R.id.done) void onDone() {
//        Bitmap bitmap = cutterImageView.getCroppedBitmap();
//        if (bitmap != null) {
//            Bitmap circularBitmap = ImageUtils.getCircularBitmap(bitmap);
//            String path = AppHelper.saveBitmap(circularBitmap);
//            if (path == null) {
//                Toast.makeText(this, R.string.write_sdcard_explanation, Toast.LENGTH_SHORT).show();
//                return;
//            }
//            PrefHelper.set(PrefConstant.CUSTOM_ICON, path);
//            EventBus.getDefault().post(new FloatingEventModel(true, PrefConstant.CUSTOM_ICON));
//            if (!bitmap.isRecycled() && !circularBitmap.isRecycled()) {
//                bitmap.recycle();
//                circularBitmap.recycle();
//            }
//        }
//        finish();
//    }

    @Override protected int layout() {
        return R.layout.crop_image_layout;
    }

    @NonNull @Override protected BasePresenter getPresenter() {
        return null;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return true;
    }

//    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null) {
//            uri = getIntent().getExtras().getParcelable("uri");
//        }
//        if (uri == null) {
//            finish();
//            return;
//        }
//        Bitmap bitmap = AppHelper.getBitmapFromUri(uri, this);
//        if (bitmap != null) {
//            cutterImageView.getParams().setShape(CookieCutterShape.CIRCLE);
//            cutterImageView.invalidate();
//            cutterImageView.setImageBitmap(bitmap);
//        } else {
//            finish();
//        }
//    }
}
