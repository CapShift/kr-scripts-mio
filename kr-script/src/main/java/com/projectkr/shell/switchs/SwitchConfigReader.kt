package com.projectkr.shell.switchs

import android.content.Context
import android.util.Log
import android.util.Xml
import android.widget.Toast
import com.projectkr.shell.ScriptEnvironmen
import com.projectkr.shell.utils.ExtractAssets
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.util.*

/**
 * Created by Hello on 2018/04/01.
 */

object SwitchConfigReader {
    private const val ASSETS_FILE = "file:///android_asset/"

    private fun getConfig(context: Context): InputStream? {
        try {
            return context.assets.open("switchs.xml")
        } catch (ex: Exception) {
            return null
        }

    }

    fun readActionConfigXml(context: Context): ArrayList<SwitchInfo>? {
        try {
            val fileInputStream = getConfig(context) ?: return ArrayList()
            val parser = Xml.newPullParser()// 获取xml解析器
            parser.setInput(fileInputStream, "utf-8")// 参数分别为输入流和字符编码
            var type = parser.eventType
            var actions: ArrayList<SwitchInfo>? = null
            var action: SwitchInfo? = null
            while (type != XmlPullParser.END_DOCUMENT) {// 如果事件不等于文档结束事件就继续循环
                when (type) {
                    XmlPullParser.START_TAG -> if ("switchs" == parser.name) {
                        actions = ArrayList()
                    }
                    else if ("separator" == parser.name) {
                        if (actions != null) {
                            val separator = SwitchInfo()
                            separator.separator = parser.nextText()
                            actions.add(separator)
                        }
                    }
                    else if ("group" == parser.name) {
                        if (actions != null) {
                            for (i in 0 until parser.attributeCount) {
                                val attrName = parser.getAttributeName(i)
                                if (attrName == "title") {
                                    val separator = SwitchInfo()
                                    separator.separator = parser.getAttributeValue(i)
                                    actions.add(separator)
                                    break
                                }
                            }
                        }
                    }
                    else if ("switch" == parser.name) {
                        action = SwitchInfo()
                        for (i in 0 until parser.attributeCount) {
                            if (action == null) {
                                break
                            }
                            when (parser.getAttributeName(i)) {
                                "root" -> {
                                    action.root = parser.getAttributeValue(i) == "true"
                                }
                                "confirm" -> {
                                    action.confirm = parser.getAttributeValue(i) == "true"
                                }
                                "start" -> {
                                    action.start = parser.getAttributeValue(i)
                                }
                                "support" -> {
                                    if (executeResultRoot(context, parser.getAttributeValue(i)) != "1") {
                                        action = null
                                    }
                                }
                            }
                        }
                    } else if ("resource" == parser.name) {
                        for (i in 0 until parser.attributeCount) {
                            if (parser.getAttributeName(i) == "file") {
                                val file = parser.getAttributeValue(i).trim()
                                if (file.startsWith(ASSETS_FILE)) {
                                    ExtractAssets(context).extractResource(file)
                                }
                            }
                        }
                    }
                    else if (action != null) {
                        if ("title" == parser.name) {
                            action.title = parser.nextText()
                        } else if ("desc" == parser.name) {
                            for (i in 0 until parser.attributeCount) {
                                if (parser.getAttributeName(i) == "su" || parser.getAttributeName(i) == "sh") {
                                    val attrValue = parser.getAttributeValue(i)
                                    action.descPollingShell = attrValue
                                    action.desc = executeResultRoot(context, action.descPollingShell)
                                }
                            }
                            if (action.desc == null || action.desc.isEmpty())
                                action.desc = parser.nextText()
                        } else if ("getstate" == parser.name) {
                            val script = parser.nextText()
                            action.getState = script
                        } else if ("setstate" == parser.name) {
                            val script = parser.nextText()
                            action.setState = script
                        }
                    }
                    XmlPullParser.END_TAG -> if ("switch" == parser.name && actions != null && action != null) {
                        if (action.title == null) {
                            action.title = ""
                        }
                        if (action.desc == null) {
                            action.desc = ""
                        }
                        if (action.getState == null) {
                            action.getState = ""
                        } else {
                            val shellResult = executeResultRoot(context, action.getState)
                            action.selected = shellResult != "error" && (shellResult == "1" || shellResult.toLowerCase() == "true")
                        }
                        if (action.setState == null) {
                            action.setState = ""
                        }

                        actions.add(action)
                        action = null
                    }
                }
                type = parser.next()// 继续下一个事件
            }

            return actions
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            Log.d("VTools ReadConfig Fail！", ex.message)
        }

        return null
    }

    private fun executeResultRoot(context: Context, scriptIn: String): String {
        return ScriptEnvironmen.executeResultRoot(context, scriptIn);
    }
}
