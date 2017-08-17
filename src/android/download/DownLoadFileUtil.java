/**
 * 
 */
package android.download;

import android.interfaces.HandlerListener;
import android.reflection.ThreadPoolTool;
import android.utils.ShowLog;

/**
 *****************************************************************************************************************************************************************************
 * 
 * @author :Atar
 * @createTime:2017-8-16下午5:53:59
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class DownLoadFileUtil {

	public void downFile(HandlerListener mHandlerListener, String strDownloadUrl, String strDownloadFileName, String strDownloadDir) {
		ThreadPoolTool.getInstance().execute(new DownLoadRunable(mHandlerListener, strDownloadUrl, strDownloadFileName, strDownloadDir));
	}

	class DownLoadRunable implements Runnable {
		/** 下载监控操作handler */
		private HandlerListener mHandlerListener;
		/** 下载链接 */
		private String strDownloadUrl;
		/** 下载文件名 */
		private String strDownloadFileName;
		/** 本地下载文件目录 */
		private String strDownloadDir;

		public DownLoadRunable(HandlerListener mHandlerListener, String strDownloadUrl, String strDownloadFileName, String strDownloadDir) {
			this.mHandlerListener = mHandlerListener;
			this.strDownloadUrl = strDownloadUrl;
			this.strDownloadDir = strDownloadDir;
			this.strDownloadFileName = strDownloadFileName;
		}

		@Override
		public void run() {
			if (strDownloadUrl == null || strDownloadUrl.length() == 0 || (!strDownloadUrl.contains("http://") && !strDownloadUrl.contains("https://"))) {
				return;
			}
			ShowLog.i("DownloadThread", "strDownloadUrl--->" + strDownloadUrl);
			DownLoadFile.downLoad(strDownloadUrl, strDownloadFileName, strDownloadDir, mHandlerListener);
		}
	}
}
