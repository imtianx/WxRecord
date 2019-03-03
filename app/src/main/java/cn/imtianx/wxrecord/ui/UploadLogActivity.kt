package cn.imtianx.wxrecord.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import cn.imtianx.wxrecord.R
import cn.imtianx.wxrecord.util.CommonUtils
import kotlinx.android.synthetic.main.activity_log.*

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName UploadLogActivity
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/3 9:22 PM
 */
class UploadLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        toolbar.let {
            setSupportActionBar(it)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }
        tv_log.text = CommonUtils.readLog()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                android.R.id.home -> {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, UploadLogActivity::class.java))
        }
    }
}