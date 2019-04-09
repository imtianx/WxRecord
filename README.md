# WxRecord 获取上传指定用户微信聊天记录

每隔2 min 轮询检测微信本地数据库，上传指定用户指定类型聊天记录，参考：[https://github.com/imtianx/WeChatRecordApp](https://github.com/imtianx/WeChatRecordApp)

> 注意：该方案需要 root 手机，此外可以使用 `xposed` 框架来 hook wechat ,然后获取聊天记录（测试了 [WechatSpellbook](https://github.com/Gh0u1L5/WechatSpellbook) 框架，只能 hook 新消息的插入，无法 hook 查询，存在消息的遗漏）。


## 实现思路

从 wechat 目录 `/data/data/com.tencent.mm/MicroMsg` 下拷贝聊天记录数据库文件 **`EnMicroMsg.db`,数据库密码为：MD5(IMEI 1+UIN).Substring(0, 7).toLower ,其中 uin 在 sp 文件 `/data/data/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml` 中保存着** ,然后解析读取数据库数据并上传，记录最后上传位置，轮询上述操作。

可以使用 [sqlcipher](/tools/wxsqlcipher.exe) 工具查看数据库内容。

参考文章：[微信数据库最新的解密方式，使用C++代码解密微信加密数据库信息](https://www.52pojie.cn/forum.php?mod=viewthread&tid=778496&extra=page%3D1%26filter%3Dauthor%26orderby%3Ddateline)


对于未完全 root 的 手机无法显示 `data` 目录，可以使用下面命令进行授权：

```
adb shell su -c "chmod 777 /data"
```

