package cn.imtianx.wxrecord.net

import java.lang.Exception

/**
 * <pre>
 *     @desc:
 * </pre>
 * @author 奚岩
 * @date 2018/11/12 11:03 PM
 */
class AuthFailException(msg: String) : RuntimeException(msg)

class BizException(val errorCode: Int, msg: String) : RuntimeException(msg)

class ResponseThrowable(val errorCode : Int,msg: String) : Exception()