package org.jboss.license.dictionary.utils;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 9/11/17
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

}
