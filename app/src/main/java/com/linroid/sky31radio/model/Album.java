
package com.linroid.sky31radio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Album implements Parcelable {

    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String type;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String cover;
    @Expose
    @SerializedName("program_count")
    private int programCount;

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
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
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

    public int getProgramCount() {
        return programCount;
    }

    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.cover);
        dest.writeInt(this.programCount);
    }

    public Album() {
    }

    private Album(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.type = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.cover = in.readString();
        this.programCount = in.readInt();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
