/**
 * 
 */
package android.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
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
						views[i].setBackgroundColor(resources.obtainTypedArray(
								resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getSkinPackName())).getColor(skinType, 0));
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
						textView[i].setTextColor(resources.obtainTypedArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getSkinPackName()))
								.getColor(skinType, 0));
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
						views[i].setBackgroundDrawable(resources.obtainTypedArray(
								resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getSkinPackName())).getDrawable(skinType));
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
						imageViews[i].setImageDrawable(resources.obtainTypedArray(
								resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getSkinPackName())).getDrawable(skinType));
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
			listView.setDivider(resources.obtainTypedArray(resources.getIdentifier(context.getResources().getString(resourcesName), "array", SkinResourcesManager.getSkinPackName())).getDrawable(
					skinType));
			listView.setDividerHeight(1);
		}
	}
}
