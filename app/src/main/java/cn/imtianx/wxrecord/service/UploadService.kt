package cn.imtianx.wxrecord.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import cn.imtianx.wxrecord.Message
import cn.imtianx.wxrecord.RefreshMsgId
import cn.imtianx.wxrecord.util.CommonUtils
import cn.imtianx.wxrecord.util.Const
import cn.imtianx.wxrecord.util.log
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ShellUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import org.litepal.LitePal
import org.litepal.LitePalDB
import org.litepal.crud.DataSupport
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName UploadService
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 4:15 PM
 */
class UploadService : Service() {

    private var mUin = ""
    private var mUinEnc = ""
    private val curApkPath = "/storage/emulated/0/wxrecord/"

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    @SuppressLint("HardwareIds", "CheckResult")
    override fun onCreate() {
        super.onCreate()
        val imeiObservable = Observable
            .interval(0, 1 * 60, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .map {
                // 861579034182767
                val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                return@map manager.deviceId
            }
        val uinObservable = Observable
            .interval(0, 1 * 60, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnNext {
                ShellUtils.execCmd("chmod -R 777 ${Const.PATH_WX_ROOT}", true)
            }
            .map {
                // 1145104186
                val doc = Jsoup.parse(File(Const.PATH_WX_SP_UIN), "UTF-8")
                val elements = doc.select("int")
                elements
                    .filter { it.attr("name") == "_auth_uin" }
                    .forEach { mUin = it.attr("value") }
                if (mUin.isEmpty()) {
                    throw NullPointerException("当前没有登录微信，请登录后重试")
                }
                log("uin----------------$mUin")
                return@map mUin
            }

        Observable.zip(
            imeiObservable,
            uinObservable,
            BiFunction<String, String, String> { imie, uin ->

                EncryptUtils.encryptMD5ToString(imie + uin).toLowerCase().substring(0, 7).let {
                    log("imei:$imie\t\t uin:$uin\t\t数据库密码：$it")
                    it
                }
            })
            .doOnNext {
                mUinEnc = EncryptUtils.encryptMD5ToString("mm$mUin").toLowerCase()
                log("UinEnc:$mUinEnc")
            }
            .observeOn(Schedulers.io())
            .doOnNext { dbPwd ->
                val wxDbDir = File("${Const.PATH_WX_DB}$mUinEnc")
                val fileList = CommonUtils.searchFile(wxDbDir, Const.WX_DB_FILE_NAME)
                fileList.forEach {
                    val copyFilePath = "$curApkPath${Const.WX_DB_FILE_NAME}"
                    log("new db path:$copyFilePath\t\t preDir: ${it.absolutePath}")
                    CommonUtils.copyFile(it.absolutePath, copyFilePath)
                    openWXDB(File(copyFilePath), dbPwd)
                }

            }.subscribe(object : Observer<String> {
                override fun onComplete() {
                    Log.e("tx", "onComplete---------")
                }

                override fun onSubscribe(d: Disposable) {
                    log("onSubscribe---------:${d.isDisposed}")
                }

                override fun onNext(t: String) {
                    log(
                        "onNext--------- $t\t\t current thread:${Thread.currentThread().name}"
                    )
                }

                override fun onError(e: Throwable) {
                    log("onError---------:${e.printStackTrace()}\n${e.message}")
                }
            }
            )
    }

    private fun openWXDB(file: File, password: String) {
        ToastUtils.showShort("正在读取微信数据，请稍候...")
        log("open db----------")
        SQLiteDatabase.loadLibs(this)
        val hook = object : SQLiteDatabaseHook {
            override fun preKey(database: SQLiteDatabase) {}

            override fun postKey(database: SQLiteDatabase) {
                database.rawExecSQL("PRAGMA cipher_migrate;")
            }
        }
        try {
            val db = SQLiteDatabase.openOrCreateDatabase(
                file, password, null, hook
            )
            openMessageTable(db)
            db.close()
        } catch (e: Exception) {
            log("open db error : ${e.message}")
            ToastUtils.showShort("打开数据库失败：${e.message}\n")
        }
    }

    /**
     * open user info table
     */
    private fun openUserInfoTable(db: SQLiteDatabase) {
        val values = ArrayList<String>()
        // account,nickname
        val cursor =
            db.rawQuery("select value from userinfo where id = ? or id = ?", arrayOf("2", "4"))
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val value = cursor.getString(cursor.getColumnIndex("value"))
                values.add(value)
            }
        }
        cursor.close()
    }

    /**
     * open message table
     */
    private fun openMessageTable(db: SQLiteDatabase) {
        switchDBUser(Const.UPLOAD_WX_USER_NAME)
        val cursor = db.rawQuery(
            "select * from message where talkerId = ? and msgId > ? ",
            arrayOf(Const.UPLOAD_USER_ID, SPUtils.getInstance().getString(Const.LAST_MSG_ID, "0"))
        )

        log("cursor                    count：${cursor.count}")
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val msgSvrId = cursor.getString(cursor.getColumnIndex("msgId"))
                SPUtils.getInstance().put(Const.LAST_MSG_ID, msgSvrId ?: "0")
                val type = cursor.getString(cursor.getColumnIndex("type"))
                var content = cursor.getString(cursor.getColumnIndex("content"))
                if (content == null) content = ""
                if (msgSvrId == null) {
                    log("该次记录 msgSvrId 为空，跳过")
                    continue
                }
                val list = DataSupport.where("msgSvrId = ?", msgSvrId).find(Message::class.java)
                if (list.isEmpty()) {
                    val message = Message()
                    message.msgSvrId = msgSvrId
                    message.type = type
                    message.content = content
                    Log.e("tx", "\n\n msg: $content \n\n")
                    uploadMsg(message)
                }
            }
        } else {
            log("have no usefual data from db~~~~~~~~~~")
        }
        cursor.close()
    }


    /**
     * get last msg id
     */
    private fun getLastMsgId(db: SQLiteDatabase): String {
        var lastMsgId = "0"
        val last = DataSupport.findLast(Message::class.java)
        if (last != null) {
            Log.e("tx", "本地数据库中存在最后一条记录，msgSvrid：${last.msgSvrId}")
            val msgCu =
                db.rawQuery(" select * from message where msgsvrid = ? ", arrayOf(last.msgSvrId))
            if (msgCu.count > 0) {
                while (msgCu.moveToNext()) {
                    lastMsgId = msgCu.getString(msgCu.getColumnIndex("msgId"))
                    Log.e("tx", "微信数据库中存在 msgSvrid 为：${last.msgSvrId} 的记录，msgid 为：$lastMsgId")
                }
            }
            msgCu.close()
        }
        Log.e("tx", "聊天记录从 msgid 为：$lastMsgId 处开始查询")
        return lastMsgId
    }

    private fun uploadMsg(message: Message) {

        CommonUtils.writeLog("msgId:\t${message.msgSvrId}\t\tcontent:\t\t${message.content}")
        EventBus.getDefault().post(RefreshMsgId())
    }

    private fun switchDBUser(dbName: String) = LitePal.use(LitePalDB.fromDefault(dbName))
}