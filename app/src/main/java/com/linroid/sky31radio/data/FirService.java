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

package com.linroid.sky31radio.data;

import com.linroid.sky31radio.model.FirVersion;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by linroid on 15/2/14.
 */
public interface FirService {
    @GET("/app/version/{idOrAppid}")
    Observable<FirVersion> checkVersion(@Path("idOrAppid")String idOrAppid);
}
