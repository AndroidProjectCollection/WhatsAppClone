package com.febrian.whatsappclone.api

import com.febrian.whatsappclone.data.PushNotification
import com.febrian.whatsappclone.utils.Constant.CONTENT_TYPE
import com.febrian.whatsappclone.utils.Constant.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiNotification {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}