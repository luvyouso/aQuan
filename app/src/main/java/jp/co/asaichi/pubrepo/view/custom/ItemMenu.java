package jp.co.asaichi.pubrepo.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.co.asaichi.pubrepo.R;
import jp.co.asaichi.pubrepo.databinding.LayoutItemMenuBinding;

/**
 * Created by nguyentu on 11/28/17.
 */

public class ItemMenu extends LinearLayout {

    private LayoutItemMenuBinding mLayoutItemMenuBinding;

    public ItemMenu(Context context) {
        super(context);
        intView(getContext(), null);
    }

    public ItemMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        intView(getContext(), attrs);
    }

    public ItemMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intView(getContext(), attrs);
    }

    /**
     * @param context
     * @param attrs
     */
    private void intView(Context context, AttributeSet attrs) {
        mLayoutItemMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_item_menu, this, true);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemMenu, 0, 0);
            try {
                //icon
//                Drawable drawable = typedArray.getDrawable(R.styleable.ItemMenu_icon_menu);
                int drawable = typedArray.getResourceId(R.styleable.ItemMenu_icon_menu, R.mipmap.ic_launcher);
                Glide.with(getContext())
                        .load(drawable)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .error(R.mipmap.ic_launcher))
                        .into(mLayoutItemMenuBinding.mImageViewIcon);
                // title
                mLayoutItemMenuBinding.mTextViewTitle.setText(typedArray.getText(R.styleable.ItemMenu_title_menu));
                //notification
                CharSequence notification = typedArray.getText(R.styleable.ItemMenu_notification);
                if (!TextUtils.isEmpty(notification)) {
                    mLayoutItemMenuBinding.mTextViewNotification.setVisibility(VISIBLE);
                    mLayoutItemMenuBinding.mTextViewNotification.setText(notification);
                } else {
                    mLayoutItemMenuBinding.mTextViewNotification.setVisibility(GONE);
                }
                boolean isTop = typedArray.getBoolean(R.styleable.ItemMenu_isTop, false);
                boolean isBottom = typedArray.getBoolean(R.styleable.ItemMenu_isBottom, false);
                if (isTop) {
                    mLayoutItemMenuBinding.mViewLineBottom.setVisibility(GONE);
                }
                if (isBottom) {
                    mLayoutItemMenuBinding.mViewLineTop.setVisibility(GONE);
                }

            } finally {
                typedArray.recycle();
            }
        }
    }

    public LayoutItemMenuBinding getLayoutItemMenuBinding() {
        return mLayoutItemMenuBinding;
    }

    public void setLayoutItemMenuBinding(LayoutItemMenuBinding mLayoutItemMenuBinding) {
        this.mLayoutItemMenuBinding = mLayoutItemMenuBinding;
    }
}
