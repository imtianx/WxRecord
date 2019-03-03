package cn.imtianx.wxrecord.net

import cn.imtianx.wxrecord.data.ApiService
import cn.imtianx.wxrecord.util.Const
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName RetrofitClient
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/2/27 2:48 PM
 */
class RetrofitClient private constructor() {
    fun getRetrofit(): Retrofit {

        val gson = GsonBuilder()
            .registerTypeAdapterFactory(RespTypeAdapterFactory()).create()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(8, 20, TimeUnit.SECONDS))
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .validateEagerly(true)
            .build()

    }

    fun <T> create(service: Class<T>): T = getRetrofit().create(service)

    fun getApiService() = create(ApiService::class.java)

    companion object {
        fun getInstance() = Inner.INSTANCE
    }

    private object Inner {
        val INSTANCE = RetrofitClient()
    }
}