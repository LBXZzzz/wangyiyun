package com.example.musicmelody;

import java.io.Serializable;

public class SongItem implements Serializable {
    private String singerName;
    private String songName;
    private String songId;
    private String picUrl;

    public SongItem(String singerName, String songName, String songId, String picUrl) {
        this.singerName = singerName;
        this.songName = songName;
        this.songId = songId;
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
