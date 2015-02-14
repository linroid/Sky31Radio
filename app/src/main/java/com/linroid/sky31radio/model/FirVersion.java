/*
 *
 *  * Copyright (c) linroid 2015.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.linroid.sky31radio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by linroid on 15/2/14.
 */

public class FirVersion {

    @Expose
    private String name;
    @Expose
    private Integer version;
    @Expose
    private String versionShort;
    @Expose
    private String installUrl;
    @SerializedName("update_url")
    @Expose
    private String updateUrl;

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The versionShort
     */
    public String getVersionShort() {
        return versionShort;
    }

    /**
     *
     * @param versionShort
     * The versionShort
     */
    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    /**
     *
     * @return
     * The installUrl
     */
    public String getInstallUrl() {
        return installUrl;
    }

    /**
     *
     * @param installUrl
     * The installUrl
     */
    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    /**
     *
     * @return
     * The updateUrl
     */
    public String getUpdateUrl() {
        return updateUrl;
    }

    /**
     *
     * @param updateUrl
     * The update_url
     */
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

}