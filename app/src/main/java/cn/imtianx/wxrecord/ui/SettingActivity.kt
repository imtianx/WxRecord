package cn.imtianx.wxrecord.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import cn.imtianx.wxrecord.R
import kotlinx.android.synthetic.main.activity_log.*

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName SettingActivity
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/4 11:23 AM
 */
class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        toolbar.let {
            setSupportActionBar(it)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }
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
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}