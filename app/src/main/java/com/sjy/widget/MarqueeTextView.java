package com.sjy.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView implements Runnable {
	private int currentScrollX;
	private boolean isStop = false;
	private int mTextWidth;
	private float mViewWidth;

	public MarqueeTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Paint paint = getPaint();
		String str = getText().toString();
		System.out.print(str);
		mTextWidth = (int) paint.measureText(str) + 2;
		mViewWidth = getWidth();
	}

	@Override
	public void run() {
		init();
		if(mTextWidth <= mViewWidth) {
			isStop = true;
		}
		
		currentScrollX += 1;
		
		scrollTo(currentScrollX, 0);
		if (isStop) {
			currentScrollX = 0;
			scrollTo(0, 0);
			return;
		}

		if (currentScrollX >= mTextWidth) {
			currentScrollX = 0;
			scrollTo(0, 0);
		}
		postDelayed(this, 5);
	}

	public void startScroll() {
		isStop = false;
		removeCallbacks(this);
		post(this);
	}

	public void stopScroll() {
		isStop = true;
	}

	public void startFor0() {
		currentScrollX = 0;
		startScroll();
	}
}
