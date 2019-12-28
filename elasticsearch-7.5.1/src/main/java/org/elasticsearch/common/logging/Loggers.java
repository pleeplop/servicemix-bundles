/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.common.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.shard.ShardId;

import static org.elasticsearch.common.util.CollectionUtils.asArrayList;

/**
 * A set of utilities around Logging.
 */
public class Loggers {

    public static final String SPACE = " ";

    public static final Setting<Level> LOG_DEFAULT_LEVEL_SETTING =
            new Setting<>("logger.level", Level.INFO.name(), Level::valueOf, Setting.Property.NodeScope);
    public static final Setting.AffixSetting<Level> LOG_LEVEL_SETTING =
            Setting.prefixKeySetting("logger.", (key) -> new Setting<>(key, Level.INFO.name(), Level::valueOf, Setting.Property.Dynamic,
                    Setting.Property.NodeScope));

    public static Logger getLogger(Class<?> clazz, ShardId shardId, String... prefixes) {
        return getLogger(clazz, shardId.getIndex(), asArrayList(Integer.toString(shardId.id()), prefixes).toArray(new String[0]));
    }

    /**
     * Just like {@link #getLogger(Class, ShardId, String...)} but String loggerName instead of
     * Class and no extra prefixes.
     */
    public static Logger getLogger(String loggerName, ShardId shardId) {
        String prefix = formatPrefix(shardId.getIndexName(), Integer.toString(shardId.id()));
        return new PrefixLogger(LogManager.getLogger(loggerName), prefix);
    }

    public static Logger getLogger(Class<?> clazz, Index index, String... prefixes) {
        return getLogger(clazz, asArrayList(Loggers.SPACE, index.getName(), prefixes).toArray(new String[0]));
    }

    public static Logger getLogger(Class<?> clazz, String... prefixes) {
        return new PrefixLogger(LogManager.getLogger(clazz), formatPrefix(prefixes));
    }

    public static Logger getLogger(Logger parentLogger, String s) {
        Logger inner = LogManager.getLogger(parentLogger.getName() + s);
        if (parentLogger instanceof PrefixLogger) {
            return new PrefixLogger(inner, ((PrefixLogger)parentLogger).prefix());
        }
        return inner;
    }

    private static String formatPrefix(String... prefixes) {
        String prefix = null;
        if (prefixes != null && prefixes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String prefixX : prefixes) {
                if (prefixX != null) {
                    if (prefixX.equals(SPACE)) {
                        sb.append(" ");
                    } else {
                        sb.append("[").append(prefixX).append("]");
                    }
                }
            }
            if (sb.length() > 0) {
                prefix = sb.toString();
            }
        }
        return prefix;
    }

    /**
     * Set the level of the logger. If the new level is null, the logger will inherit it's level from its nearest ancestor with a non-null
     * level.
     */
    public static void setLevel(Logger logger, String level) {
        final Level l;
        if (level == null) {
            l = null;
        } else {
            l = Level.valueOf(level);
        }
        setLevel(logger, l);
    }

    public static void setLevel(Logger logger, Level level) {
        // nothing to do
    }

    public static void addAppender(final Logger logger, final Appender appender) {
        // nothing to do
    }

    public static void removeAppender(final Logger logger, final Appender appender) {
        // nothing to do
    }

    public static Appender findAppender(final Logger logger, final Class<? extends Appender> clazz) {
        // nothing to do
        return null;
    }

}