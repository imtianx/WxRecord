package cn.imtianx.wxrecord.data

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * <pre>
 *     @desc:
 * </pre>
 * @fileName Entry
 * @author 奚岩
 * @email imtianx@gmail.com
 * @date 2019/3/2 3:21 PM
 */
class ChatRoom : DataSupport() {
    var name = ""                // 微信群名，类似id
    var memberList = ""          // 会员微信号列表
    var roomOwner = ""           // 群主
    var selfDisplayName = ""     // 我在本群昵称
    var modifyTime = 0L          // 修改时间
    var isModify = 0             // 是否修改 0：修改 1：无修改
}

class Contact : DataSupport(), Serializable {
    var username = ""
    var nickname = ""
    var type = ""
    var conRemark = ""
}

class Message : DataSupport() {
    var msgSvrId = ""
    var type = ""
    var isSend = ""           // 1：发送   0：接收   2：系统消息
    var createTime = ""
    var talker = ""
    var content = ""
    var imgPath = ""
}

data class UserInfo(
    val username: String,  // 账号
    val nickname: String   // 昵称
) : Serializable

class WeChatFile : DataSupport() {
    var msgSvrId = ""
    var type = ""
    var path = ""
    var name = ""
    var date = 0L
}


// event msg
class LaunchApp2Front

class RefreshMsgId

class UploadStatus(
    var status: String // -1暂无数据，0停止，1正在上传
)


