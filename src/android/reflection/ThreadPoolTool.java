/**
 * 
 */
package android.reflection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.application.CommonApplication;
import android.http.HttpRequest;
import android.interfaces.CommonNetWorkExceptionToast;
import android.interfaces.HandleMessageListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.reflection.ExceptionEnum.GsonJsonParserException;
import android.reflection.ExceptionEnum.HttpIOException;
import android.reflection.ExceptionEnum.HttpProtocolException;
import android.reflection.ExceptionEnum.HttpRequestFalse400;
import android.reflection.ExceptionEnum.HttpRequestFalse403;
import android.reflection.ExceptionEnum.HttpRequestFalse404;
import android.reflection.ExceptionEnum.HttpRequestFalse405;
import android.reflection.ExceptionEnum.HttpRequestFalse500;
import android.reflection.ExceptionEnum.HttpRequestFalse502;
import android.reflection.ExceptionEnum.HttpRequestFalse503;
import android.reflection.ExceptionEnum.HttpRequestFalse504;
import android.reflection.ExceptionEnum.RefelectException;
import android.reflection.ExceptionEnum.ReflectionActivityFinished;
import android.reflection.ExceptionEnum.ReflectionClassNotFoundException;
import android.reflection.ExceptionEnum.ReflectionIllegalAccessException;
import android.reflection.ExceptionEnum.ReflectionIllegalArgumentException;
import android.reflection.ExceptionEnum.ReflectionNoSuchMethodErrorException;
import android.reflection.ExceptionEnum.ReflectionParamHasNullException;
import android.reflection.ExceptionEnum.ReflectionSecurityException;
import android.reflection.ExceptionEnum.ReflectionTimeOutException;
import android.reflection.ExceptionEnum.ReflectionUnknownHostException;
import android.reflection.ExceptionEnum.ReflectionUnknownServiceException;
import android.reflection.ExceptionEnum.ReflectionUnsupportedEncodingException;
import android.reflection.ExceptionEnum.XmlIOException;
import android.reflection.ExceptionEnum.XmlParserException;
import android.utils.ShowLog;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 ******************************************************************************************
 * @className:多线程异步http请求类
 * @author: Atar 
 * @createTime:2014-5-18下午11:34:10
 * @modifyTime:
 * @version: 1.0.0
 * @description:利用多线程进行异步操作网络请求，此类使用单利模式
 ******************************************************************************************
 */
public class ThreadPoolTool {

	private static final String TAG = ThreadPoolTool.class.getSimpleName();
	private static ThreadPoolTool mInstance;// 异步请求单例模式
	private final Handler handler = new Handler(Looper.getMainLooper());
	private final CommonNetWorkExceptionToast mCommonNetWorkExceptionToast = new CommonNetWorkExceptionToast();

	public static ThreadPoolTool getInstance() {
		if (mInstance == null) {
			mInstance = new ThreadPoolTool();
		}
		return mInstance;
	}

	// 线程池
	private final ExecutorService exec = Executors.newCachedThreadPool();
	private Gson gson = new Gson();

	/**
	 * 执行异步任务
	 * @author :Atar
	 * @createTime:2015-9-22下午4:24:05
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param mRunnable
	 * @description:
	 */
	public void execute(Runnable mRunnable) {
		if (exec != null) {
			exec.execute(mRunnable);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示
	 * @author :Atar
	 * @createTime:2014-8-18下午4:47:28
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:
	 */
	public void setAsyncTask(int msg, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, null, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2015-9-21上午10:20:33
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:
	 */
	public void setAsyncTask(int msg, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, activity, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 ，区分哪一个线程
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param whichThread 同一请求Url  多次请求中的哪一次 ------->对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 */
	public void setAsyncTask(int msg, int whichThread, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, null, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 ，区分哪一个线程 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param whichThread 同一请求Url  多次请求中的哪一次 ------->对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 */
	public void setAsyncTask(int msg, int whichThread, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, activity, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示
	 * @author :Atar
	 * @createTime:2014-8-18下午4:48:08
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, null, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-8-18下午4:48:08
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, activity, className, methodName, params,
				null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 区分哪一个线程
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url 对应http返回时handler -------> 中msg.what
	 * @param whichThread  同一请求Url 多次请求中的哪一次 -------> 对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 * 不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, int whichThread, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, null, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 区分哪一个线程 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url 对应http返回时handler -------> 中msg.what
	 * @param whichThread  同一请求Url 多次请求中的哪一次 -------> 对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 * 不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, int whichThread, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, activity, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 多线程多异步请求 带Toast提示 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：0x1015
	 * @author :Atar
	 * @createTime:2014-12-12上午10:44:36
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param what 代表哪一个请求Url------->对应http返回时handler 中msg.what
	 * @param msg2 同一请求Url 但请求中参数1不相同-------> 对应http返回时handler 中msg.arg2
	 * @param msg1 同一请求Url 但请求中参数2不相同---msg1值必须大于0----> 对应http返回时handler 中msg.arg1
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:应用场景，在同一界面，在网络不好情况下 同一请求地址 反回数据结构一样 请求参数1换了两个值请求 在没有返回情况下，用户又去操作，
	 * 请求参数2换了两个值，那这4个请求 下次回来时 页面需要展示的数据为最后一次请求的数据，此时前几次请求回来的数据 不是用户想要的如果不区分导致数据错乱
	 * 怎么区分时会用到 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：-0x1015 -0x1016 且 msg1值必须大于0
	 */
	public void setAsyncTask(int what, int msg2, int msg1, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(what, msg2, msg1, mHandleMessageListener, null, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 多线程多异步请求 带Toast提示 加入activity 生命周期 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：0x1015
	 * @author :Atar
	 * @createTime:2014-12-12上午10:44:36
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param what 代表哪一个请求Url------->对应http返回时handler 中msg.what
	 * @param msg2 同一请求Url 但请求中参数1不相同-------> 对应http返回时handler 中msg.arg2
	 * @param msg1 同一请求Url 但请求中参数2不相同---msg1值必须大于0----> 对应http返回时handler 中msg.arg1
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @description:应用场景，在同一界面，在网络不好情况下 同一请求地址 反回数据结构一样 请求参数1换了两个值请求 在没有返回情况下，用户又去操作，
	 * 请求参数2换了两个值，那这4个请求 下次回来时 页面需要展示的数据为最后一次请求的数据，此时前几次请求回来的数据 不是用户想要的如果不区分导致数据错乱
	 * 怎么区分时会用到 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：-0x1015 -0x1016 且 msg1值必须大于0
	 */
	public void setAsyncTask(int what, int msg2, int msg1, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params) {
		HttpImplementTask task = new HttpImplementTask(what, msg2, msg1, mHandleMessageListener, activity, className, methodName, params, null);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/*******************************************************************以下是带有 typeOfT Gson解析对象异步请求方法 可调任意同步请求类和方法********************************************************************/

	/**
	 * 异步处理http请求，显示toast异常提示
	 * @author :Atar
	 * @createTime:2014-8-18下午4:47:28
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:
	 */
	public void setAsyncTask(int msg, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, null, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2015-9-21上午10:20:33
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:
	 */
	public void setAsyncTask(int msg, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, activity, className, methodName, params,
				typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 ，区分哪一个线程
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param whichThread 同一请求Url  多次请求中的哪一次 ------->对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 */
	public void setAsyncTask(int msg, int whichThread, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, null, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，显示toast异常提示 ，区分哪一个线程 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param whichThread 同一请求Url  多次请求中的哪一次 ------->对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 */
	public void setAsyncTask(int msg, int whichThread, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithToast, mHandleMessageListener, activity, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示
	 * @author :Atar
	 * @createTime:2014-8-18下午4:48:08
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, null, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-8-18下午4:48:08
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url -------> 对应http返回时handler 中msg.what
	 * @param mHandleMessageListener 
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, ErrorMsgEnum.NetWorkThreadMsg2, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, activity, className, methodName, params,
				typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 区分哪一个线程
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url 对应http返回时handler -------> 中msg.what
	 * @param whichThread  同一请求Url 多次请求中的哪一次 -------> 对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 * 不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, int whichThread, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, null, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 异步处理http请求，不显示toast异常提示 区分哪一个线程 加入activity 生命周期
	 * @author :Atar
	 * @createTime:2014-10-8上午11:06:22
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param msg 代表哪一个请求Url 对应http返回时handler -------> 中msg.what
	 * @param whichThread  同一请求Url 多次请求中的哪一次 -------> 对应http返回时handler 中msg.arg2
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:如ListView 中多个item中有按钮请求，点击一个再点击一个，然后再点击一个。。。。list中的position 可用于 whichThread
	 * 不显示toast异常提示应用场景，如一些后台循环请求，但此请求就是出问题，在当前界面又不需要给用户展示
	 */
	public void setAsyncTaskWhitoutToast(int msg, int whichThread, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(msg, whichThread, ErrorMsgEnum.NetWorkMsg1WhithoutToast, mHandleMessageListener, activity, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 多线程多异步请求 带Toast提示 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：0x1015
	 * @author :Atar
	 * @createTime:2014-12-12上午10:44:36
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param what 代表哪一个请求Url------->对应http返回时handler 中msg.what
	 * @param msg2 同一请求Url 但请求中参数1不相同-------> 对应http返回时handler 中msg.arg2
	 * @param msg1 同一请求Url 但请求中参数2不相同---msg1值必须大于0----> 对应http返回时handler 中msg.arg1
	 * @param mHandleMessageListener
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:应用场景，在同一界面，在网络不好情况下 同一请求地址 反回数据结构一样 请求参数1换了两个值请求 在没有返回情况下，用户又去操作，
	 * 请求参数2换了两个值，那这4个请求 下次回来时 页面需要展示的数据为最后一次请求的数据，此时前几次请求回来的数据 不是用户想要的如果不区分导致数据错乱
	 * 怎么区分时会用到 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：-0x1015 -0x1016 且 msg1值必须大于0
	 */
	public void setAsyncTask(int what, int msg2, int msg1, HandleMessageListener mHandleMessageListener, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(what, msg2, msg1, mHandleMessageListener, null, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * 多线程多异步请求 带Toast提示 加入activity 生命周期 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：0x1015
	 * @author :Atar
	 * @createTime:2014-12-12上午10:44:36
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param what 代表哪一个请求Url------->对应http返回时handler 中msg.what
	 * @param msg2 同一请求Url 但请求中参数1不相同-------> 对应http返回时handler 中msg.arg2
	 * @param msg1 同一请求Url 但请求中参数2不相同---msg1值必须大于0----> 对应http返回时handler 中msg.arg1
	 * @param mHandleMessageListener
	 * @param activity 用于判断activity是否关闭情况下用到
	 * @param className
	 * @param methodName
	 * @param params
	 * @param typeOfT Gson解析对象
	 * @description:应用场景，在同一界面，在网络不好情况下 同一请求地址 反回数据结构一样 请求参数1换了两个值请求 在没有返回情况下，用户又去操作，
	 * 请求参数2换了两个值，那这4个请求 下次回来时 页面需要展示的数据为最后一次请求的数据，此时前几次请求回来的数据 不是用户想要的如果不区分导致数据错乱
	 * 怎么区分时会用到 特别注意msg1 不能设置成 ErrorMsgEnum.NetWorkMsg1WhithoutToast 即值不能为：-0x1015 -0x1016 且 msg1值必须大于0
	 */
	public void setAsyncTask(int what, int msg2, int msg1, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] params, Type typeOfT) {
		HttpImplementTask task = new HttpImplementTask(what, msg2, msg1, mHandleMessageListener, activity, className, methodName, params, typeOfT);
		if (exec != null) {
			exec.execute(task);
		}
	}

	/**
	 * HttpImplementTask:实现http异步加载Runnable
	 * @author: Atar 
	 * @createTime:2014-5-19上午12:34:55
	 * @modifyTime:
	 * @version: 1.0.0
	 * @description:
	 */
	private class HttpImplementTask implements Runnable {
		private HandleMessageListener mHandleMessageListener;
		private String reflectClassName;
		private String ReflectMethodName;
		private Object[] Reflectargs;
		private int msgWhat = ErrorMsgEnum.ENotDefine_Msg;// 默认未知错误异常
		private int msg2 = -1;// 默认不区分线程
		private int msg1 = -1;
		private Type typeOfT;// 返回解析Gson用到
		private Message msg = Message.obtain();
		private boolean hasActivity;
		private WeakReference<Activity> mWeakReference;// 弱引用 用于判断activity是否关闭情况下用到

		public HttpImplementTask(int msg, int msg2, int msg1, HandleMessageListener mHandleMessageListener, Activity activity, String className, String methodName, Object[] args, Type typeOfT) {
			msgWhat = msg;
			this.msg2 = msg2;
			this.msg1 = msg1;
			this.mHandleMessageListener = mHandleMessageListener;
			reflectClassName = className;
			ReflectMethodName = methodName;
			Reflectargs = args;
			this.typeOfT = typeOfT;
			if (activity != null) {
				hasActivity = true;
				mWeakReference = new WeakReference<Activity>(activity);
			}
		}

		public void run() {
			if (!HttpRequest.IsUsableNetWork(CommonApplication.getContext())) {
				msg.what = ErrorMsgEnum.EMobileNetUseless_Msg;
				if (msg1 != ErrorMsgEnum.NetWorkMsg1WhithoutToast) {
					msg1 = ErrorMsgEnum.NetWorkMsg1WhithToast;
				}
				msg.arg1 = msg1;
				msg.arg2 = msg2;
				if (handler != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							mCommonNetWorkExceptionToast.NetWorkHandlerMessage(msg, mHandleMessageListener);
						}
					});
				}
				return;
			}
			try {
				Object objReutrn = Reflection.invokeStaticMethod(reflectClassName, ReflectMethodName, Reflectargs);
				if (hasActivity && mWeakReference != null && mWeakReference.get() != null && mWeakReference.get().isFinishing()) {
					ShowLog.i(TAG, "---activity已经关闭---异步线程执行到此结束-------->");
					return;
				}
				if (typeOfT != null && objReutrn instanceof String) {
					ShowLog.i(TAG, Reflectargs[0] + "---->" + typeOfT.toString() + "------>" + objReutrn);
					if (typeOfT != String.class) {
						objReutrn = gson.fromJson((String) objReutrn, typeOfT);
					}
				}
				// if (objReutrn != null) {
				msg.obj = objReutrn;
				// } else {// 没有找到反射方法
				// msgWhat = ErrorMsgEnum.EHttpIO_Msg;
				// }
			} catch (RefelectException e) {
				if (msg1 != ErrorMsgEnum.NetWorkMsg1WhithoutToast) {
					msg1 = ErrorMsgEnum.NetWorkMsg1WhithToast;
				}
				msg.arg1 = msg1;
				// 22个异常捕获
				if (e != null) {
					if (e instanceof HttpProtocolException) {
						msgWhat = ErrorMsgEnum.EHttpProtocol_Msg;
					} else if (e instanceof HttpIOException) {
						msgWhat = ErrorMsgEnum.EHttpIO_Msg;
					} else if (e instanceof ReflectionTimeOutException) {
						msgWhat = ErrorMsgEnum.EConnectTimeout_Msg;
					} else if (e instanceof ReflectionClassNotFoundException) {
						msgWhat = ErrorMsgEnum.EClassNotFound_Msg;
					} else if (e instanceof ReflectionParamHasNullException) {
						msgWhat = ErrorMsgEnum.EParamHasNull_Msg;
					} else if (e instanceof GsonJsonParserException) {
						msgWhat = ErrorMsgEnum.EJsonParser_Msg;
					} else if (e instanceof ReflectionSecurityException) {
						msgWhat = ErrorMsgEnum.ESecurity_Msg;
					} else if (e instanceof ReflectionIllegalAccessException) {
						msgWhat = ErrorMsgEnum.EIllegalAccess_Msg;
					} else if (e instanceof ReflectionNoSuchMethodErrorException) {
						msgWhat = ErrorMsgEnum.ENotFoundMethods_Msg;
					} else if (e instanceof ReflectionIllegalArgumentException) {
						msgWhat = ErrorMsgEnum.EParamUnInvalid_Msg;
					} else if (e instanceof XmlParserException) {
						msgWhat = ErrorMsgEnum.EXmlParser_Msg;
					} else if (e instanceof XmlIOException) {
						msgWhat = ErrorMsgEnum.EXmlIO_Msg;
					} else if (e instanceof HttpRequestFalse400) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail400;
					} else if (e instanceof HttpRequestFalse403) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail403;
					} else if (e instanceof HttpRequestFalse404) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail404;
					} else if (e instanceof HttpRequestFalse405) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail405;
					} else if (e instanceof HttpRequestFalse502) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail502;
					} else if (e instanceof HttpRequestFalse503) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail503;
					} else if (e instanceof HttpRequestFalse504) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail504;
					} else if (e instanceof HttpRequestFalse500) {
						msgWhat = ErrorMsgEnum.EHttpRequestFail500;
					} else if (e instanceof ReflectionUnknownHostException) {
						msgWhat = ErrorMsgEnum.EUnknownHost_msg;
					} else if (e instanceof ReflectionUnknownServiceException) {
						msgWhat = ErrorMsgEnum.EUnknownService_msg;
					} else if (e instanceof ReflectionUnsupportedEncodingException) {
						msgWhat = ErrorMsgEnum.EUnsupportedEncoding_msg;
					} else if (e instanceof ReflectionActivityFinished) {
						ShowLog.i(TAG, "---ReflectionActivityFinished------activity已经关闭---异步线程执行到此结束-------->");
						return;
					} else {
						msgWhat = ErrorMsgEnum.ENotDefine_Msg;
					}
				}
				msg.obj = e.getMessage();
			} catch (Exception e) {
				if (msg1 != ErrorMsgEnum.NetWorkMsg1WhithoutToast) {
					msg1 = ErrorMsgEnum.NetWorkMsg1WhithToast;
				}
				msg.arg1 = msg1;
				if (e instanceof JsonSyntaxException) {
					msgWhat = ErrorMsgEnum.EJsonParser_Msg;
					msg.obj = e.getMessage();
				} else {
					msgWhat = ErrorMsgEnum.ENotDefine_Msg;
					msg.obj = "ENotDefine_Msg";
				}
			} finally {

			}
			if (hasActivity && mWeakReference != null && mWeakReference.get() != null && mWeakReference.get().isFinishing()) {
				ShowLog.i(TAG, "---activity已经关闭---异步线程执行到此结束-------->");
				return;
			}
			if (handler != null) {
				msg.what = msgWhat;
				msg.arg1 = msg1;
				msg.arg2 = msg2;
				handler.post(new Runnable() {
					@Override
					public void run() {
						mCommonNetWorkExceptionToast.NetWorkHandlerMessage(msg, mHandleMessageListener);
					}
				});
			}
		}
	}

	/**
	 * 通用网络错误提示 供扩展所用
	 * @author :Atar
	 * @createTime:2017-3-16上午9:40:08
	 * @version:1.0.0
	 * @modifyTime:
	 * @modifyAuthor:
	 * @param errorMsgWhat
	 * @description:
	 */
	public void toastException(int errorMsgWhat) {
		final Message msg = Message.obtain();
		msg.what = errorMsgWhat;
		msg.arg1 = ErrorMsgEnum.NetWorkMsg1WhithToast;
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					mCommonNetWorkExceptionToast.NetWorkHandlerMessage(msg, null);
				}
			});
		}
	}
}
