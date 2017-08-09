/**
 * 
 */
package android.reflection;

/**
 *****************************************************************************************************************************************************************************
 * 
 * @author :Atar
 * @createTime:2017-8-9下午5:01:53
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class NetWorkMsg {

	public int what, arg1, arg2, showToast;
	public Object obj;

	public NetWorkMsg(int what, int arg1, int arg2, int showToast, Object obj) {
		super();
		this.what = what;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.showToast = showToast;
		this.obj = obj;
	}

}
