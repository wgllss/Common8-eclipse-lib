//package android.upload;
//
//import java.io.File;
//import java.util.Map;
//
//import android.os.Handler;
//
//public class UploadThread extends Thread {
//
//	private String strServerPath;
//	private Map<String, String> params;
//	private Map<String, File> files;
//	private Handler handler;
//
//	public UploadThread(String strServerPath, Map<String, String> params,
//			Map<String, File> files, Handler handler) {
//		this.strServerPath = strServerPath;
//		this.params = params;
//		this.files = files;
//		this.handler = handler;
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		super.run();
//		UploadFile.upLoad(strServerPath, params, files, handler);
//	}
//
//}
