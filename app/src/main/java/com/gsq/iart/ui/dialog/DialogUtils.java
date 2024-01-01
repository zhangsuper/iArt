package com.gsq.iart.ui.dialog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.SizeUtils;
import com.gsq.iart.R;


public class DialogUtils {

    private static final String TAG = "DialogUtils";

    //功能激活的时候，读取车辆，是不需要核销的。只有开始刷写的时候才需要核销。
    public static boolean isReadCarInfo = false;

    public interface CallBack {
        /**
         * 对话框点击
         *
         * @param isLeft 是否是左边按钮
         */
        void dialogDidCallBack(AlertDialog dialog, boolean isLeft);
    }

    public interface CategoryCallBack {
        /**
         * 对话框点击
         *
         * @param isLeft 是否是左边按钮
         */
        void dialogDidCallBack(AlertDialog dialog, boolean isLeft, String text);
    }

    public interface OnContentCallBack {
        void dialogDidChangeContent(AlertDialog dialog, String name);
    }

    public interface OnSaveBitmapCallBack {
        void dialogDidSaveBitmap(AlertDialog dialog, Bitmap bitmap);
    }

    public interface ActionSheetCallBack {
        void dialogDidChooseAction(AlertDialog dialog, int index);
    }

    public interface OnModifySuccessCallBack {
        void dialogDidModifySuccess(boolean isSuccess);
    }

    public interface OnStartCode {
        void dialogDidStartCode(boolean syncInfo);
    }


    public static void showNormalDoubleButtonDialog(Activity activity, String title, String content, String right, String left, boolean isCanCancel, CallBack callBack) {
        showAlertDialog(activity, title, null, content, left, right, isCanCancel, callBack);
    }

    /**
     * 退出确认框
     *
     * @param callBack
     */
    public static void showLogoutDialog(CallBack callBack) {
//        Activity activity = ApplicationUtils.getInstance().getTopActivity();
//        showAlertDialog(activity, activity.getString(R.string.back_to_logout),
//                null,
//                activity.getString(R.string.sure_to_logout),
//                activity.getString(R.string.cancel),
//                activity.getString(R.string.sure),
//                callBack);
    }

    private static AlertDialog progressDialog = null;
    public static AlertDialog normalDialog = null;

    public static void showProgressDialog(Activity activity, CharSequence title, CharSequence content, boolean isCancelable) {
        showProgressDialog(activity, title, content, SizeUtils.dp2px(270f),
                0,
                R.drawable.bg_shape_rect_r8_white, isCancelable);
    }

    public static void showProgressDialog(Activity activity,
                                          CharSequence title,
                                          CharSequence content,
                                          int width,
                                          int height,
                                          int backgroundResId,
                                          boolean isCancelable) {
        dismissProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Translucent_NoTitle);
        builder.setView(R.layout.dialog_progress);
        progressDialog = builder.create();
        //显示
        progressDialog.show();
        progressDialog.setCancelable(isCancelable);
        if (0 == width) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (0 == height) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        progressDialog.getWindow().setLayout(width, height);
        if (0 < backgroundResId) {
            progressDialog.getWindow().setBackgroundDrawableResource(backgroundResId);
        }
        TextView tvTitle = progressDialog.findViewById(R.id.title);
        TextView tvContent = progressDialog.findViewById(R.id.content);
        tvTitle.setText(title);
        tvContent.setText(content);
    }

    public static void updateProgress(int progress) {
        if (progressDialog != null) {
            ProgressBar mProgress = progressDialog.findViewById(R.id.pb_loading);
            mProgress.setProgress(progress);
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static AlertDialog showAlertDialog(
            final Activity activity,
            CharSequence title,
            CharSequence detail,
            CharSequence content,
            CharSequence left,
            CharSequence right,
            boolean isCanCancel,
            final CallBack callBack) {
        return showAlertDialog(activity, title, detail, content, left, right, isCanCancel, callBack,
                SizeUtils.dp2px(270f),
                0,
                R.drawable.bg_shape_rect_r8_white);
    }


    /**
     * 显示对话框，带title， detail， content，左右按钮
     *
     * @param activity
     * @param title
     * @param detail
     * @param content
     * @param left
     * @param right
     * @param callBack
     */
    public static AlertDialog showAlertDialog(
            final Activity activity,
            CharSequence title,
            CharSequence detail,
            CharSequence content,
            CharSequence left,
            CharSequence right,
            boolean isCanCancel,
            final CallBack callBack,
            int width, int height,
            int backgroundResId) {
        if (activity == null || !(activity instanceof Activity) || activity.isFinishing()) {
            return null;
        }

        AlertDialog.Builder alterDialog = new AlertDialog.Builder(activity, R.style.Translucent_NoTitle);
        alterDialog.setView(R.layout.dialog_custom);//加载进去
        normalDialog = alterDialog.create();
        normalDialog.setCancelable(isCanCancel);
        //显示
        if (!activity.isFinishing() && !normalDialog.isShowing()) {
            normalDialog.show();
        }
        if (0 == width) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (0 == height) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        normalDialog.getWindow().setLayout(width, height);
        if (backgroundResId > 0) {
            normalDialog.getWindow().setBackgroundDrawableResource(backgroundResId);
        }
        //自定义的东西
        normalDialog.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalDialog.dismiss();
                if (callBack != null) {
                    callBack.dialogDidCallBack(normalDialog, true);
                }
                normalDialog = null;
            }
        });
        normalDialog.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalDialog.dismiss();
                if (callBack != null) {
                    callBack.dialogDidCallBack(normalDialog, false);

                }
                normalDialog = null;
            }
        });

        TextView titleView = normalDialog.findViewById(R.id.title);
        TextView detailView = normalDialog.findViewById(R.id.detail);
        TextView contentView = normalDialog.findViewById(R.id.content);
        TextView leftView = normalDialog.findViewById(R.id.left);
        TextView rightView = normalDialog.findViewById(R.id.right);
        View emptyView = normalDialog.findViewById(R.id.empty_view);
        View dividerHorizontal = normalDialog.findViewById(R.id.divider_horizontal);
        View divider = normalDialog.findViewById(R.id.divider);

        leftView.setText(left);
        rightView.setText(right);
        titleView.setText(title);
        detailView.setText(detail);
        contentView.setText(content);
        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.GONE);
        }
        if (title.toString().trim().isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(detail)) {
            detailView.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(content)) {
            contentView.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(left)) {
            leftView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            leftView.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(right)) {
            rightView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            rightView.setVisibility(View.VISIBLE);
        }
        return normalDialog;
    }


}