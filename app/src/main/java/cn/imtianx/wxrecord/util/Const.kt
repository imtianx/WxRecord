package cn.imtianx.wxrecord.util

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName Const
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 3:06 PM
 */
object Const {

    const val PKG_NAME_WX = "com.tencent.mm"
    const val PATH_WX_ROOT = "/data/data/$PKG_NAME_WX/"
    const val PATH_WX_SP_UIN = "$PATH_WX_ROOT/shared_prefs/auth_info_key_prefs.xml"
    const val PATH_WX_DB = "$PATH_WX_ROOT/MicroMsg/"
    const val WX_DB_FILE_NAME = "EnMicroMsg.db"

    const val UPLOAD_USER_ID = "42"
    const val UPLOAD_WX_USER_NAME = "wxid_yplh5q7pp46t22"

    const val ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
    const val ACTION_LAUNCH_WX_APP = "cn.imtianx.wxrecord.launch_wx_app"


    const val LAST_MSG_ID = "last_wx_id"

    const val BASE_URL = "http://192.168.1.54:9001/api/"
    const val API_UPLOAD_WX_MSG = "ins/web/invoice/info/saveInvoiceWechat"

    const val UPLOAD_STATUS = "upload_status"

}