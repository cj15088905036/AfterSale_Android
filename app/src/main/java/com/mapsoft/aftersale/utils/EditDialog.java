package com.mapsoft.aftersale.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapsoft.aftersale.R;

/**
 * 自定义的对话框
 * Builder模式
 * 1.默认内容体为编辑框,如果未设置内容体,确认键,取消键,则该子view分别隐藏
 * 2.可设置项:
 *             标题,编辑框文本,确认键文本及监听,取消键文本及监听,内容体的view或resID,
 *             dismiss监听,multichoice监听,可取消?,外部可触摸?,按钮背景,dialoge背景
 * 2017/11/13 by cx
 */
public class EditDialog extends Dialog {

    /**
     * 构造方法
     *
     * @param context
     * @param theme
     */
    public EditDialog(Context context, int theme) {
        super(context, theme);
    }

    public EditDialog(Context context) {
        super(context);
    }

    /**
     * dialoge的构造者
     */
    public static class Builder implements DialogInterface.OnMultiChoiceClickListener {

        private Context context;
        private String title;//标题
        private String message;//编辑框文本
        private String positiveButtonText;//确认键文本
        private String negativeButtonText;//取消键文本
        private View contentView;//内容体
        private EditText editText;

        private int resId;//自定义的内容体view的资源id
        private OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;
        private OnDismissListener dismissListener;

        private boolean cancellable = true;
        private boolean canTouchOutside = true;
        private OnMultiChoiceClickListener multiChoiceClickListener;

        private int btnResId;//button的背景资源id
        private int bckResId;//背景资源id

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置布局的资源id
         *
         * @param resId
         * @return
         */
        public Builder setResId(int resId) {
            this.resId = resId;
            return this;
        }
        /**
         * 设置对话框的背景id
         */
        public Builder setBckResId(int bckResId) {
            this.bckResId = bckResId;
            return this;
        }

        /**
         * 设置多选监听
         * @param multiChoiceClickListener
         * @return
         */
        public Builder setMultiChoiceClickListener(OnMultiChoiceClickListener multiChoiceClickListener) {
            this.multiChoiceClickListener = multiChoiceClickListener;
            return this;
        }

        /**
         * 设置内容字符串
         *
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置内容字符串的资源id
         *
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * 设置标题字符串的资源id
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * 设置标题字符串
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         *设置内容体的view
         *
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 设置确认键的文本字符串资源id和监听
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         *
         * 设置确认键的文本和监听
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * 设置取消键的文本字符串资源id和监听
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 设置取消键的文本和监听
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 获取输入框的内容
         * @return
         */
        public String getMessage() {
            return editText.getText().toString();
        }

        /**
         * 设置dialoge的隐藏监听
         * @param dismissListener
         * @return
         */
        public Builder setOnDismissListener(OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        /**
         * 获取dialoge是否可以取消的状态值
         * @return
         */
        public boolean isCancellable() {
            return cancellable;
        }

        /**
         * 设置dialoge是否可以取消
         * @param cancellable
         * @return
         */
        public Builder setCancellable(boolean cancellable) {
            this.cancellable = cancellable;
            return this;
        }

        /**
         * 获取对话框外是否可以触摸
         * @return
         */
        public boolean isCanTouchOutside() {
            return canTouchOutside;
        }

        /**
         * 设置对话框外是否可以触摸
         */
        public Builder setCanTouchOutside(boolean canTouchOutside) {
            this.canTouchOutside = canTouchOutside;
            return this;
        }


        /**
         * 多选选中事件的响应
         * @param dialog
         * @param which
         * @param isChecked
         */
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            this.multiChoiceClickListener.onClick(dialog, which, isChecked);
        }

        /**
         * 设置按钮的背景资源id
         * @param btnResId
         * @return
         */
        public Builder setBtnResId(int btnResId) {
            this.btnResId = btnResId;
            return this;
        }

        /**
         * 生成EditableDialog对象
         */
        public EditDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final EditDialog dialog = new EditDialog(context, R.style.Dialog);

            /**如果未设置contentview ,则使用默认的*/
            View layout = resId > 0 ?
                    inflater.inflate(resId, null)
                    : inflater.inflate(R.layout.editdialog, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            /**如果设置了dialoge的背景,就使用*/
            if (bckResId > 0) {
                layout.setBackground(ActivityCompat.getDrawable(context, bckResId));
            }
            // 如果设置了确认键
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    if (btnResId > 0) {
                        ((Button) layout.findViewById(R.id.positiveButton))
                                .setBackground(ActivityCompat.getDrawable(context, btnResId));
                    }
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // 如果设置了取消键
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    if (btnResId > 0) {
                        ((Button) layout.findViewById(R.id.negativeButton))
                                .setBackground(ActivityCompat.getDrawable(context, btnResId));
                    }
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            /**使用自定义的属性*/
            if (dismissListener != null) {
                dialog.setOnDismissListener(dismissListener);
            }
            dialog.setCancelable(cancellable);
            dialog.setCanceledOnTouchOutside(canTouchOutside);

            /**如果设置了编辑框内容*/
            if (message != null) {
                editText = ((EditText) layout.findViewById(
                        R.id.message));
                editText.setText(message);
                editText.selectAll();
            } else if (contentView != null) {
                // 如果未设置编辑框内容
                // 添加内容体view
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView,
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }

}