//package android.upload;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.UnknownServiceException;
//import java.util.Map;
//
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//public class UploadFile {
//
//	public final static String TAG = UploadFile.class.getSimpleName();
//
//	public final static int UPLOAD_FLAG_FAIL = 0;
//	public final static int UPLOAD_FLAG_SUCCESS = 1;
//	public final static int UPLOAD_FLAG_ING = 2;
//	public final static int UPLOAD_FLAG_ABORT = 3;
//	public final static int UPLOAD_WEB_CALLBACK = 4;
//	public final static int UPLOAD_SERIVER_ERROR = 5;
//
//	public final static int HTTP_TIME_OUT = 5000;
//
//	public static void upLoad(String strServerPath, Map<String, String> params, Map<String, File> files, Handler mHandler) {
//		String BOUNDARY = java.util.UUID.randomUUID().toString();
//		String PREFIX = "--", LINEND = "\r\n";
//		String MULTIPART_FROM_DATA = "multipart/form-data";
//		String CHARSET = "UTF-8";
//
//		try {
//			URL uri = new URL(strServerPath);
//
//			HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
//			httpURLConnection.setConnectTimeout(HTTP_TIME_OUT);// 6秒钟连接超时
//			httpURLConnection.setReadTimeout(5 * 1000); // 缓存的最长时间
//			httpURLConnection.setDoInput(true);// 允许输入
//			httpURLConnection.setDoOutput(true);// 允许输出
//			httpURLConnection.setUseCaches(false); // 不允许使用缓存
//			httpURLConnection.setRequestMethod("POST");
//			httpURLConnection.setRequestProperty("Connection", "keep-alive");
//			httpURLConnection.setRequestProperty("Charsert", "UTF-8");
//			httpURLConnection.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
//			// 首先组拼文本类型的参数
//			StringBuilder sb = new StringBuilder();
//			for (Map.Entry<String, String> entry : params.entrySet()) {
//				sb.append(PREFIX);
//				sb.append(BOUNDARY);
//				sb.append(LINEND);
//				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
//				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//				// sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
//				sb.append(LINEND);
//				sb.append(entry.getValue());
//				sb.append(LINEND);
//			}
//			Log.d("UploadFile--sb------>", sb.toString());
//			DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
//			outStream.write(sb.toString().getBytes());
//
//			InputStream in = null;
//			// 发送文件数据
//			if (files != null) {
//				for (Map.Entry<String, File> file : files.entrySet()) {
//					StringBuilder sb1 = new StringBuilder();
//					sb1.append(PREFIX);
//					sb1.append(BOUNDARY);
//					sb1.append(LINEND);
//					sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
//					sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
//					sb1.append(LINEND);
//					outStream.write(sb1.toString().getBytes());
//					InputStream is = new FileInputStream(file.getValue());
//					byte[] buffer = new byte[1024];
//					int len = 0;
//					int m = 0;
//					int nFileLength = is.available();
//					while ((len = is.read(buffer)) != -1) {
//						outStream.write(buffer, 0, len);
//						m += len;
//						int Percent = (int) m * 100 / nFileLength; // 上传进度值
//						if (Percent >= 1) {
//							Log.d(TAG, "Percent:" + Percent);
//							if (Percent >= 99) {
//								Percent = 99;
//							}
//							Message msg = mHandler.obtainMessage(UPLOAD_FLAG_ING, Percent, 0);
//							mHandler.sendMessage(msg);
//						}
//					}
//
//					is.close();
//					outStream.write(LINEND.getBytes());
//				}
//
//				// 请求结束标志
//				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
//				outStream.write(end_data);
//				outStream.flush();
//				// 得到响应码
//				int res = httpURLConnection.getResponseCode();
//				if (res == HttpURLConnection.HTTP_OK) {
//					InputStream mInputStream = null;
//					mInputStream = httpURLConnection.getInputStream();
//
//					InputStreamReader isr = null;
//					isr = new InputStreamReader(mInputStream, CHARSET);
//
//					BufferedReader br = new BufferedReader(isr);
//					String result = br.readLine();
//					Log.d("UploadFile", "result:" + result);
//					Message msg = mHandler.obtainMessage(UPLOAD_WEB_CALLBACK, result);
//					mHandler.sendMessage(msg);
//				}
//				outStream.close();
//				httpURLConnection.disconnect();
//			}
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (UnknownServiceException e1) {
//			// TODO Auto-generated catch block
//			mHandler.sendEmptyMessage(UPLOAD_SERIVER_ERROR);
//			e1.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			mHandler.sendEmptyMessage(UPLOAD_FLAG_FAIL);
//			e1.printStackTrace();
//
//		}
//	}
//
//	public static void upLoadEx(String strServerPath, Map<String, String> params, Map<String, File> files) {
//		String BOUNDARY = java.util.UUID.randomUUID().toString();
//		String PREFIX = "--", LINEND = "\r\n";
//		String MULTIPART_FROM_DATA = "multipart/form-data";
//		String CHARSET = "UTF-8";
//
//		try {
//			URL uri = new URL(strServerPath);
//
//			HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
//			httpURLConnection.setConnectTimeout(HTTP_TIME_OUT);// 6秒钟连接超时
//			httpURLConnection.setReadTimeout(5 * 1000); // 缓存的最长时间
//			httpURLConnection.setDoInput(true);// 允许输入
//			httpURLConnection.setDoOutput(true);// 允许输出
//			httpURLConnection.setUseCaches(false); // 不允许使用缓存
//			httpURLConnection.setRequestMethod("POST");
//			httpURLConnection.setRequestProperty("Connection", "keep-alive");
//			httpURLConnection.setRequestProperty("Charsert", "UTF-8");
//			httpURLConnection.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
//			// 首先组拼文本类型的参数
//			StringBuilder sb = new StringBuilder();
//			for (Map.Entry<String, String> entry : params.entrySet()) {
//				sb.append(PREFIX);
//				sb.append(BOUNDARY);
//				sb.append(LINEND);
//				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
//				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//				// sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
//				sb.append(LINEND);
//				sb.append(entry.getValue());
//				sb.append(LINEND);
//			}
//			Log.d("UploadFile--sb------>", sb.toString());
//			DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
//			outStream.write(sb.toString().getBytes());
//
//			InputStream in = null;
//			// 发送文件数据
//			if (files != null) {
//				for (Map.Entry<String, File> file : files.entrySet()) {
//					StringBuilder sb1 = new StringBuilder();
//					sb1.append(PREFIX);
//					sb1.append(BOUNDARY);
//					sb1.append(LINEND);
//					sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
//					sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
//					sb1.append(LINEND);
//					outStream.write(sb1.toString().getBytes());
//					InputStream is = new FileInputStream(file.getValue());
//					byte[] buffer = new byte[1024];
//					int len = 0;
//					int nFileLength = is.available();
//					while ((len = is.read(buffer)) != -1) {
//						outStream.write(buffer, 0, len);
//					}
//
//					is.close();
//					outStream.write(LINEND.getBytes());
//				}
//
//				// 请求结束标志
//				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
//				outStream.write(end_data);
//				outStream.flush();
//				// 得到响应码
//				int res = httpURLConnection.getResponseCode();
//				if (res == HttpURLConnection.HTTP_OK) {
//					InputStream mInputStream = null;
//					mInputStream = httpURLConnection.getInputStream();
//
//					InputStreamReader isr = null;
//					isr = new InputStreamReader(mInputStream, CHARSET);
//
//					BufferedReader br = new BufferedReader(isr);
//					String result = br.readLine();
//					Log.d("UploadFile", "result:" + result);
//				}
//				outStream.close();
//				httpURLConnection.disconnect();
//			}
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (UnknownServiceException e1) {
//			e1.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//	}
//}
