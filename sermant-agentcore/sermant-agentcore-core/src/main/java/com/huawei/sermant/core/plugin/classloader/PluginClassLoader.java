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

package com.huawei.sermant.core.plugin.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件类加载器，用于加载插件服务包
 *
 * @author HapThorin
 * @version 1.0.0
 * @since 2021/11/12
 */
public class PluginClassLoader extends URLClassLoader {
    /**
     * 对ClassLoader内部已加载的Class的管理
     */
    private final Map<String, Class<?>> pluginClassMap = new HashMap<>();

    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    /**
     * 自己实现一套findLoaded再find的逻辑，避免优先查找AppClassLoader的类
     *
     * @param name 全限定名
     * @return Class对象
     */
    private Class<?> getPluginClass(String name) {
        if (!pluginClassMap.containsKey(name)) {
            try {
                pluginClassMap.put(name, findClass(name));
            } catch (ClassNotFoundException ignored) {
                pluginClassMap.put(name, null);
            }
        }
        return pluginClassMap.get(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = getPluginClass(name);
            if (clazz == null) {
                clazz = super.loadClass(name, resolve);
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }
}
