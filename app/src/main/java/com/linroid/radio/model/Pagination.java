
package com.linroid.radio.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Pagination<T extends Parcelable> {

    @Expose
    private int total;
    @SerializedName("per_page")
    @Expose
    private int perPage;
    @SerializedName("current_page")
    @Expose
    private int currentPage;
    @SerializedName("last_page")
    @Expose
    private int lastPage;
    @Expose
    private int from;
    @Expose
    private int to;
    @Expose
    private List<T> data = new ArrayList<T>();

    /**
     * 
     * @return
     *     The total
     */
    public int getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The perPage
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * 
     * @param perPage
     *     The per_page
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    /**
     * 
     * @return
     *     The currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 
     * @param currentPage
     *     The current_page
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 
     * @return
     *     The lastPage
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * 
     * @param lastPage
     *     The last_page
     */
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    /**
     * 
     * @return
     *     The from
     */
    public int getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * 
     * @return
     *     The to
     */
    public int getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    public void setTo(int to) {
        this.to = to;
    }

    /**
     * 
     * @return
     *     The data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(List<T> data) {
        this.data = data;
    }

}
