/*
 * Copyright (C) 2021-2021 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.sermant.core.lubanops.integration.transport.http;

/**
 * @author
 * @date 2020/8/7 17:49
 */
public abstract class AbstractHttpSinger extends AbstractSigner {

    String getHeader(HttpRequest request, String header) {
        if (header == null) {
            return null;
        } else {
            return request.getHeaders().get(header);
        }
    }

}
