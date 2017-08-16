package android.download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.common.CommonHandler;
import android.interfaces.HandlerListener;
import android.os.Message;
import android.utils.FileUtils;

public class DownLoadFile {
	/** 默认超时时长 */
	public final static int DEFAULT_WAITTIME = 5000;
	/** 临时文件后缀 */
	public final static String DEFAULT_SUFFIX = ".suffix";

	/** 下载Bean */
	private static DownLoadFileBean mDownLoadBean;
	/** 下载计数器,用于同步 */
	private volatile static CountDownLatch mPauseLatch;

	/**
	 * 根据网络URL 下载文件保存到本地strFilePath下
	 * 
	 * @param zipUrl
	 * @param zipName
	 * @param dirStr
	 */
	public static void downLoad(String zipUrl, String zipName, String dirStr, HandlerListener HandlerListener) {
		if (dirStr != null) {
			FileUtils.createDir(dirStr);
		}
		CountDownLatch latch = new CountDownLatch(1);
		mDownLoadBean = new DownLoadFileBean();
		mDownLoadBean.setFileSiteURL(zipUrl);// 设置远程文件地址
		mDownLoadBean.setFileSaveName(zipName);// 设置本地文件名
		mDownLoadBean.setFileSavePath(dirStr);// 设置本地文件路径
		mDownLoadBean.setFileThreadNum(1);// 单线程下载
		mDownLoadBean.setHandlerListenerr(HandlerListener);// 下载通知消息
		mDownLoadBean.setPauseDownloadFlag(false);// 下载终止标志位设为false
		DownLoadFileTask zipT = new DownLoadFileTask(mDownLoadBean, latch);
		ExecutorService executor = null;
		try {
			executor = Executors.newCachedThreadPool();
			executor.execute(zipT);
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
			// 如果不是被调用者停止,则发送成功失败消息
			if (mPauseLatch == null) {
				Message msg = new Message();
				if (mDownLoadBean != null) {
					msg.what = mDownLoadBean.isDownSuccess() ? DownLoadFileBean.DOWLOAD_FLAG_SUCCESS : DownLoadFileBean.DOWLOAD_FLAG_FAIL;
					CommonHandler.getInstatnce().handerMessage(mDownLoadBean.getHandlerListener(), msg);
					mDownLoadBean = null;
				}
			} else {
				mPauseLatch.countDown();
			}
		}
	}

	/**
	 * 暂停下载
	 */
	public static void pauseDownload() {
		if (mDownLoadBean != null) {
			mDownLoadBean.setPauseDownloadFlag(true);
			mPauseLatch = new CountDownLatch(1);
			try {
				mPauseLatch.await();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 发送停止下载消息
				Message msg = new Message();
				msg.what = DownLoadFileBean.DOWLOAD_FLAG_ABORT;
				CommonHandler.getInstatnce().handerMessage(mDownLoadBean.getHandlerListener(), msg);
				mPauseLatch = null;
				mDownLoadBean = null;
			}

		}
	}

	/**
	 * 通过文件流下载文件保存到本地strFilePath下
	 * 
	 * @param strUrl
	 *            远程地址
	 * @param strFileName
	 *            本地存储地址
	 * @return
	 */
	public static boolean downFileOnStream(String strUrl, String strFilePath) {
		OutputStream os = null;
		try {
			if (strFilePath != null) {
				String strDir = strFilePath.substring(0, strFilePath.lastIndexOf("/"));
				FileUtils.createDir(strDir);
			}

			// 重新编码所含中文
			String strRight = strUrl.substring(strUrl.lastIndexOf("/") + 1, strUrl.length() - 1);
			strUrl = strUrl.replaceAll(strRight, URLEncoder.encode(strRight, "utf-8"));
			// handle replace "+"->\\%20
			strUrl = strUrl.replaceAll("\\+", "%20");// 处理空格
			URL url = new URL(strUrl);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000);
			conn.connect();
			InputStream is = conn.getInputStream();
			if (is == null)
				throw new RuntimeException("stream is null");
			// 把文件存到path
			os = new FileOutputStream(strFilePath);
			byte buf[] = new byte[1024];
			for (int numread = is.read(buf), zero = 0; numread > 0;) {
				os.write(buf, zero, numread);
				numread = is.read(buf);
			}
			os.flush();
			is.close();
			os.close();

		} catch (Exception e) {
			try {
				if (os != null)
					os.close();
			} catch (Exception ex) {
				return false;
			}
			return false;
		}

		return true;
	}
}
