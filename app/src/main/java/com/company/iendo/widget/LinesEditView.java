package com.company.iendo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.iendo.R;
import com.hjq.widget.view.ClearEditText;

/**
 * ignoreCnOrEn 为false的时候
 * 1个中文算1个
 * 2个英文算1个
 * <p>
 * 另外：如：只有一个英文时也算1个
 * <p>
 * Created by louisgeek on 2016/9/19.
 */
public class LinesEditView extends LinearLayout {
    private Context mContext;
    private ClearEditText id_et_input;
    private TextView id_tv_input;

    private int MAX_COUNT;
    private String hintText;
    private int hintTextColor;
    private boolean ignoreCnOrEn;
    private boolean showPositive;
    private String contentText;
    private int contentTextSize;
    private int contentTextColor;
    private float contentViewHeight;

    private boolean isInputAble = true;

    public void setInputAble(boolean inputAble) {
        isInputAble = inputAble;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInputAble) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }


    public LinesEditView(Context context) {
        this(context, null);
    }

    public LinesEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinesEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LinesEditView);
        MAX_COUNT = typedArray.getInteger(R.styleable.LinesEditView_classic_maxCount, 200);
        ignoreCnOrEn = typedArray.getBoolean(R.styleable.LinesEditView_classic_ignoreCnOrEn, true);
        showPositive = typedArray.getBoolean(R.styleable.LinesEditView_classic_showPositive, true);
        hintText = typedArray.getString(R.styleable.LinesEditView_classic_hintText);
        hintTextColor = typedArray.getColor(R.styleable.LinesEditView_classic_hintTextColor, Color.parseColor("#42000000"));
        contentText = typedArray.getString(R.styleable.LinesEditView_classic_contentText);
        contentTextColor = typedArray.getColor(R.styleable.LinesEditView_classic_contentTextColor, Color.parseColor("#8A000000"));
//        contentTextSize = typedArray.getDimensionPixelSize(R.styleable.LinesEditView_classic_contentTextSize, dp2px(context, 1));
        contentTextSize = typedArray.getDimensionPixelSize(R.styleable.LinesEditView_classic_contentTextSize, dp2px(context, getResources().getDimension(R.dimen.sp_6)));
        contentViewHeight = typedArray.getDimensionPixelSize(R.styleable.LinesEditView_classic_contentViewHeight,
                dp2px(context,250 ));
        typedArray.recycle();
        //
        init();
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_lines_edit_view, this);
        id_et_input = (ClearEditText) view.findViewById(R.id.id_et_input);
        id_tv_input = (TextView) view.findViewById(R.id.id_tv_input);

        if (this.getBackground() == null) {
            this.setBackgroundResource(R.drawable.selector_lines_edit_view_bg);
        }

        id_et_input.addTextChangedListener(mTextWatcher);
        id_et_input.setHint(hintText);
        id_et_input.setHintTextColor(hintTextColor);
        id_et_input.setText(contentText);
        id_et_input.setTextColor(contentTextColor);
        id_et_input.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
        id_et_input.setHeight((int) contentViewHeight);
        /**
         * 配合 id_tv_input xml的 android:focusable="true"
         android:focusableInTouchMode="true"

         在id_et_input设置完文本后

         不给id_et_input 焦点
         */
        id_tv_input.requestFocus();
        //init
        configCount();
        id_et_input.setSelection(id_et_input.length()); // 将光标移动最后一个字符后面
        /**
         * focus后给背景设置Selected
         */
        id_et_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                LinesEditView.this.setSelected(b);
            }
        });


    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        private int editStart;

        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            editStart = id_et_input.getSelectionStart();
            editEnd = id_et_input.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            id_et_input.removeTextChangedListener(mTextWatcher);

            try {
                if (ignoreCnOrEn) {
                    //当输入字符个数超过限制的大小时，进行截断操作
                    while (calculateLengthIgnoreCnOrEn(editable.toString()) > MAX_COUNT) {
                        editable.delete(editStart - 1, editEnd);
                        editStart--;
                        editEnd--;
                    }
                } else {
                    // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                    while (calculateLength(editable.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                        editable.delete(editStart - 1, editEnd);
                        editStart--;
                        editEnd--;
                    }
                }
            }catch (Exception e){
            }



            id_et_input.setSelection(editStart);

            // 恢复监听器
            id_et_input.addTextChangedListener(mTextWatcher);

            //update
            configCount();
        }
    };


    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    private int calculateLengthIgnoreCnOrEn(CharSequence c) {
        int len = 0;
        for (int i = 0; i < c.length(); i++) {
            len++;
        }
        return len;
    }

    private void configCount() {
        long nowCount;
        if (ignoreCnOrEn) {
            nowCount = calculateLengthIgnoreCnOrEn(id_et_input.getText().toString());
        } else {
            nowCount = calculateLength(id_et_input.getText().toString());
        }
        if (showPositive) {
            //正数显示 【当前输入数/总数】
            id_tv_input.setText(String.valueOf(nowCount) );
//            id_tv_input.setText(String.valueOf(nowCount) + "/" + MAX_COUNT);
        } else {
            //倒数显示 【剩余输入数/总数】
            id_tv_input.setText(String.valueOf((nowCount )));
//            id_tv_input.setText(String.valueOf((MAX_COUNT - nowCount)) + "/" + MAX_COUNT);
        }

    }

    private static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setContentText(String content) {
        contentText = content;
        if (id_et_input == null) {
            return;
        }
        id_et_input.setText(contentText);
    }

    public String getContentText() {
        if (id_et_input != null) {
            contentText = id_et_input.getText() == null ? "" : id_et_input.getText().toString();
        }
        return contentText;
    }

    public ClearEditText getContentEdit() {
        if (id_et_input != null) {
            return id_et_input;

        }
        return null;

    }


    public void setHintText(String hintText) {
        this.hintText = hintText;
        if (id_et_input == null) {
            return;
        }
        id_et_input.setHint(hintText);
    }

    public void setContentTextSize(int size) {
        if (id_et_input == null) {
            return;
        }
        id_et_input.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setContentTextColor(int color) {
        if (id_et_input == null) {
            return;
        }
        id_et_input.setTextColor(color);
    }

    public void setHintColor(int color) {
        if (id_et_input == null) {
            return;
        }
        id_et_input.setHintTextColor(color);
    }

    public String getHintText() {
        if (id_et_input != null) {
            hintText = id_et_input.getHint() == null ? "" : id_et_input.getHint().toString();
        }
        return hintText;
    }

    public void setMaxCount(int max_count) {
        this.MAX_COUNT = max_count;
        configCount();
    }

    public void setIgnoreCnOrEn(boolean ignoreCnOrEn) {
        this.ignoreCnOrEn = ignoreCnOrEn;
        configCount();
    }
}
