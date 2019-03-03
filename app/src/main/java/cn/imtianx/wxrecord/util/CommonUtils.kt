package cn.imtianx.wxrecord.util

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import com.blankj.utilcode.util.TimeUtils
import java.io.*
import java.util.regex.Pattern

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName CommonUtils
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 4:00 PM
 */
object CommonUtils {

    private var list = ArrayList<File>()

    fun checkRoot(pkgCodePath: String): Boolean {
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

    fun searchFile(file: File, fileName: String): List<File> {
        list.clear()
        recursiveSearchFile(file, fileName)
        return list
    }

    /**
     * 递归查询指定目录下的指定文件名的文件列表
     *
     * @param file     目录
     * @param fileName 需要查找的文件名称
     */
    private fun recursiveSearchFile(file: File, fileName: String) {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (childFile in files) {
                    recursiveSearchFile(childFile, fileName)
                }
            }
        } else {
            if (fileName == file.name) {
                list.add(file)
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    fun copyFile(oldPath: String, newPath: String) {
        try {
            val oldFile = File(oldPath)
            val newFileParent = File(newPath).parentFile
            if (!newFileParent.exists()) {
                newFileParent.mkdirs()
            }

            if (oldFile.exists()) {
                val inStream = FileInputStream(oldPath)
                val fs = FileOutputStream(newPath)
                val buffer = ByteArray(1024)
                var byteRead = inStream.read(buffer)
                while (byteRead != -1) {
                    fs.write(buffer, 0, byteRead)
                    byteRead = inStream.read(buffer)
                }
                inStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception(e.message)
        }
    }

    const val PATH_ROOT = "/wxrecord"
    const val FILE_LOG = "log.txt"

    private fun createLogFile() {
        val sd = Environment.getExternalStorageDirectory()
        val root = File(sd, PATH_ROOT)
        if (!root.exists()) {
            root.mkdir()
        }
        val log = File(root, FILE_LOG)
        if (!log.exists()) {
            log.createNewFile()
        }
    }

    fun writeLog(log: String) {
        createLogFile()
        try {
            val fos = FileOutputStream(
                File("${Environment.getExternalStorageDirectory()}$PATH_ROOT/$FILE_LOG"),
                true
            )
            fos.write("${TimeUtils.getNowString()}\t\t".toByteArray())
            fos.write("$log\n".toByteArray())
            fos.close()
        } catch (e: Exception) {
            log("写入日志失败：${e.message}")
        }
    }

    fun isAppRunning(context: Context, pkgName: String): Boolean {
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .apply {
                getRunningTasks(1000).forEach { taskInfo ->
                    if (taskInfo.topActivity.packageName == pkgName) {
                        return true
                    }
                }
            }
        return false
    }

}