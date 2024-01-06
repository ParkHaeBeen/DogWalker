package com.project.dogwalker.domain.reserve;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayHistory is a Querydsl query type for PayHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayHistory extends EntityPathBase<PayHistory> {

    private static final long serialVersionUID = -731029238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayHistory payHistory = new QPayHistory("payHistory");

    public final com.project.dogwalker.domain.QBaseEntity _super = new com.project.dogwalker.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.project.dogwalker.domain.user.QUser customer;

    public final NumberPath<Long> payId = createNumber("payId", Long.class);

    public final StringPath payMethod = createString("payMethod");

    public final NumberPath<Integer> payPrice = createNumber("payPrice", Integer.class);

    public final EnumPath<PayStatus> payStatus = createEnum("payStatus", PayStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayHistory(String variable) {
        this(PayHistory.class, forVariable(variable), INITS);
    }

    public QPayHistory(Path<? extends PayHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayHistory(PathMetadata metadata, PathInits inits) {
        this(PayHistory.class, metadata, inits);
    }

    public QPayHistory(Class<? extends PayHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new com.project.dogwalker.domain.user.QUser(forProperty("customer")) : null;
    }

}

