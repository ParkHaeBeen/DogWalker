package com.project.dogwalker.domain.user.walker;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWalkerServicePrice is a Querydsl query type for WalkerServicePrice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalkerServicePrice extends EntityPathBase<WalkerServicePrice> {

    private static final long serialVersionUID = -130965749L;

    public static final QWalkerServicePrice walkerServicePrice = new QWalkerServicePrice("walkerServicePrice");

    public final NumberPath<Integer> serviceFee = createNumber("serviceFee", Integer.class);

    public final NumberPath<Integer> timeUnit = createNumber("timeUnit", Integer.class);

    public final NumberPath<Long> walkerId = createNumber("walkerId", Long.class);

    public final NumberPath<Long> walkerPcId = createNumber("walkerPcId", Long.class);

    public QWalkerServicePrice(String variable) {
        super(WalkerServicePrice.class, forVariable(variable));
    }

    public QWalkerServicePrice(Path<? extends WalkerServicePrice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWalkerServicePrice(PathMetadata metadata) {
        super(WalkerServicePrice.class, metadata);
    }

}

