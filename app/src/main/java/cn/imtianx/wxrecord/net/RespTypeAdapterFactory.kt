package cn.imtianx.wxrecord.net

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName RespTypeAdapterFactory
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/2/27 2:50 PM
 */
class RespTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                return delegate.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(jin: JsonReader): T {
                var jsonElement = elementAdapter.read(jin)
                if (jsonElement.isJsonObject) {
                    val jsonObject = jsonElement.asJsonObject

                    if (jsonObject.has("success")) {
                        ExceptionHandler.throwExceptionIfNotSuccess(jsonObject)

                        if (jsonObject.has("data")) {
                            jsonElement = jsonObject.get("data")
                        }
                    }
                }
                return delegate.fromJsonTree(jsonElement)
            }

        }.nullSafe()

    }
}