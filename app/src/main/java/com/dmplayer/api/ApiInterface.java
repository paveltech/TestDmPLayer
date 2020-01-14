package com.dmplayer.api;



import com.dmplayer.pojo.SongResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("music.json")
    Call<SongResponse> getSongs();

}
