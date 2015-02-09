package com.linroid.sky31radio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linroid on 1/15/15.
 */


public class Anchor implements Parcelable {

    @Expose
    private int id;
    @Expose
    private String nickname;
    @Expose
    private String email;
    @Expose
    private String role;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String avatar;
    @SerializedName("program_count")
    @Expose
    private int programCount;

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role The role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return The programCount
     */
    public int getProgramCount() {
        return programCount;
    }

    /**
     * @param programCount The program_count
     */
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
        dest.writeString(this.nickname);
        dest.writeString(this.email);
        dest.writeString(this.role);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.avatar);
        dest.writeInt(this.programCount);
    }

    public Anchor() {
    }

    private Anchor(Parcel in) {
        this.id = in.readInt();
        this.nickname = in.readString();
        this.email = in.readString();
        this.role = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.avatar = in.readString();
        this.programCount = in.readInt();
    }

    public static final Parcelable.Creator<Anchor> CREATOR = new Parcelable.Creator<Anchor>() {
        public Anchor createFromParcel(Parcel source) {
            return new Anchor(source);
        }

        public Anchor[] newArray(int size) {
            return new Anchor[size];
        }
    };
}