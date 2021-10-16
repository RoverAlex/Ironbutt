package com.dw.ironButt.database.api


import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    companion object {
        //methods
        const val  COOKIE = "auth/generate_auth_cookie"

        //params
        const val  CHECK_RELEASE = "check-token.php"
        const val  SET_REQUEST = "set-request.php"
    }

    @GET(CHECK_RELEASE)
    fun checkToken(
        @Query("token")token:String
    ):Call<CheckTokenJson>

    @GET(COOKIE)
    fun authCookie(
        @Query("username")name :String,
        @Query("password") password:String
    ): Call<AuthLoginJson>

    @POST(SET_REQUEST)
    @FormUrlEncoded
    fun requestUserList(
        @Field("token") token:String,
        @Field("userList") userList:String
    ):Call<ServerResponse>

    @POST(SET_REQUEST)
    @FormUrlEncoded
    fun requestPointList(
        @Field("token") token:String,
        @Field("points") userList:String
    ):Call<ServerResponse>

    @Multipart
    @POST(SET_REQUEST)
    fun uploadImages(
        @Part("token") token:String,
        @Part("unIRout") unIRout:String,
        @Part images: List<MultipartBody.Part>
    ): Call<ServerResponse>

}


