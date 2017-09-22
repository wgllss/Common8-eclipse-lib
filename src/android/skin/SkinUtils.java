/**
 * 
 */
package android.skin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 *****************************************************************************************************************************************************************************
 * 设置 颜色 背景 图片 
 * @author :Atar
 * @createTime:2017-9-18上午11:38:59
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
@SuppressLint("Recycle")
public class SkinUtils {

	/**
	 * 设置背景
	 * @author :Atar
	 * @createTime:2017-9-18上午11:45:29
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources 
	 * @param resourcesName 
	 * @param skinType
	 * @param views
	 * @description:
	 */
	public static void setBackgroundColor(Context context, Resources resources, int resourcesName, int skinType, View... views) {
		if (context != null && resources != null) {
			if (views != null && views.length > 0) {
				for (int i = 0; i < views.length; i++) {
					try {
						views[i].setBackgroundColor(getArrayColor(context, resources, resourcesName, skinType));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 设置字体颜色
	 * @author :Atar
	 * @createTime:2017-9-18上午11:45:53
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @param textView
	 * @description:
	 */
	public static void setTextColor(Context context, Resources resources, int resourcesName, int skinType, TextView... textView) {
		if (context != null && resources != null) {
			if (textView != null && textView.length > 0) {
				for (int i = 0; i < textView.length; i++) {
					try {
						textView[i].setTextColor(getArrayColor(context, resources, resourcesName, skinType));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 设置背景
	 * @author :Atar
	 * @createTime:2017-9-18上午11:45:29
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources 
	 * @param resourcesName 
	 * @param skinType
	 * @param views
	 * @description:
	 */
	@SuppressWarnings("deprecation")
	public static void setBackgroundDrawable(Context context, Resources resources, int resourcesName, int skinType, View... views) {
		if (context != null && resources != null) {
			if (views != null && views.length > 0) {
				for (int i = 0; i < views.length; i++) {
					try {
						views[i].setBackgroundDrawable(getArrayDrawable(context, resources, resourcesName, skinType));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 设置图片
	 * @author :Atar
	 * @createTime:2017-9-18上午11:45:29
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources 
	 * @param resourcesName 
	 * @param skinType
	 * @param views
	 * @description:
	 */
	public static void setImageDrawable(Context context, Resources resources, int resourcesName, int skinType, ImageView... imageViews) {
		if (context != null && resources != null) {
			if (imageViews != null && imageViews.length > 0) {
				for (int i = 0; i < imageViews.length; i++) {
					try {
						imageViews[i].setImageDrawable(getArrayDrawable(context, resources, resourcesName, skinType));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 设置listView分隔线
	 * @author :Atar
	 * @createTime:2017-9-18上午11:47:01
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @param listView
	 * @description:
	 */
	public static void setDivider(Context context, Resources resources, int resourcesName, int skinType, ListView listView) {
		if (context != null && listView != null) {
			listView.setDivider(getArrayDrawable(context, resources, resourcesName, skinType));
			listView.setDividerHeight(1);
		}
	}

	/**
	 * 设置Array下文字
	 * @author :Atar
	 * @createTime:2017-9-19下午4:18:28
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @param textView
	 * @description:
	 */
	public static void setText(Context context, Resources resources, int resourcesName, int skinType, TextView... textView) {
		if (context != null && resources != null) {
			if (textView != null && textView.length > 0) {
				for (int i = 0; i < textView.length; i++) {
					try {
						textView[i].setText(getArrayString(context, resources, resourcesName, skinType));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 得到Array下颜色值
	 * @author :Atar
	 * @createTime:2017-9-19下午2:53:24
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @param listView
	 * @return
	 * @description:
	 */
	public static int getArrayColor(Context context, Resources resources, int resourcesName, int skinType) {
		try {
			return resources.obtainTypedArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getInstance(context).getSkinPackName())).getColor(
					skinType, 0);
		} catch (Exception e) {
			return Color.TRANSPARENT;
		}
	}

	/**
	 * 得到Array下drawable
	 * @author :Atar
	 * @createTime:2017-9-19下午2:55:55
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @return
	 * @description:
	 */
	public static Drawable getArrayDrawable(Context context, Resources resources, int resourcesName, int skinType) {
		try {
			return resources.obtainTypedArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getInstance(context).getSkinPackName()))
					.getDrawable(skinType);
		} catch (Exception e) {
			return new ColorDrawable(Color.TRANSPARENT);
		}
	}

	/**
	 * 得到Array下String
	 * @author :Atar
	 * @createTime:2017-9-19下午4:14:54
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @param skinType
	 * @return
	 * @description:
	 */
	public static String getArrayString(Context context, Resources resources, int resourcesName, int skinType) {
		try {
			return resources.getStringArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getInstance(context).getSkinPackName()))[skinType];
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 得到颜色
	 * @author :Atar
	 * @createTime:2017-9-19下午4:26:01
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param resources
	 * @param resourcesName
	 * @return
	 * @description:
	 */
	public static int getColor(Context context, Resources resources, int resourcesName) {
		try {
			return resources.getColor(resources.getIdentifier(context.getResources().getString(resourcesName), "color", SkinResourcesManager.getInstance(context).getSkinPackName()));
		} catch (Exception e) {
			return 0;
		}
	}

	public static String getString(Context context, Resources resources, int resourcesName) {
		try {
			return resources.getString(resources.getIdentifier(context.getResources().getString(resourcesName), "string", SkinResourcesManager.getInstance(context).getSkinPackName()));
		} catch (Exception e) {
			return "";
		}
	}

	public static String[] getStringArray(Context context, Resources resources, int resourcesName) {
		try {
			return resources.getStringArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getInstance(context).getSkinPackName()));
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static Drawable getDrawable(Context context, Resources resources, int resourcesName) {
		try {
			return resources.getDrawable(resources.getIdentifier(context.getResources().getString(resourcesName), "drawable", SkinResourcesManager.getInstance(context).getSkinPackName()));
		} catch (Exception e) {
			return new ColorDrawable(Color.TRANSPARENT);
		}
	}

	// public static int getStyleID(Context context, Resources resources, int resourcesName) {
	// try {
	// return resources.getIdentifier(context.getResources().getString(resourcesName), "style", SkinResourcesManager.getInstance(context).getSkinPackName());
	// } catch (Exception e) {
	// return 0;
	// }
	// }
	//
	// public static int getAnimID(Context context, Resources resources, int resourcesName) {
	// try {
	// return resources.getIdentifier(context.getResources().getString(resourcesName), "anim", SkinResourcesManager.getInstance(context).getSkinPackName());
	// } catch (Exception e) {
	// return 0;
	// }
	// }

	public static float getDimenID(Context context, Resources resources, int resourcesName) {
		try {
			return resources.getDimension(resources.getIdentifier(context.getResources().getString(resourcesName), "dimen", SkinResourcesManager.getInstance(context).getSkinPackName()));
		} catch (Exception e) {
			return 0;
		}
	}
}
