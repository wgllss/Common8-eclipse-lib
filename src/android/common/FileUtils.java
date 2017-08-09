package android.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.application.CommonApplication;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * 
 */
public class FileUtils {

	public static void openFile(File f, Context context) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);
	}

	/* 判断文件MimeType的method */
	private static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	public static String getCacheDir() {
		String strHomeDir = "";// 主目录
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {// 有SD卡
			String str = android.os.Environment.getExternalStorageDirectory()
					.toString();
			if (!str.endsWith("/") && !str.endsWith("\\")) {
				str += '/';
			}
			strHomeDir = str;
		} else {
			strHomeDir = CommonApplication.getContext().getFilesDir()
					.getAbsolutePath();
		}

		return strHomeDir;
	}

	private final static String TAG = FileUtils.class.getCanonicalName();

	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 *            完整路径
	 * @return
	 */
	public static boolean exists(String path) {
		try {
			File file = new File(path);
			if (!file.exists() || file.isFile() == false)// 不存在
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean delFile(String filePath) {
		boolean ret = false;
		Log.d(TAG, "delFile:>>" + filePath);
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
				ret = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "delFile()>>" + e);
			return false;
		}
		return ret;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param dirName
	 * @return
	 */
	public static boolean delDir(String dirPath) {
		boolean ret = false;
		Log.d(TAG, "delDir:>>" + dirPath);
		try {
			File file = new File(dirPath);
			if (file.exists()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						if (!delDir(files[i].getName())) {
							return false;
						}
					} else {
						files[i].delete();
					}
				}
				file.delete(); // 删除空文件夹
				ret = true;
			} else
				return true;
		} catch (Exception e) {
			Log.e(TAG, "delDir()>>" + e);
			return false;
		}
		return ret;
	}

	/**
	 * 创建任意深度的文件所在文件夹
	 * 
	 * @param path
	 * @return File对象
	 */
	public static File createDir(String path) {
		File file = new File(path);
		// 寻找父目录是否存在
		File parent = new File(file.getAbsolutePath().substring(0,
				file.getAbsolutePath().lastIndexOf(File.separator)));
		// 如果父目录不存在，则递归寻找更上一层目录
		if (!parent.exists()) {
			createDir(parent.getPath());
			// 创建父目录
			file.mkdirs();
		} else {
			// 判断自己是否存在
			File self = new File(path);
			if (!self.exists())
				self.mkdirs();
		}

		return file;
	}

	/**
	 * 根据文件名获取其drawable值,如果本地此文件不存在
	 * 
	 * @param absolutePath
	 *            完整路径
	 * @return
	 */
	public static Drawable getDrawableByFilePath(String absolutePath) {
		System.out.println("getDrawableByFileName()");
		Bitmap bitmap = null;
		Drawable drawable = null;
		try {
			if (absolutePath != null && !absolutePath.equals("")) {
				if (FileUtils.exists(absolutePath)) {// 文件存在
					BitmapFactory.Options options = new BitmapFactory.Options();
					// options.inPreferredConfig= Bitmap.Config.ARGB_8888;
					options.inJustDecodeBounds = true;
					InputStream is = new FileInputStream(new File(absolutePath));
					bitmap = BitmapFactory.decodeStream(is, null, options);
					// bitmap = BitmapFactory.decodeFile(absolutePath, options);

					options.inSampleSize = computeSampleSize(options, -1,
							128 * 128);
					options.inJustDecodeBounds = false;
					try {
						bitmap = BitmapFactory.decodeStream(is, null, options);
						/*
						 * bitmap = BitmapFactory .decodeFile(absolutePath,
						 * options);
						 */
						if (!bitmap.isRecycled()) {
							bitmap.recycle();
							System.gc();
						}

					} catch (OutOfMemoryError err) {
						System.out.println("OutOfMemoryError" + err.toString());
					}

					drawable = new BitmapDrawable(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drawable;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	public static long getFileSizes(File f) throws Exception {// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	// 递归
	public long getFileSize(File f) throws Exception// 取得文件夹大小
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
}
