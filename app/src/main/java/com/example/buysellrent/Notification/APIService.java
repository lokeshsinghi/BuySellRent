package com.example.buysellrent.Notification;

import com.example.buysellrent.Notification.Response;
import com.example.buysellrent.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAagxZTEU:APA91bHV3UAqSyONQ7hDLyAQchbDOEh7EltWkAWiNmd2WvGyRUWurusxsE9p1pmDO9lEESHgl9eID5BIiszRmOuaUIovqi-4SjykerrglVMBy6eBoNPSesatQ6x9zo0mivJwfRIskJnU"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
