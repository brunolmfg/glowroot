/*
 * Copyright 2014-2018 the original author or authors.
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
package org.glowroot.agent.plugin.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.glowroot.agent.plugin.api.checker.Nullable;
import org.glowroot.agent.plugin.api.util.ImmutableList;
import org.glowroot.agent.plugin.api.util.ImmutableMap;
import org.glowroot.agent.plugin.servlet.ServletAspect.HttpServletRequest;

// shallow copies are necessary because request may not be thread safe, which may affect ability
// to see detail from active traces
//
// shallow copies are also necessary because servlet container may clear out the objects after the
// request is complete (e.g. tomcat does this) in order to reuse them, in which case this detail
// would need to be captured synchronously at end of request anyways (although then it could be
// captured only if trace met threshold for storage...)
class DetailCapture {

    private DetailCapture() {}

    static Map<String, Object> captureRequestParameters(
            Map</*@Nullable*/ String, /*@Nullable*/ Object> requestParameters) {
        List<Pattern> capturePatterns = ServletPluginProperties.captureRequestParameters();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry</*@Nullable*/ String, /*@Nullable*/ Object> entry : requestParameters
                .entrySet()) {
            String name = entry.getKey();
            if (name == null) {
                continue;
            }
            // converted to lower case for case-insensitive matching (patterns are lower case)
            String keyLowerCase = name.toLowerCase(Locale.ENGLISH);
            if (!matchesOneOf(keyLowerCase, capturePatterns)) {
                continue;
            }
            @Nullable
            Object values = entry.getValue();
            if (values instanceof String[]) {
                set(map, name, (String[]) values);
            }
        }
        return ImmutableMap.copyOf(map);
    }

    static Map<String, Object> captureRequestParameters(HttpServletRequest request) {
        Enumeration<? extends /*@Nullable*/ Object> e = request.getParameterNames();
        if (e == null) {
            return Collections.emptyMap();
        }
        List<Pattern> capturePatterns = ServletPluginProperties.captureRequestParameters();
        List<Pattern> maskPatterns = ServletPluginProperties.maskRequestParameters();
        Map<String, Object> map = new HashMap<String, Object>();
        while (e.hasMoreElements()) {
            Object nameObj = e.nextElement();
            if (nameObj == null) {
                continue;
            }
            if (!(nameObj instanceof String)) {
                continue;
            }
            String name = (String) nameObj;
            // converted to lower case for case-insensitive matching (patterns are lower case)
            String keyLowerCase = name.toLowerCase(Locale.ENGLISH);
            if (!matchesOneOf(keyLowerCase, capturePatterns)) {
                continue;
            }
            if (matchesOneOf(keyLowerCase, maskPatterns)) {
                map.put(name, "****");
                continue;
            }
            @Nullable
            String[] values = request.getParameterValues(name);
            if (values != null) {
                set(map, name, values);
            }
        }
        return ImmutableMap.copyOf(map);
    }

    private static void set(Map<String, Object> map, String name, @Nullable String[] values) {
        if (values == null) {
            return;
        }
        if (values.length == 1) {
            String value = values[0];
            if (value != null) {
                map.put(name, value);
            }
        } else {
            List</*@Nullable*/ String> list =
                    new ArrayList</*@Nullable*/ String>(values.length);
            Collections.addAll(list, values);
            map.put(name, list);
        }
    }

    static Map<String, Object> captureRequestHeaders(HttpServletRequest request) {
        List<Pattern> capturePatterns = ServletPluginProperties.captureRequestHeaders();
        if (capturePatterns.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> requestHeaders = new HashMap<String, Object>();
        Enumeration</*@Nullable*/ String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return Collections.emptyMap();
        }
        for (Enumeration</*@Nullable*/ String> e = headerNames; e.hasMoreElements();) {
            String name = e.nextElement();
            if (name == null) {
                continue;
            }
            // converted to lower case for case-insensitive matching (patterns are lower case)
            String keyLowerCase = name.toLowerCase(Locale.ENGLISH);
            if (!matchesOneOf(keyLowerCase, capturePatterns)) {
                continue;
            }
            Enumeration</*@Nullable*/ String> values = request.getHeaders(name);
            if (values != null) {
                captureRequestHeader(name, values, requestHeaders);
            }
        }
        return ImmutableMap.copyOf(requestHeaders);
    }

    static @Nullable String captureRequestRemoteAddr(HttpServletRequest request) {
        if (ServletPluginProperties.captureRequestRemoteAddr()) {
            return request.getRemoteAddr();
        } else {
            return null;
        }
    }

    static @Nullable String captureRequestRemoteHost(HttpServletRequest request) {
        if (ServletPluginProperties.captureRequestRemoteHost()) {
            return request.getRemoteHost();
        } else {
            return null;
        }
    }

    static boolean matchesOneOf(String key, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(key).matches()) {
                return true;
            }
        }
        return false;
    }

    private static void captureRequestHeader(String name, Enumeration</*@Nullable*/ String> values,
            Map<String, Object> requestHeaders) {
        if (!values.hasMoreElements()) {
            requestHeaders.put(name, "");
        } else {
            String value = values.nextElement();
            if (!values.hasMoreElements()) {
                requestHeaders.put(name, Strings.nullToEmpty(value));
            } else {
                List<String> list = new ArrayList<String>();
                list.add(Strings.nullToEmpty(value));
                while (values.hasMoreElements()) {
                    list.add(Strings.nullToEmpty(values.nextElement()));
                }
                requestHeaders.put(name, ImmutableList.copyOf(list));
            }
        }
    }
}
