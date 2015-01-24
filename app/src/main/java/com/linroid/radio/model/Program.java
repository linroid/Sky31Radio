
package com.linroid.radio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Program implements Parcelable {

    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("album_id")
    @Expose
    private String albumId;
    @Expose
    private String title;
    @Expose
    private String author;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String cover;
    @Expose
    private String thumbnail;
    @Expose
    private String article;
    @Expose
    private String background;
    @SerializedName("total_play")
    @Expose
    private int totalPlay;
    @Expose
    private Audio audio;
    @Expose
    private Album album;

    /**
     * 
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The albumId
     */
    public String getAlbumId() {
        return albumId;
    }

    /**
     * 
     * @param albumId
     *     The album_id
     */
    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 
     * @param author
     *     The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The cover
     */
    public String getCover() {
        return cover;
    }

    /**
     * 
     * @param cover
     *     The cover
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 
     * @return
     *     The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * 
     * @param thumbnail
     *     The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * 
     * @return
     *     The background
     */
    public String getBackground() {
        return background;
    }

    /**
     * 
     * @param background
     *     The background
     */
    public void setBackground(String background) {
        this.background = background;
    }

    /**
     * 
     * @return
     *     The totalPlay
     */
    public int getTotalPlay() {
        return totalPlay;
    }

    /**
     * 
     * @param totalPlay
     *     The total_play
     */
    public void setTotalPlay(int totalPlay) {
        this.totalPlay = totalPlay;
    }

    /**
     * 
     * @return
     *     The audio
     */
    public Audio getAudio() {
        return audio;
    }

    /**
     * 
     * @param audio
     *     The audio
     */
    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    /**
     * 
     * @return
     *     The album
     */
    public Album getAlbum() {
        return album;
    }

    /**
     * 
     * @param album
     *     The album
     */
    public void setAlbum(Album album) {
        this.album = album;
    }


    public Program() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.albumId);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.cover);
        dest.writeString(this.thumbnail);
        dest.writeString(this.article);
        dest.writeString(this.background);
        dest.writeInt(this.totalPlay);
        dest.writeParcelable(this.audio, 0);
        dest.writeParcelable(this.album, 0);
    }

    private Program(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readString();
        this.albumId = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.cover = in.readString();
        this.thumbnail = in.readString();
        this.article = in.readString();
        this.background = in.readString();
        this.totalPlay = in.readInt();
        this.audio = in.readParcelable(Audio.class.getClassLoader());
        this.album = in.readParcelable(Album.class.getClassLoader());
    }

    public static final Creator<Program> CREATOR = new Creator<Program>() {
        public Program createFromParcel(Parcel source) {
            return new Program(source);
        }

        public Program[] newArray(int size) {
            return new Program[size];
        }
    };
}
