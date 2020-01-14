package com.dmplayer.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SongResponse {

    @SerializedName("music")
    @Expose
    private ArrayList<SongItem> music = null;

    public ArrayList<SongItem> getMusic() {
        return music;
    }

    public void setMusic(ArrayList<SongItem> music) {
        this.music = music;
    }
}
