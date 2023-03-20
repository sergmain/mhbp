/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ai.metaheuristic.mhbp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:28 PM
 */
@Slf4j
public class YamlUtils {

    // https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-threading
    // The implementation of Yaml is not thread-safe.
    // Different threads may not call the same instance.
    // Threads must have separate Yaml instances.

    public static Yaml init(Class<?> clazz) {
        return initWithTags(clazz, new Class[]{clazz}, null);
    }

    public static Yaml initWithTags(Class<?> clazz, Class<?>[] clazzMap, @Nullable TypeDescription customTypeDescription) {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Representer representer = new Representer() {

            @Override
            @Nullable
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                // if value of property is null, ignore it.
                if (propertyValue == null) {
                    return null;
                }
                else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        if (clazzMap!=null) {
            for (Class<?> clazzTag : clazzMap) {
                representer.addClassTag(clazzTag, Tag.MAP);
            }
        }

        Constructor constructor = new Constructor(clazz);
        if (customTypeDescription!=null) {
            constructor.addTypeDescription(customTypeDescription);
        }

        Yaml yaml = new Yaml(constructor, representer, options);
        return yaml;
    }

    public static String toString(Object obj, Yaml yaml) {
        return yaml.dump(obj);
    }

    public static Object to(String s, Yaml yaml) {
        if (S.b(s)) {
            throw new IllegalArgumentException("'yaml' parameter is blank");
        }
        return yaml.load(s);
    }

    @Nullable
    public static Object toNullable(@Nullable String s, Yaml yaml) {
        if (S.b(s)) {
            return null;
        }
        return yaml.load(s);
    }

    public static Object to(InputStream is, Yaml yaml) {
        return yaml.load(is);
    }

    public static Object to(File file, Yaml yaml) {
        try(FileInputStream fis =  new FileInputStream(file)) {
            return yaml.load(fis);
        } catch (IOException e) {
            log.error("Error", e);
            throw new IllegalStateException("Error while loading file: " + file.getPath(), e);
        }
    }
}
