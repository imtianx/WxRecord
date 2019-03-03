package cn.imtianx.wxrecord.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.imtianx.wxrecord.service.UploadService
import cn.imtianx.wxrecord.util.Const
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName CopyWxDbReceiver
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 4:16 PM
 */
class CopyWxDbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("tx", "检测 拷贝 wx db:------------------ :${TimeUtils.getNowString()}")
        if (SPUtils.getInstance().getBoolean(Const.IS_NEED_COPY_WX_DB, true)) {
            ToastUtils.showShort("开始拷贝 wx db")
            context.startService(Intent(context, UploadService::class.java))
        }
    }
}