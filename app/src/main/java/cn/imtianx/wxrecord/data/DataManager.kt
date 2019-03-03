package cn.imtianx.wxrecord.data

import cn.imtianx.wxrecord.net.RetrofitClient
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName DataManager
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/2/27 3:07 PM
 */
object DataManager {

    fun postWxMsg(content: String) = RetrofitClient.getInstance().getApiService().postWxMsg(
        RequestBody.create(
            MediaType.parse("application/json"), content
        )
    )
}