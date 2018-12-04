package com.dkp.shopping.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dkp.shopping.R;

public class ActionBarMainView extends RelativeLayout implements OnClickListener {
	private TextView tvTitle;
	public static final int ACTIONBAR_MAIN_FIRST = 0x0;
	public static final int ACTIONBAR_MAIN_SECOND = 0x1;
	public static final int ACTIONBAR_MAIN_THIRD = 0x2;
	private ImageView iVSearch;
	private View rootView;

	public ActionBarMainView(Context context) {
		super(context);
		initView(context);
	}

	public ActionBarMainView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public ActionBarMainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}


	private void initView(Context context) {
		rootView = LayoutInflater.from(context).inflate(R.layout.action_bar_main, this);
		tvTitle = rootView.findViewById(R.id.actionbar_title);
		iVSearch = rootView.findViewById(R.id.actionbar_search);
		updateView(ACTIONBAR_MAIN_FIRST);
	}

	/**
	 *
	 * @param type 更改的类型
	 */
	public void updateView(int type) {
		switch (type) {
			case ACTIONBAR_MAIN_FIRST:
				if(null != rootView){
					rootView.setVisibility(View.VISIBLE);
				}
				tvTitle.setText(R.string.tab_first_name);
				break;
			case ACTIONBAR_MAIN_SECOND:
				if(null != rootView){
					rootView.setVisibility(View.VISIBLE);
				}
				tvTitle.setText(R.string.tab_second_name);
				iVSearch.setVisibility(View.GONE);
				break;
			case ACTIONBAR_MAIN_THIRD:
				if(null != rootView){
					rootView.setVisibility(View.VISIBLE);
				}
				tvTitle.setText(R.string.tab_third_name);
			break;
			default:
					break;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}
}
