/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.shaded.jul;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

public class Logger {

    private final org.slf4j.Logger logger;

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    private Logger(String name) {
        this(org.slf4j.LoggerFactory.getLogger(name));
    }

    @VisibleForTesting
    Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @VisibleForTesting
    org.slf4j.Logger getLogger() {
        return logger;
    }

    public String getName() {
        return logger.getName();
    }

    public void severe(String msg) {
        logger.error(msg);
    }

    public void warning(String msg) {
        logger.warn(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void config(String msg) {
        logger.info(msg);
    }

    public void fine(String msg) {
        logger.debug(msg);
    }

    public void finer(String msg) {
        logger.trace(msg);
    }

    public void finest(String msg) {
        logger.trace(msg);
    }

    public void log(Level level, String msg) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            logger.error(msg);
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            logger.warn(msg);
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            logger.info(msg);
        } else if (level.intValue() >= Level.FINE.intValue()) {
            logger.debug(msg);
        } else {
            logger.trace(msg);
        }
    }

    public void log(Level level, String msg, Object param1) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            if (logger.isErrorEnabled()) {
                logger.error(MessageFormat.format(msg, param1));
            }
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format(msg, param1));
            }
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            if (logger.isInfoEnabled()) {
                logger.info(MessageFormat.format(msg, param1));
            }
        } else if (level.intValue() >= Level.FINE.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug(MessageFormat.format(msg, param1));
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace(MessageFormat.format(msg, param1));
            }
        }
    }

    public void log(Level level, String msg, Object[] params) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            if (logger.isErrorEnabled()) {
                logger.error(MessageFormat.format(msg, params));
            }
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            if (logger.isWarnEnabled()) {
                logger.warn(MessageFormat.format(msg, params));
            }
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            if (logger.isInfoEnabled()) {
                logger.info(MessageFormat.format(msg, params));
            }
        } else if (level.intValue() >= Level.FINE.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug(MessageFormat.format(msg, params));
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace(MessageFormat.format(msg, params));
            }
        }
    }

    public void log(Level level, String msg, Throwable thrown) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            logger.error(msg, thrown);
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            logger.warn(msg, thrown);
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            logger.info(msg, thrown);
        } else if (level.intValue() >= Level.FINE.intValue()) {
            logger.debug(msg, thrown);
        } else {
            logger.trace(msg, thrown);
        }
    }

    public boolean isLoggable(Level level) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            return logger.isErrorEnabled();
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            return logger.isWarnEnabled();
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            return logger.isInfoEnabled();
        } else if (level.intValue() >= Level.FINE.intValue()) {
            return logger.isDebugEnabled();
        } else {
            return logger.isTraceEnabled();
        }
    }

    public Level getLevel() {
        if (logger.isErrorEnabled()) {
            return Level.SEVERE;
        } else if (logger.isWarnEnabled()) {
            return Level.WARNING;
        } else if (logger.isInfoEnabled()) {
            return Level.CONFIG;
        } else if (logger.isDebugEnabled()) {
            return Level.FINE;
        } else if (logger.isTraceEnabled()) {
            return Level.FINEST;
        } else {
            return Level.OFF;
        }
    }

    @SuppressWarnings("unused")
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        log(level, msg);
    }

    @SuppressWarnings("unused")
    public void logp(Level level, String sourceClass, String sourceMethod, String msg,
            Object param1) {
        log(level, msg, param1);
    }

    @SuppressWarnings("unused")
    public void logp(Level level, String sourceClass, String sourceMethod, String msg,
            Object[] params) {
        log(level, msg, params);
    }

    @SuppressWarnings("unused")
    public void logp(Level level, String sourceClass, String sourceMethod, String msg,
            Throwable thrown) {
        log(level, msg, thrown);
    }

    @SuppressWarnings("unused")
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName,
            String msg) {
        log(level, msg);
    }

    @SuppressWarnings("unused")
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName,
            String msg, Object param1) {
        log(level, msg, param1);
    }

    @SuppressWarnings("unused")
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName,
            String msg, Object[] params) {
        log(level, msg, params);
    }

    @SuppressWarnings("unused")
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName,
            String msg, Throwable thrown) {
        log(level, msg, thrown);
    }

    @SuppressWarnings("unused")
    public void entering(String sourceClass, String sourceMethod) {}

    @SuppressWarnings("unused")
    public void entering(String sourceClass, String sourceMethod, Object param1) {}

    @SuppressWarnings("unused")
    public void entering(String sourceClass, String sourceMethod, Object[] params) {}

    @SuppressWarnings("unused")
    public void exiting(String sourceClass, String sourceMethod) {}

    @SuppressWarnings("unused")
    public void exiting(String sourceClass, String sourceMethod, Object result) {}

    @SuppressWarnings("unused")
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {}

    public @Nullable ResourceBundle getResourceBundle() {
        return null;
    }

    public @Nullable String getResourceBundleName() {
        return null;
    }
}
