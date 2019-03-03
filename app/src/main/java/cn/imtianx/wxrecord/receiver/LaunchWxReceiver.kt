package cn.imtianx.wxrecord.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.imtianx.wxrecord.LaunchApp2Front
import cn.imtianx.wxrecord.util.Const
import cn.imtianx.wxrecord.util.log
import org.greenrobot.eventbus.EventBus

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName LaunchWxReceiver
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/3 7:44 PM
 */
class LaunchWxReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Const.ACTION_LAUNCH_WX_APP) {
            context.let {
                it.startActivity(it.packageManager.getLaunchIntentForPackage(Const.PKG_NAME_WX))
                EventBus.getDefault().post(LaunchApp2Front())
                log("重启微信----------")
            }
        }
    }
}