package com.example.plant_id.nfc

import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build

// NFC 标签读取工具对象
// 职责：从 Android NFC Intent / Tag 对象解析标签 ID，并向标签写入 NDEF 数据
object NfcReader {

    // 写入标签时使用的 URI scheme / host，便于集中维护
    private const val URI_SCHEME = "plantid"
    private const val URI_HOST = "nfc"

    // NFC 相关的 Intent action 集合
    private val NFC_ACTIONS = setOf(
        NfcAdapter.ACTION_NDEF_DISCOVERED,
        NfcAdapter.ACTION_TAG_DISCOVERED,
        NfcAdapter.ACTION_TECH_DISCOVERED
    )

    // 判断给定 Intent 是否是 NFC 触发的
    fun isNfcIntent(intent: Intent): Boolean {
        return intent.action in NFC_ACTIONS
    }

    /**
     * 从 NFC Intent 中提取标签 ID（冷启动 / onNewIntent 路径使用）
     * 返回大写十六进制字符串，如 "A4B2C3D4"
     *
     * 读取策略（双保险）：
     *  1. 优先从 intent.data URI 读取（"plantid://nfc/<UID>"）
     *     → 纯字符串操作，无 Parcelable，在所有 Android 版本 / OEM ROM 上100%可靠
     *  2. 兜底从 EXTRA_TAG 读取（旧版可能因 ClassLoader 不一致返回 null）
     *     → Android 13+ 使用带类型参数的新版 API
     */
    fun readTagId(intent: Intent): String? {
        if (!isNfcIntent(intent)) return null

        // 方式一：从 URI 记录读取（写入时主动放进去的，可靠）
        val uri = intent.data
        if (uri?.scheme == URI_SCHEME && uri.host == URI_HOST) {
            val tagId = uri.lastPathSegment
            if (!tagId.isNullOrEmpty()) return tagId
        }

        // 方式二：从 EXTRA_TAG 读取（Android 13+ 使用新 API 防 ClassLoader 问题）
        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
        return tag?.id?.toHexString()
    }

    // 直接从 Tag 对象读取 ID（enableReaderMode 回调路径使用）
    // 返回大写十六进制字符串，如 "A4B2C3D4"
    fun readTagId(tag: Tag): String = tag.id.toHexString()

    /**
     * 向标签写入 NDEF 数据：URI 记录 + AAR
     *
     * URI 记录：plantid://nfc/<UID>
     *   → 冷启动时 Android 将此 URI 放入 intent.data，可直接读取 UID，无 Parcelable 问题
     *
     * AAR 记录：Android Application Record（包名）
     *   → 任何手机扫此标签都直接打开本 App，无需系统选择框
     *
     * 写入时机：enableReaderMode 后台回调中静默执行，失败不影响主流程。
     * 若标签已有 URI+AAR 双记录，跳过（避免不必要写入）。
     */
    fun writeAar(tag: Tag, packageName: String): Boolean {
        val tagId = readTagId(tag)
        val uriRecord = NdefRecord.createUri(Uri.parse("$URI_SCHEME://$URI_HOST/$tagId"))
        val aarRecord = NdefRecord.createApplicationRecord(packageName)
        val ndefMessage = NdefMessage(arrayOf(uriRecord, aarRecord))

        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                try {
                    val existing = ndef.ndefMessage
                    // 已有 URI+AAR 双记录（新格式），跳过
                    if (existing != null &&
                        existing.records.size >= 2 &&
                        hasOurAar(existing, packageName)
                    ) return true
                    if (!ndef.isWritable) return false
                    ndef.writeNdefMessage(ndefMessage)
                    true
                } finally {
                    runCatching { ndef.close() }
                }
            } else {
                // 空白标签：先格式化再写入
                val formatable = NdefFormatable.get(tag) ?: return false
                formatable.connect()
                try {
                    formatable.format(ndefMessage)
                    true
                } finally {
                    runCatching { formatable.close() }
                }
            }
        } catch (e: Exception) {
            false   // 标签移走或 IO 错误，静默降级
        }
    }

    // 检查 NDEF 消息中是否已包含指向指定包名的 AAR
    private fun hasOurAar(message: NdefMessage, packageName: String): Boolean =
        message.records.any { record ->
            record.tnf == NdefRecord.TNF_EXTERNAL_TYPE &&
                    "android.com:pkg" == String(record.type, Charsets.US_ASCII) &&
                    packageName == String(record.payload, Charsets.UTF_8)
        }

    // 将 ByteArray 转换为大写十六进制字符串
    private fun ByteArray.toHexString(): String =
        joinToString("") { byte -> "%02X".format(byte) }
}
