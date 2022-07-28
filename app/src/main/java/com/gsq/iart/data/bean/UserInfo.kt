package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class UserInfo(var id: String,var name: String,var avatarUrl: String): Serializable {
}