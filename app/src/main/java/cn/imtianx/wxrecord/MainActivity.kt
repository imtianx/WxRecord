package cn.imtianx.wxrecord

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import cn.imtianx.wxrecord.service.UploadService
import cn.imtianx.wxrecord.util.CommonUtils
import cn.imtianx.wxrecord.util.Const
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*


/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName MainActivity
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 4:00 PM
 */

class MainActivity : AppCompatActivity() {

    private var mWxAlarmManager: AlarmManager? = null

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_START_UPLOAD -> {
                    startService(Intent(this@MainActivity, UploadService::class.java))
                }
                MSG_RESRESH_LAST_MSG_ID -> {
                    tv_last_msg_id.text = SPUtils.getInstance().getString(Const.LAST_MSG_ID,"0")
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWxAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        registerCopyWxDbAlarm()
        sendMessage(MSG_START_UPLOAD, 1000L)
        sendMessage(MSG_RESRESH_LAST_MSG_ID)

        btn_check_root.setOnClickListener {
            ToastUtils.showShort("root status : ${CommonUtils.checkRoot(packageName)}")
        }

        btn_reset_last_msg.setOnClickListener {
            SPUtils.getInstance().put(Const.LAST_MSG_ID, "0")
            sendMessage(MSG_RESRESH_LAST_MSG_ID)
        }

    }


    private fun registerCopyWxDbAlarm() {
        ToastUtils.showShort("启动定时检测 copy 微信 db")
        mWxAlarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000L, 60 * 1000L,
            PendingIntent.getBroadcast(
                this, 0,
                Intent(Const.ACTION_ALARM_COPY_WX_DB), 0
            )
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun sendMessage(msgWhat: Int, delayMillis: Long = 0L, bundle: Bundle? = null) {
        mHandler.sendMessageDelayed(Message().apply {
            what = msgWhat
            bundle?.let {
                data = it
            }
        }, delayMillis)
    }

    companion object {
        const val MSG_START_UPLOAD = 10001
        const val MSG_RESRESH_LAST_MSG_ID = 10002
    }


}

