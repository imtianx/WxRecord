package cn.imtianx.wxrecord.net

import com.google.gson.JsonObject
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * <pre>
 *     @desc: handler exception
 * </pre>
 * @author 奚岩
 * @date 2018/11/12 2:16 PM
 */
object ExceptionHandler {

    private const val CODE_DEFAULT = 0

    private const val CODE_AUTH_FAIL = 60210

    fun throwExceptionIfNotSuccess(jsonObject: JsonObject) {
        val isNotSuccess = jsonObject.get("success").asBoolean.not()

        if (isNotSuccess) {
            val msg = if (jsonObject.has("msg")) {
                jsonObject.get("msg").asString
            } else {
                ""
            }
            val code = if (jsonObject.has("code")) {
                jsonObject.get("code").asInt
            } else {
                CODE_DEFAULT
            }
            if (isAuthFail(code)) {
                throw  AuthFailException(msg)
            } else {
                throw BizException(code, msg)
            }
        }
    }

    private fun isAuthFail(code: Int) = code == CODE_AUTH_FAIL


    fun isServerBusy(throwable: Throwable): Boolean {
        return (throwable is SocketException) || (throwable is IOException)
    }

    fun isNetworkUnavailable(throwable: Throwable): Boolean {
        return (throwable is UnknownHostException) || (throwable is ConnectException)
    }

    fun isNetworkTimeout(throwable: Throwable): Boolean {
        return (throwable is SocketTimeoutException)
    }
}

