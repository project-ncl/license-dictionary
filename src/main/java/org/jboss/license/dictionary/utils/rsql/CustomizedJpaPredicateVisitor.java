package org.jboss.license.dictionary.utils.rsql;

import com.github.tennaito.rsql.jpa.AbstractJpaVisitor;
import com.github.tennaito.rsql.jpa.PredicateBuilderStrategy;
import com.github.tennaito.rsql.misc.ArgumentParser;
import com.github.tennaito.rsql.misc.Mapper;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;

import javax.persistence.EntityManager;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class CustomizedJpaPredicateVisitor<T> extends AbstractJpaVisitor<Predicate, T> {
    private From root;
    private CustomizedPredicateBuilder customizedPredicateBuilder;

    public CustomizedJpaPredicateVisitor<T> withRoot(From root) {
        this.root = root;
        return this;
    }

    public CustomizedJpaPredicateVisitor<T> withPredicateBuilder(CustomizedPredicateBuilder builder) {
        this.customizedPredicateBuilder = builder;
        return this;
    }

    public CustomizedJpaPredicateVisitor<T> withMapper(Mapper mapper) {
        this.getBuilderTools().setPropertiesMapper(mapper);
        return this;
    }

    public CustomizedJpaPredicateVisitor<T> withArgumentParser(ArgumentParser argumentParser) {
        this.getBuilderTools().setArgumentParser(argumentParser);
        return this;
    }

    public CustomizedJpaPredicateVisitor<T> withPredicateBuilderStrategy(PredicateBuilderStrategy strategy) {
        this.getBuilderTools().setPredicateBuilder(strategy);
        return this;
    }

    @Override
    public Predicate visit(AndNode node, EntityManager em) {
        return customizedPredicateBuilder.createPredicate(node, root, entityClass, em, getBuilderTools());
    }

    @Override
    public Predicate visit(OrNode node, EntityManager em) {
        return customizedPredicateBuilder.createPredicate(node, root, entityClass, em, getBuilderTools());
    }

    @Override
    public Predicate visit(ComparisonNode node, EntityManager em) {
        return customizedPredicateBuilder.createPredicate(node, root, entityClass, em, getBuilderTools());
    }
}