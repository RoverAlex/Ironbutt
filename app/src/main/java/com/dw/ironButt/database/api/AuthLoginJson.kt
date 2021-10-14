package com.denisovdw.ironbutt

import com.google.gson.annotations.SerializedName

data class CheckTokenJson(
	@field:SerializedName("token")
	val token: String = "",

	@field:SerializedName("message")
	val message: String = "",
)


data class AuthLoginJson(

	@field:SerializedName("cookie")
	val cookie: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("cookie_name")
	val cookieName: String? = null
)

data class User(
	@field:SerializedName("firstname")
	val firstname: String = "",

	@field:SerializedName("capabilities")
	val capabilities: String = "",

	@field:SerializedName("registered")
	val registered: String = "",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("nicename")
	val nicename: String = "",

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("lastname")
	val lastname: String = "",

	@field:SerializedName("displayname")
	val displayname: String = "",

	@field:SerializedName("nickname")
	val nickname: String = "",

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String = ""
)

data class ServerResponse (
	@field:SerializedName("token")
	val token: String = "",
	@field:SerializedName("success")
	var success: String = ""
		)
