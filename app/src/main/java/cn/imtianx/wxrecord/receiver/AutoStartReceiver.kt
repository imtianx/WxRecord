package cn.imtianx.wxrecord.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.imtianx.wxrecord.MainActivity
import cn.imtianx.wxrecord.util.Const

/**
 * <pre>
 *     @desc: aoto start
 * </pre>
 * @fileName AutoStartReceiver
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 3:33 PM
 */
class AutoStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Const.ACTION_BOOT_COMPLETED) {
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}