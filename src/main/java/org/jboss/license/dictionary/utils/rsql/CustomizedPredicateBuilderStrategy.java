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
package org.jboss.license.dictionary.utils.rsql;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;

import com.github.tennaito.rsql.builder.BuilderTools;
import com.github.tennaito.rsql.jpa.PredicateBuilderStrategy;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;

public class CustomizedPredicateBuilderStrategy implements PredicateBuilderStrategy {

    @Override
    public <T> Predicate createPredicate(Node node, From root, Class<T> entity, EntityManager manager, BuilderTools tools)
            throws IllegalArgumentException {

        ComparisonNode cn = (ComparisonNode) node;
        String operator = cn.getOperator().getSymbol();
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        switch (operator) {
            case "=containsPair=": { // usage: "mapField =containsPair= (requiredKey, requiredValue)"
                                     // transforms to JPQL "KEY(mapField) = 'requiredKey' and VALUE(mapField) = 'requiredValue'
                                     // "
                MapJoin phoneRoot = (MapJoin) CustomizedPredicateBuilder.findPropertyPath(cn.getSelector(), root, manager,
                        tools);

                return builder.and(builder.equal(phoneRoot.key(), cn.getArguments().get(0)),
                        builder.equal(phoneRoot.value(), cn.getArguments().get(1)));
            }
            default:
                throw new IllegalArgumentException("Unknown operator: " + cn.getOperator());
        }

    }
}
