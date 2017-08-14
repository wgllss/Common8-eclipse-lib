/**
 * 
 */
package android.common;

import android.os.Handler;
import android.os.Looper;

/**
 *****************************************************************************************************************************************************************************
 * 全局公共 只用一个唯一的 handler  处理异步到UI之间的通信
 * @author :Atar
 * @createTime:2012-8-11下午1:55:31
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description: 如果我们在每个activity或是其他类里面都new 一个handler其实是比较低效率的做法，完全可以只用一个全局的静态handler来进行线程和主线程的通信。
 *****************************************************************************************************************************************************************************
 */
public class CommonHandler {

	private static CommonHandler instance;
	private Handler handler = new Handler(Looper.getMainLooper());

	public static synchronized CommonHandler getInstatnce() {
		if (instance == null) {
			instance = new CommonHandler();
		}
		return instance;
	}

	public Handler getHandler() {
		return handler;
	}
}
