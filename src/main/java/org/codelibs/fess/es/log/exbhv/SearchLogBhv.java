/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.log.exbhv;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.es.log.bsbhv.BsSearchLogBhv;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.util.DfTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FreeGen
 */
public class SearchLogBhv extends BsSearchLogBhv {
    private static final Logger logger = LoggerFactory.getLogger(SearchLogBhv.class);

    @Override
    protected String asEsIndex() {
        return ComponentUtil.getFessConfig().getIndexLogIndex();
    }

    @Override
    protected LocalDateTime toLocalDateTime(final Object value) {
        if (value != null) {
            try {
                final Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value.toString()));
                final LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                return date;
            } catch (final DateTimeParseException e) {
                logger.debug("Invalid date format: " + value, e);
            }
        }
        return DfTypeUtil.toLocalDateTime(value);
    }

    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            final Object searchFieldObj = source.get("searchField");
            if (searchFieldObj instanceof Map) {
                ((Map<String, String>) searchFieldObj).entrySet().stream()
                        .forEach(e -> result.getSearchFieldLogList().add(new Pair(e.getKey(), e.getValue())));
            }
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

}
