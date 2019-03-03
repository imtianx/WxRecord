package cn.imtianx.wxrecord.ui

import android.app.Application
import com.blankj.utilcode.util.Utils
import org.litepal.LitePal

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName App
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 3:41 PM
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        Utils.init(this)
    }
}