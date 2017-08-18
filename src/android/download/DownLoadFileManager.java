/**
 * 
 */
package android.download;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.application.CrashHandler;
import android.common.CommonHandler;
import android.interfaces.HandlerListener;
import android.reflection.ThreadPoolTool;
import android.utils.FileUtils;
import android.utils.ShowLog;

/**
 *****************************************************************************************************************************************************************************
 * 多线程异步下载管理器 ，可同时下载多个,可暂停, 可断点续传 
 * @author :Atar
 * @createTime:2011-8-18上午11:46:11
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class DownLoadFileManager {
	private String TAG = DownLoadFileManager.class.getSimpleName();

	private static DownLoadFileManager instance;
	/**弱引用管理多个同时下载*/
	private Map<String, WeakReference<DownLoadFile>> map;

	public static DownLoadFileManager getInstance() {
		if (instance == null) {
			instance = new DownLoadFileManager();
			instance.map = new HashMap<String, WeakReference<DownLoadFile>>();
		}
		return instance;
	}

	/**
	 * 下载文件
	 * @author :Atar
	 * @createTime:2011-8-18下午1:59:19
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param Activity:弱引用持有UI Activity 在activity finishi时自动停止下载
	 * @param HandlerListener 下载回调监听
	 * @param whcih 哪一个下载，用于同时下载多个 which值必须不相同
	 * @param fileUrl 文件url
	 * @param strDownloadFileName 本地文件名
	 * @param strDownloadDir 本地文件目录
	 * @description:
	 */
	public void downLoad(final Activity activity, final HandlerListener handlerListener, final int which, final String fileUrl, final String strDownloadFileName, final String strDownloadDir) {
		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					DownLoadFile mDownLoadFile = new DownLoadFile();
					if (map != null) {
						map.put(Integer.toString(which), new WeakReference<DownLoadFile>(mDownLoadFile));
					}
					mDownLoadFile.downLoad(activity, handlerListener, which, fileUrl, strDownloadFileName, strDownloadDir);
				} catch (Exception e) {
					ShowLog.e(TAG, "handerMessage-->" + CrashHandler.crashToString(e));
				}
			}
		});
	}

	/**
	 * 暂停下载
	 * @author :Atar
	 * @createTime:2011-8-18下午2:03:52
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param which 哪一个下载，用于同时下载多个
	 * @description:
	 */
	public void pauseDownload(final int which) {
		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (map != null && map.get(Integer.toString(which)) != null) {
						DownLoadFile mDownLoadFile = map.get(Integer.toString(which)).get();
						mDownLoadFile.pauseDownload();
					}
				} catch (Exception e) {
					ShowLog.e(TAG, "handerMessage-->" + CrashHandler.crashToString(e));
				}
			}
		});
	}

	/**
	 * 获得上次没有下载完的文件进度百分比值 
	 * @author :Atar
	 * @createTime:2017-8-18下午3:30:55
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @return
	 * @description:
	 */
	public void initTempFilePercent(final int which, final HandlerListener handlerListener, final String fileUrl, final String strDownloadFileName, final String strDownloadDir) {
		ThreadPoolTool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(strDownloadDir + File.separator + strDownloadFileName);
					File tempFile = new File(strDownloadDir + File.separator + strDownloadFileName + ".tmp" + which);
					if (file.exists() && !tempFile.exists()) {
						CommonHandler.getInstatnce().handerMessage(handlerListener, which, 0, DownLoadFileBean.DOWLOAD_FLAG_SUCCESS, 100);
					} else {
						if (file.exists()) {
							long tempSize = file.length();
							long fileLength = FileUtils.getUrlFileSize(fileUrl);
							int nPercent = 0;
							if (fileLength != 0) {
								nPercent = (int) (tempSize * 100 / fileLength);
							}
							CommonHandler.getInstatnce().handerMessage(handlerListener, which, 0, DownLoadFileBean.DOWLOAD_FLAG_ING, nPercent);
						}
					}
				} catch (Exception e) {
					ShowLog.e(TAG, "initTempFilePercent-->" + CrashHandler.crashToString(e));
				}
			}
		});
	}

	/**
	 * 下载动作结束移除 管理中的此次下载对象
	 * @author :Atar
	 * @createTime:2011-8-18下午2:10:46
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param which
	 * @description:
	 */
	public void remove(int which) {
		try {
			if (map != null && map.containsKey(Integer.toString(which))) {
				map.remove(Integer.toString(which));
			}
		} catch (Exception e) {
			ShowLog.e(TAG, "handerMessage-->" + CrashHandler.crashToString(e));
		}
	}
}
