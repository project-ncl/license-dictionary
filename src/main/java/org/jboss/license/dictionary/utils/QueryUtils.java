/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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
package org.jboss.license.dictionary.utils;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com <br>
 *         Date: 9/11/17
 */
public class QueryUtils {

    public static <T> Optional<T> getSingleResult(TypedQuery<T> query) {
        List<T> resultList = query.getResultList();

        switch (resultList.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(resultList.get(0));
            default:
                throw new NonUniqueResultException(
                        "non unique result for query, expected one result, got: " + resultList.size() + ", " + resultList);
        }
    }

    public static String escapeReservedChars(String rsqlArgument) {
        // From https://github.com/jirutka/rsql-parser/blob/v2.1.0/src/main/java/cz/jirutka/rsql/parser/RSQLParser.java
        // reserved = '"' | "'" | "(" | ")" | ";" | "," | "=" | "!" | "~" | "<" | ">" | " ";

        return rsqlArgument.replace("'", "\\'").replace("!", "\\!").replace("|", "\\|").replace("\"", "\\\"")
                .replace(";", "\\;").replace(",", "\\,").replace("=", "\\=").replace("~", "\\~").replace("<", "\\<")
                .replace(">", "\\>");
    }

}
