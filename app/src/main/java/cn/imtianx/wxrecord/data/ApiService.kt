package cn.imtianx.wxrecord.data

import cn.imtianx.wxrecord.net.BaseResponse
import cn.imtianx.wxrecord.util.Const
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName ApiSeriverce
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/2/27 3:27 PM
 */
interface ApiService {

    @POST(Const.API_UPLOAD_WX_MSG)
    fun postWxMsg(@Body body: RequestBody): Observable<BaseResponse<Boolean>>

}