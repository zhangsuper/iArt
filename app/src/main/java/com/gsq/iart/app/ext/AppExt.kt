package com.gsq.iart.app.ext

import java.util.regex.Pattern


/**
 * 数据流标准范围值验证
 * 是|正常
 * 校准已经运行|校准已完成|校准已经运行，校准已完成
 * [1,100]
 * (1,100)
 * {>=50,<=30}
 **/
fun String.checkRange(range: String): Boolean {
    if (range.startsWith('[') || range.startsWith('(')) {
        var subRange = range.substring(1, range.length - 1)
        var splitRange = subRange.split(',')
        var range1 = splitRange[0].toFloat()
        var range2 = splitRange[1].toFloat()
        if (range.startsWith('[')) {
            if (this.toFloat() < range1) {
                return false
            }
        } else if (range.startsWith('(')) {
            if (this.toFloat() <= range1) {
                return false
            }
        }
        if (range.endsWith(']')) {
            if (this.toFloat() > range2) {
                return false
            }
        } else if (range.endsWith(')')) {
            if (this.toFloat() >= range2) {
                return false
            }
        }
        return true
    } else if (range.startsWith('{') && range.endsWith('}')) {
        //{>=50,>50,=50,<30,<=30,}
        var subRange = range.substring(1, range.length - 1)
        var splitRange = subRange.split(',')
        splitRange.forEach {
            if (it.contains(">=")) {
                if (this.toFloat() >= it.replace(">=", "").toFloat()) {
                    return true
                }
            }
            if (it.contains("<=")) {
                if (this.toFloat() <= it.replace("<=", "").toFloat()) {
                    return true
                }
            }
            if (it.contains(">") && !it.contains("=")) {
                if (this.toFloat() > it.replace(">", "").toFloat()) {
                    return true
                }
            }
            if (it.contains("<") && !it.contains("=")) {
                if (this.toFloat() < it.replace("<", "").toFloat()) {
                    return true
                }
            }
            if (it.contains("=") && !it.contains(">") && !it.contains("<")) {
                if (this.toFloat() == it.replace("=", "").toFloat()) {
                    return true
                }
            }
        }
        return false
    } else {
        var listRange = range.split('|')
        return listRange.contains(this)
    }
    return true
}

fun String.isInteger(): Boolean {
    if (this.isNullOrEmpty()) {
        return false
    }
    var pattern = Pattern.compile("^[-\\+]?[\\d]*$")
    return pattern.matcher(this).matches()
}

fun String.isDouble(): Boolean {
    if (this.isNullOrEmpty()) {
        return false
    }
    val pattern = Pattern.compile("^[-\\+]?\\d*[.]\\d+\$")
    return pattern.matcher(this).matches()
}