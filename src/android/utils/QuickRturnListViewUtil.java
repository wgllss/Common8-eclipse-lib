/**
 * 
 */
package android.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 *****************************************************************************************************************************************************************************
 * 
 * @author :Atar
 * @createTime:2017-7-17下午1:50:44
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class QuickRturnListViewUtil {
	public static final int TOP = 5;
	public static final int BUTTOM = 6;

	private int mItemCount;
	private int mItemOffsetY[];
	private boolean scrollIsComputed = false;
	private int mHeight;
	private int rawY;

	private int quickReturnDirection = BUTTOM;
	private boolean isTopAnimation = true;
	private ListView listView;

	private int mQuickReturnHeight;
	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private static final int STATE_EXPANDED = 3;
	private int mState = STATE_ONSCREEN;
	private int mScrollY;
	private int mMinRawY = 0;
	private TranslateAnimation anim;

	private boolean noAnimation = false;
	private int mCachedVerticalScrollRange;

	public QuickRturnListViewUtil(int quickReturnDirection, boolean isTopAnimation, ListView listView) {
		super();
		this.quickReturnDirection = quickReturnDirection;
		this.isTopAnimation = isTopAnimation;
		this.listView = listView;
	}

	private void computeScrollY() {
		if (listView == null) {
			return;
		}
		mHeight = 0;
		mItemCount = listView.getAdapter().getCount();
		if (mItemOffsetY == null) {
			mItemOffsetY = new int[mItemCount];
		}
		for (int i = 0; i < mItemCount; ++i) {
			View view = listView.getAdapter().getView(i, null, listView);
			view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			mItemOffsetY[i] = mHeight;
			mHeight += view.getMeasuredHeight();
			System.out.println(mHeight);
		}
		scrollIsComputed = true;
	}

	private int getComputedScrollY() {
		if (listView == null) {
			return 0;
		}
		int pos, nScrollY, nItemY;
		View view = null;
		pos = listView.getFirstVisiblePosition();
		view = listView.getChildAt(0);
		nItemY = view.getTop();
		nScrollY = mItemOffsetY[pos] - nItemY;
		return nScrollY;
	}

	public void setQuickReturnEvent(final View mQuickReturnView, final View topPlaceHolder) {
		try {
			if (listView == null) {
				return;
			}
			listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					mQuickReturnHeight = mQuickReturnView.getHeight();
					computeScrollY();
					if (quickReturnDirection == TOP) {
						mCachedVerticalScrollRange = mHeight;
					}
				}
			});
			listView.setOnScrollListener(new OnScrollListener() {
				@SuppressLint("NewApi")
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (quickReturnDirection == BUTTOM) {
						if (firstVisibleItem > 1) {
							mQuickReturnView.setVisibility(View.VISIBLE);
						}
					} else {
						mQuickReturnView.setVisibility(View.VISIBLE);
					}
					mScrollY = 0;
					int translationY = 0;

					if (scrollIsComputed) {
						mScrollY = getComputedScrollY();
					}
					if (quickReturnDirection == BUTTOM) {
						rawY = mScrollY;
					} else if (quickReturnDirection == TOP) {
						rawY = (topPlaceHolder != null ? topPlaceHolder.getTop() : 0) - Math.min(mCachedVerticalScrollRange - listView.getHeight(), mScrollY);
					}

					switch (mState) {
					case STATE_OFFSCREEN:
						if (quickReturnDirection == BUTTOM) {
							if (rawY >= mMinRawY) {
								mMinRawY = rawY;
							} else {
								mState = STATE_RETURNING;
							}
						} else if (quickReturnDirection == TOP) {
							if (rawY <= mMinRawY) {
								mMinRawY = rawY;
							} else {
								mState = STATE_RETURNING;
							}
						}
						translationY = rawY;
						break;

					case STATE_ONSCREEN:
						if (quickReturnDirection == BUTTOM) {
							if (rawY > mQuickReturnHeight) {
								mState = STATE_OFFSCREEN;
								mMinRawY = rawY;
							}
						} else if (quickReturnDirection == TOP) {
							if (rawY < -mQuickReturnHeight) {
								mState = STATE_OFFSCREEN;
								mMinRawY = rawY;
							}
						}
						translationY = rawY;
						break;
					case STATE_RETURNING:
						if (quickReturnDirection == BUTTOM) {
							translationY = (rawY - mMinRawY) + mQuickReturnHeight;
							if (translationY < 0) {
								translationY = 0;
								mMinRawY = rawY + mQuickReturnHeight;
							}

							if (rawY == 0) {
								mState = STATE_ONSCREEN;
								translationY = 0;
							}

							if (translationY > mQuickReturnHeight) {
								mState = STATE_OFFSCREEN;
								mMinRawY = rawY;
							}
						} else if (quickReturnDirection == TOP) {
							if (!isTopAnimation) {
								translationY = (rawY - mMinRawY) - mQuickReturnHeight;
								if (translationY > 0) {
									translationY = 0;
									mMinRawY = rawY - mQuickReturnHeight;
								}
								if (rawY > 0) {
									mState = STATE_ONSCREEN;
									translationY = rawY;
								}
								if (translationY < -mQuickReturnHeight) {
									mState = STATE_OFFSCREEN;
									mMinRawY = rawY;
								}
							} else {
								if (translationY > 0) {
									translationY = 0;
									mMinRawY = rawY - mQuickReturnHeight;
								} else if (rawY > 0) {
									mState = STATE_ONSCREEN;
									translationY = rawY;
								} else if (translationY < -mQuickReturnHeight) {
									mState = STATE_OFFSCREEN;
									mMinRawY = rawY;
								} else if (isTopAnimation && mQuickReturnView.getTranslationY() != 0 && !noAnimation) {
									noAnimation = true;
									anim = new TranslateAnimation(0, 0, -mQuickReturnHeight, 0);
									anim.setFillAfter(true);
									anim.setDuration(250);
									mQuickReturnView.startAnimation(anim);
									anim.setAnimationListener(new AnimationListener() {

										@Override
										public void onAnimationStart(Animation animation) {
										}

										@Override
										public void onAnimationRepeat(Animation animation) {
										}

										@Override
										public void onAnimationEnd(Animation animation) {
											noAnimation = false;
											mMinRawY = rawY;
											mState = STATE_EXPANDED;
										}
									});
								}
							}
						}
						break;
					case STATE_EXPANDED:
						if (isTopAnimation && rawY < mMinRawY - 2 && !noAnimation) {
							noAnimation = true;
							anim = new TranslateAnimation(0, 0, 0, -mQuickReturnHeight);
							anim.setFillAfter(true);
							anim.setDuration(250);
							anim.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									noAnimation = false;
									mState = STATE_OFFSCREEN;
								}
							});
							mQuickReturnView.startAnimation(anim);
						} else if (translationY > 0) {
							translationY = 0;
							mMinRawY = rawY - mQuickReturnHeight;
						} else if (rawY > 0) {
							mState = STATE_ONSCREEN;
							translationY = rawY;
						} else if (translationY < -mQuickReturnHeight) {
							mState = STATE_OFFSCREEN;
							mMinRawY = rawY;
						} else {
							mMinRawY = rawY;
						}
						break;
					}
					if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
						anim = new TranslateAnimation(0, 0, translationY, translationY);
						anim.setFillAfter(true);
						anim.setDuration(0);
						mQuickReturnView.startAnimation(anim);
					} else {
						mQuickReturnView.setTranslationY(translationY);
					}
				}

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
