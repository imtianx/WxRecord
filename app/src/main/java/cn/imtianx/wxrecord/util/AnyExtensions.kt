package cn.imtianx.wxrecord.util

import android.util.Log
import com.blankj.utilcode.util.ShellUtils
import com.blankj.utilcode.util.TimeUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName AnyExtensions
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 3:54 PM
 */

fun ShellUtils.checkRoot(pkgCodePath: String): Boolean {
    var process: Process? = null
    try {
        val cmd = "ls -l $pkgCodePath"
        process = Runtime.getRuntime().exec(cmd)

        val mReader = BufferedReader(InputStreamReader(process!!.inputStream))
        val mRespBuff = StringBuffer()
        val buff = CharArray(1024 * 10)
        var ch = mReader.read(buff)
        while (ch != -1) {
            mRespBuff.append(buff, 0, ch)
            ch = mReader.read(buff)
        }
        mReader.close()
        //root后权限的表达式
        val reg = "-rwxr[\\s\\S]+"
        return Pattern.matches(reg, mRespBuff.toString())
    } catch (e: Exception) {
        throw Exception(e.message)
    } finally {
        try {
            process!!.destroy()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}

fun Any.log(msg: String = "") {
    Log.e("tx", "\t\ttime: ${TimeUtils.getNowString()}\t\tmsg:\t\t$msg \n")
}
