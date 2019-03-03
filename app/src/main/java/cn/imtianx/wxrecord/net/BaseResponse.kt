package cn.imtianx.wxrecord.net

/**
 * <pre>
 *     @desc: common result
 * </pre>
 * @author 奚岩
 * @date 2018/5/31 6:14 PM
 */
class BaseResponse<out T>(
    val errCode: Int = RESP_CODE_SUCCESS,
    val msg: String? = null,
    val data: T?
) {

    fun isSuccess() = errCode == RESP_CODE_SUCCESS

    companion object {
        private const val RESP_CODE_SUCCESS = 200
        private const val RESP_CODE_ERROR = -1

        fun <T> success(data: T?): BaseResponse<T> {
            return BaseResponse(
                RESP_CODE_SUCCESS,
                "success",
                data
            )
        }

        fun <T> fail(msg: String?, code: Int = RESP_CODE_ERROR): BaseResponse<T> {
            return BaseResponse(code, msg, null)
        }
    }

}

fun <T> BaseResponse<T>.applyActionWithNetworkData(
    successAction: (T) -> Unit,
    failedAction: ((String, Int) -> Unit)?
) {
    if (isSuccess()) {
        data?.let {
            successAction(it)
        }
    } else {
        if (failedAction != null) {
            failedAction(msg ?: "未知错误", errCode)
        } else {
        }
    }

}

