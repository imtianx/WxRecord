package cn.imtianx.wxrecord

import cn.imtianx.wxrecord.util.CommonUtils
import com.blankj.utilcode.util.EncryptUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun txTest(){
        val srcPath = "/Users/imtianx/workspace/workspace_yangche51/WxRecord/app/src/test/java/cn/imtianx/wxrecord/EnMicroMsg.db"
        val newPath = "/Users/imtianx/workspace/workspace_yangche51/WxRecord/app/src/test/java/cn/imtianx/wxrecord/22EnMicroMsg.db"
        CommonUtils.copyFile(srcPath,newPath)
        println("-------------    ${EncryptUtils.encryptMD5ToString("mm1145104186")}")

    }
}
