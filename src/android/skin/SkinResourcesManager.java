/**
 * 
 */
package android.skin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.download.DownLoadFileManager;
import android.interfaces.HandlerListener;
import android.os.Environment;
import android.os.Message;
import android.reflection.ThreadPoolTool;
import android.utils.FileUtils;
import android.utils.MDPassword;
import android.utils.ShowLog;

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
	private String TAG = SkinResourcesManager.class.getSimpleName();

	/**是否加载apk资源 false加载library下资源*/
	public static boolean isLoadApkSkin = true;
	private String download_skin_Url = "";
	/**主工程包名*/
	private String main_project_packname = "";
	/**皮肤工程包名*/
	private String skin_project_packname = "";
	/**assets根目录下资源文件 默认皮肤资源 */
	private String DEFAULT_ASSETS_SKIN_NAME = "skin.js";
	/**SD卡目录 下载 资源文件 皮肤资源*/
	private String SD_PATH = Environment.getExternalStorageDirectory() + "/.Android/.cache/.sjkfdifdns/";
	/**sd下默认皮肤资源*/
	private String DEFAULT_SD_SKIN_NAME = "default_skin";
	/**sd下载皮肤资源*/
	private String DOWNLOAD_SD_SKIN_NAME = "download_skin";

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
	 * @createTime:2017-9-18下午5:19:23
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param context
	 * @param isDownLoadApkSkin 是否加载apk资源皮肤
	 * @param main_project_packname 主工程包名
	 * @param skin_project_packname 皮肤工程包名
	 * @param download_skin_Url 下载皮肤url
	 * @description:
	 */
	public void initSkinResources(boolean isDownLoadApkSkin, String main_project_packname, String skin_project_packname, final String download_skin_Url) {
		isLoadApkSkin = isDownLoadApkSkin;
		this.main_project_packname = main_project_packname;
		this.skin_project_packname = skin_project_packname;
		if (isLoadApkSkin) {
			this.download_skin_Url = download_skin_Url;
			ThreadPoolTool.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					try {
						File file = new File(SD_PATH);
						if (!FileUtils.exists(SD_PATH)) {
							FileUtils.createDir(SD_PATH);
						}
						final File downloadFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME));
						File tempFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME) + ".tmp0");
						if (downloadFile.exists() && !tempFile.exists()) {// 存在下载皮肤文件，但不存在 下载缓存文件 代表下载完成有完整文件
							loadSkinResources(downloadFile.getAbsolutePath(), null);
							return;
						}
						File defaultFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME));
						if (!defaultFile.exists()) {
							copyfileFromAssetsToSD(mContext);
						}
						loadSkinResources(defaultFile.getAbsolutePath(), null);
					} catch (Exception e) {
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
			File file = new File(SD_PATH + MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME));
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
		} catch (Exception e) {
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
			mResources = new WeakReference<Resources>(skinResource);
			if (callback != null) {
				callback.loadSkinSuccess(skinResource);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				File tempFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME) + ".tmp0");
				if (downloadFile.exists() && !tempFile.exists()) {// 存在下载皮肤文件，但不存在 下载缓存文件 代表下载完成有完整文件
					newest_path = downloadFile.getAbsolutePath();
				} else {
					File defaultFile = new File(file.getAbsolutePath(), MDPassword.getPassword32(DEFAULT_SD_SKIN_NAME));
					if (defaultFile.exists()) {
						newest_path = defaultFile.getAbsolutePath();
					} else {
						return;
					}
				}
				loadSkinResources(newest_path, callback);
			}
		});
	}

	/**
	 * 下载皮肤
	 * @author :Atar
	 * @createTime:2017-9-20下午4:15:10
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @description:
	 */
	public void downLoadSkin() {
		// final File downloadFile = new File(SD_PATH + MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME));
		// File tempFile = new File(SD_PATH + MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME) + ".tmp0");
		// if (downloadFile.exists() && !tempFile.exists()) {// 存在下载皮肤文件，但不存在 下载缓存文件 代表下载完成有完整文件
		// downloadFile.deleteOnExit();
		// }
		DownLoadFileManager.getInstance().downLoad(null, handlerListener, 0, download_skin_Url, 0, true, MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME), SD_PATH);
	}

	HandlerListener handlerListener = new HandlerListener() {
		@Override
		public void onHandlerData(Message msg) {
			switch (msg.what) {
			case android.download.DownLoadFileBean.DOWLOAD_FLAG_FAIL:
				ShowLog.i(TAG, "皮肤下载失败");
				break;
			case android.download.DownLoadFileBean.DOWLOAD_FLAG_SUCCESS:
				if (9999 == msg.arg2) {
					loadSkinResources(SD_PATH + MDPassword.getPassword32(DOWNLOAD_SD_SKIN_NAME), null);
				}
				ShowLog.i(TAG, "皮肤下载成功");
				break;
			case android.download.DownLoadFileBean.DOWLOAD_FLAG_ING:
				if (9999 == msg.arg2) {
					loadSkinResources(null);
				}
				downLoadSkin();
				ShowLog.i(TAG, "皮肤正在下载:" + msg.arg2 + "%");
				break;
			}
		}
	};

	public Resources getResources() {
		if (isLoadApkSkin) {
			return mResources != null ? mResources.get() : null;
		} else {
			return mContext.getResources();
		}
	}

	public String getSkinPackName() {
		return isLoadApkSkin ? skin_project_packname : main_project_packname;
	}

	public interface loadSkinCallBack {
		void loadSkinSuccess(Resources mResources);
	}
}
