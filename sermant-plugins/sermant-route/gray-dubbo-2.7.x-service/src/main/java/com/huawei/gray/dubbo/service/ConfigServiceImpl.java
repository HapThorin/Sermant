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

package com.huawei.gray.dubbo.service;

import com.huawei.gray.dubbo.cache.DubboCache;
import com.huawei.sermant.core.plugin.config.PluginConfigManager;
import com.huawei.sermant.core.plugin.service.PluginService;
import com.huawei.sermant.core.service.ServiceManager;
import com.huawei.sermant.core.service.dynamicconfig.DynamicConfigurationFactoryServiceImpl;
import com.huawei.sermant.core.service.dynamicconfig.service.DynamicConfigurationService;
import com.huawei.route.common.gray.config.GrayConfig;
import com.huawei.route.common.gray.listener.GrayConfigListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 配置服务
 *
 * @author pengyuyi
 * @date 2021/11/24
 */
public class ConfigServiceImpl implements PluginService {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final DynamicConfigurationFactoryServiceImpl dynamicConfigurationFactoryService =
            ServiceManager.getService(DynamicConfigurationFactoryServiceImpl.class);

    private GrayConfig grayConfig;

    private GrayConfigListener listener;

    private DynamicConfigurationService configurationService;

    /**
     * 初始化通知
     */
    @Override
    public void start() {
        grayConfig = PluginConfigManager.getPluginConfig(GrayConfig.class);
        configurationService = dynamicConfigurationFactoryService.getDynamicConfigurationService();
        // 此块只会适配KIE配置中心
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                initRequests();
            }
        });
    }

    private void initRequests() {
        listener = new GrayConfigListener(DubboCache.getLabelName(), grayConfig.getDubboKey());
        configurationService.addGroupListener(grayConfig.getDubboGroup(), listener);
    }

    @Override
    public void stop() {
        configurationService.removeGroupListener(grayConfig.getDubboGroup(), grayConfig.getDubboGroup(), listener);
    }
}