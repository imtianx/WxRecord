package cn.imtianx.wxrecord.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cn.imtianx.wxrecord.R
import cn.imtianx.wxrecord.data.LaunchApp2Front
import cn.imtianx.wxrecord.data.UploadStatus
import cn.imtianx.wxrecord.service.UploadService
import cn.imtianx.wxrecord.util.CommonUtils
import cn.imtianx.wxrecord.util.Const
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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

    private var currentUploadStatus = "-1"

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_START_UPLOAD -> {
                    startService(Intent(this@MainActivity, UploadService::class.java))
                }
                MSG_REFRESH_LAST_MSG_ID -> {
                    tv_last_msg_id.text = SPUtils.getInstance().getString(Const.LAST_MSG_ID, "0")
                }
                MSG_LAUNCH_CUR_APP -> {
                    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                        .apply {
                            getRunningTasks(1000).forEach { taskInfo ->
                                if (taskInfo.topActivity.packageName == packageName) {
                                    moveTaskToFront(taskInfo.id, 0)
                                    return
                                }
                            }
                        }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWxAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sendMessage(MSG_START_UPLOAD, 1000L)
        sendMessage(MSG_REFRESH_LAST_MSG_ID)

        btn_check_root.setOnClickListener {
            ToastUtils.showShort("wx 运行状态：${CommonUtils.isAppRunning(this, Const.PKG_NAME_WX)}")
        }

        btn_reset_last_msg.setOnClickListener {
            SPUtils.getInstance().put(Const.LAST_MSG_ID, "0")
            sendMessage(MSG_REFRESH_LAST_MSG_ID)
            CommonUtils.clearLog()
        }

        if (!EventBus.getDefault().isRegistered(this@MainActivity)) {
            EventBus.getDefault().register(this@MainActivity)
        }

        registerWxAlarm()


        btn_control.setOnClickListener {
            when (currentUploadStatus) {
                "-1" -> {
                    currentUploadStatus = "1"
                }
                "0" -> {
                    currentUploadStatus = "1"
                }
                "1" -> {
                    currentUploadStatus = "0"
                }
            }
            SPUtils.getInstance().put(Const.UPLOAD_STATUS, currentUploadStatus)
            refreshUI(currentUploadStatus)
        }
    }

    private fun registerWxAlarm() {
        mWxAlarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000L, 60 * 1000L,
            PendingIntent.getBroadcast(
                this, 0,
                Intent(Const.ACTION_LAUNCH_WX_APP), 0
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reveiveNewMsg(event: Any) {
        when (event) {
            is LaunchApp2Front -> {
                sendMessage(MSG_LAUNCH_CUR_APP, 1000L)
            }
            is UploadStatus -> {
                refreshUI(event.status)
            }
        }
    }

    private fun refreshUI(status: String) {
        currentUploadStatus = status
        ll_upload.visibility = if (status == "1") {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        btn_control.text = when (status) {
            "0" -> {
                "开始上传"
            }
            "1" -> {
                "停止上传"
            }
            else -> {
                ToastUtils.showShort("开始检查数据~~")
                "暂无数据可上传"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.menu_main, it)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.menu_setting -> {
                    SettingActivity.launch(this@MainActivity)
                }

                R.id.menu_upload_log -> {
                    UploadLogActivity.launch(this@MainActivity)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        sendMessage(MSG_REFRESH_LAST_MSG_ID, 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MainActivity)
    }

    companion object {
        const val MSG_START_UPLOAD = 10001
        const val MSG_REFRESH_LAST_MSG_ID = 10002
        const val MSG_LAUNCH_CUR_APP = 10003
    }


}

