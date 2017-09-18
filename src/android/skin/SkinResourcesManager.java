/**
 * 
 */
package android.skin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.reflection.ThreadPoolTool;
import android.utils.FileUtils;
import android.utils.MDPassword;

/**
 *****************************************************************************************************************************************************************************
 * 皮肤资源管理器
 * @author :Atar
 * @createTime:2017-9-18上午10:33:21
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class SkinResourcesManager {
	/**是否加载apk资源 false加载library下资源*/
	public static boolean isLoadApkSkin = true;
	/**主工程包名*/
	public static String MAIN_PROJECT_PACKNAME = "";
	/**皮肤工程包名*/
	public static String SKIN_PROJECT_PACKNAME = "";

	/**assets根目录下资源文件 默认皮肤资源 */
	private String DEFAULT_ASSETS_SKIN_NAME = "skin.js";
	/**SD卡目录 下载 资源文件 皮肤资源*/
	private String SD_PATH = Environment.getExternalStorageDirectory() + "/.Android/.cache/.sjkfdifdns/";
	/**sd下默认皮肤资源*/
	private String DEFAULT_SD_SKIN_NAME;
	/**sd下载皮肤资源*/
	private String DOWNLOAD_SD_SKIN_NAME;

	private static SkinResourcesManager mInstance;
	private Context mContext;
	private WeakReference<Resources> mResources;

	public static SkinResourcesManager getInstance(Context mContext) {
		if (mInstance == null) {
			mInstance = new SkinResourcesManager();
			mInstance.mContext = mContext;
		}
		return mInstance;
	}

	/**
	 * 初始化皮肤资源
	 * @author :Atar
	 * @createTime:2017-9-18上午11:07:14
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @description:
	 */
	public void initSkinResources(final Context context) {
		if (isLoadApkSkin) {
			ThreadPoolTool.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					File file = new File(SD_PATH);
					if (!FileUtils.exists(SD_PATH)) {
						FileUtils.createDir(SD_PATH);
					}
					File downloadFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME));
					if (downloadFile.exists()) {
						loadSkinResources(downloadFile.getAbsolutePath(), null);
					} else {
						File defaultFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME));
						if (defaultFile.exists()) {
							loadSkinResources(defaultFile.getAbsolutePath(), null);
						} else {
							copyfileFromAssetsToSD(context);
						}
					}
				}
			});
		}
	}

	/**
	 * 从assets中复制apk到sd中
	 * @author :Atar
	 * @createTime:2017-9-18上午10:01:43
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @return
	 * @description:
	 */
	private boolean copyfileFromAssetsToSD(Context context) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(DEFAULT_ASSETS_SKIN_NAME);
			File file = new File(SD_PATH);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i); // 写入到文件
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copyIsFinish;
	}

	/**
	 * 同步 加载皮肤资源
	 * @author :Atar
	 * @createTime:2017-9-18上午11:08:05
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param skinFilePath
	 * @param callback
	 * @description:
	 */
	private void loadSkinResources(final String skinFilePath, final loadSkinCallBack callback) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(assetManager, skinFilePath);
			Resources superRes = mContext.getResources();
			Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
			if (mResources == null) {
				mResources = new WeakReference<Resources>(skinResource);
			}
			if (callback != null) {
				callback.loadSkinSuccess(skinResource);
			}
		} catch (Exception e) {

		}

	}

	/**
	 * 异步 加载皮肤资源
	 * @author :Atar
	 * @createTime:2017-9-18上午11:08:30
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param callback
	 * @description:
	 */
	public void loadSkinResources(final loadSkinCallBack callback) {
		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				String newest_path = SD_PATH + MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME);
				File file = new File(SD_PATH);
				if (!FileUtils.exists(SD_PATH)) {
					FileUtils.createDir(SD_PATH);
				}
				File downloadFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME));
				if (downloadFile.exists()) {
					newest_path = downloadFile.getAbsolutePath();
				} else {
					File defaultFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME));
					if (defaultFile.exists()) {
						newest_path = defaultFile.getAbsolutePath();
					}
				}
				loadSkinResources(newest_path, callback);
			}
		});
	}

	public Resources getResources() {
		return mResources != null ? mResources.get() : null;
	}

	public static String getSkinPackName() {
		return isLoadApkSkin ? SKIN_PROJECT_PACKNAME : MAIN_PROJECT_PACKNAME;
	}

	public interface loadSkinCallBack {
		void loadSkinSuccess(Resources mResources);
	}
}
