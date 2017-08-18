package android.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import android.common.CommonHandler;
import android.utils.ShowLog;

/**
 * 
 * 
 * <p>
 * Title:子线程下载
 * </p>
 * 
 * <p>
 * Description:描述
 * </p>
 * 
 */
public class SubDownLoadFileThead extends Thread {
	private final static String TAG = SubDownLoadFileThead.class.getCanonicalName();

	private long startPos; // 开始下载的指针位置
	private long endPos; // 结束下载的指针位置
	private int threadId; // 子线程号
	private final CountDownLatch latch;// 子信号量
	private RandomAccessFile file = null;// 存放的文件
	private RandomAccessFile tempFile = null;// 指针文件
	private DownLoadFileBean downLoadFileBean;
	private int timeout = 10000;// 超时时间
	private int reTryNum = 3;// 超时重试次数
	private int curNum;// 当前重试次数
	private boolean isOK = false;// 下载完成
	private String mis = "";// 提示信息

	private boolean isRange;// 是否支持断点续传

	public SubDownLoadFileThead(DownLoadFileBean downLoadFileBean, CountDownLatch latch, long startPos, long endPos, int threadId) {
		this.isRange = true;
		this.latch = latch;
		this.startPos = startPos;
		this.endPos = endPos;
		this.downLoadFileBean = downLoadFileBean;
		this.threadId = threadId;
		this.mis = "[(" + downLoadFileBean.getFileSiteURL() + ")子线程:" + threadId + "]";
		try {
			file = new RandomAccessFile(downLoadFileBean.getSaveFile(), "rw");
			tempFile = new RandomAccessFile(downLoadFileBean.getTempFile(), "rw");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SubDownLoadFileThead(DownLoadFileBean downLoadFileBean, CountDownLatch latch) {
		this.isRange = false;
		this.latch = latch;
		this.downLoadFileBean = downLoadFileBean;
		this.threadId = 0;
		this.mis = "[(" + downLoadFileBean.getFileSiteURL() + ")子线程:" + threadId + "]";
		try {
			file = new RandomAccessFile(downLoadFileBean.getSaveFile(), "rw");
			tempFile = new RandomAccessFile(downLoadFileBean.getTempFile(), "rw");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		ShowLog.i(TAG, mis + "开始下载!");
		curNum = 0;
		while (curNum < reTryNum && !isOK) {
			if (curNum > 0) {
				ShowLog.i(TAG, mis + "第" + curNum + "次重试下载:");
			}
			downLoad();
		}
		latch.countDown();// 完成之后结束通知
	}

	/** 首次连接,初使化长度 */
	private void downLoad() {
		// FileChannel fc=null;
		InputStream inputStream = null;
		HttpURLConnection con = null;
		Long myFileLength = 0L;// 临时文件长度,用于减少下载进度消息数量
		try {
			curNum++;
			long count = 0;
			URL url = new URL(downLoadFileBean.getFileSiteURL());
			con = (HttpURLConnection) url.openConnection();
			setConHead(con);
			// 设置连接超时时间为10000ms
			con.setConnectTimeout(timeout);
			// 设置读取数据超时时间为10000ms
			con.setReadTimeout(timeout);
			if (startPos < endPos && isRange) {
				// 设置下载数据的起止区间
				con.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
				ShowLog.i(TAG, "'" + downLoadFileBean.getFileSiteURL() + "'-Thread号:" + threadId + " 开始位置:" + startPos + ",结束位置：" + endPos);
				file.seek(startPos);// 转到文件指针位置
				// fc=file.getChannel();
			}
			int responseCode = con.getResponseCode();
			// 判断http status是否为HTTP/1.1 206 Partial Content或者200 OK
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
				inputStream = con.getInputStream();// 打开输入流
				int len = 0;
				byte[] b = new byte[1024];
				long seek = 4 + 16 * threadId;

				while (!downLoadFileBean.getPauseDownloadFlag() && (len = inputStream.read(b)) != -1) {
					file.write(b, 0, len);// 写入临时数据文件,外性能需要提高
					// MappedByteBuffer mbbo =
					// fc.map(FileChannel.MapMode.READ_WRITE, 0, len);
					// for(int i=0;i<len;i++){
					// mbbo.put(i, b[i]);
					// }
					count += len;
					startPos += len;
					tempFile.seek(seek);
					tempFile.writeLong(startPos);// 写入断点数据文件
					if (downLoadFileBean.getHandlerListener() != null && (count - myFileLength) > 1024) {
						myFileLength = count;
						int nPercent = (int) (startPos * 100 / downLoadFileBean.getFileLength());
						if (downLoadFileBean.getWeakReference() != null && downLoadFileBean.getWeakReference().get() != null && !downLoadFileBean.getWeakReference().get().isFinishing()) {
							CommonHandler.getInstatnce().handerMessage(downLoadFileBean.getHandlerListener(), downLoadFileBean.getWhich(), 0, DownLoadFileBean.DOWLOAD_FLAG_ING, nPercent);
						}
					}
				}

				if (startPos >= endPos) {
					isOK = true;
				}// 下载完成
					// fc.close();
			}
		} catch (Exception e) {
			ShowLog.e(TAG, mis + "异常:" + e);// logger.debug
		} finally {
			try {
				// 关闭连接
				if (con != null) {
					con.disconnect();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				// 关闭文件
				file.close();
				// 文件指针文件
				tempFile.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置请求头
	 * 
	 * @param httpConnection
	 */
	private void setConHead(HttpURLConnection httpConnection) {
		httpConnection.setRequestProperty("User-Agent", "java-download-core");// 设置头,也可以不做设置
		httpConnection.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
		httpConnection.setRequestProperty("Accept-Encoding", "aa");
		httpConnection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		httpConnection.setRequestProperty("Keep-Alive", "300");
		httpConnection.setRequestProperty("Connection", "keep-alive");
		httpConnection.setRequestProperty("Cache-Control", "max-age=0");
	}

}
