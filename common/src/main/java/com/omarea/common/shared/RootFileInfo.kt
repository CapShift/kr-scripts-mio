package com.omarea.common.shared

import com.omarea.common.shell.RootFile

class RootFileInfo {

    var parentDir: String = ""
    var filePath: String = ""
    var isDirectory: Boolean = false
    var fileSize: Long = 0

    private val fileName: String
        get() {
            if (filePath.endsWith("/")) {
                return filePath.substring(0, filePath.length - 1)
            }
            return filePath
        }

    private val absolutePath: String
        get() = "$parentDir/$fileName"


    fun exists(): Boolean {
        return RootFile.itemExists(this.absolutePath)
    }

    fun length(): Long {
        return this.fileSize
    }
}
