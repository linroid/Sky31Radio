
package com.linroid.radio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Audio implements Parcelable {

    @Expose
    private int id;
    @SerializedName("program_id")
    @Expose
    private String programId;
    @Expose
    private String size;
    @Expose
    private String duration;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String src;

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
     *     The programId
     */
    public String getProgramId() {
        return programId;
    }

    /**
     * 
     * @param programId
     *     The program_id
     */
    public void setProgramId(String programId) {
        this.programId = programId;
    }

    /**
     * 
     * @return
     *     The size
     */
    public String getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
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
     *     The src
     */
    public String getSrc() {
        return src;
    }

    /**
     * 
     * @param src
     *     The src
     */
    public void setSrc(String src) {
        this.src = src;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.programId);
        dest.writeString(this.size);
        dest.writeString(this.duration);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.src);
    }

    public Audio() {
    }

    private Audio(Parcel in) {
        this.id = in.readInt();
        this.programId = in.readString();
        this.size = in.readString();
        this.duration = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.src = in.readString();
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
}
