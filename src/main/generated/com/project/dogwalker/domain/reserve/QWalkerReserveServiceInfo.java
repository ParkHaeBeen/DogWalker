package com.project.dogwalker.domain.reserve;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalkerReserveServiceInfo is a Querydsl query type for WalkerReserveServiceInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalkerReserveServiceInfo extends EntityPathBase<WalkerReserveServiceInfo> {

    private static final long serialVersionUID = 748147515L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalkerReserveServiceInfo walkerReserveServiceInfo = new QWalkerReserveServiceInfo("walkerReserveServiceInfo");

    public final com.project.dogwalker.domain.QBaseEntity _super = new com.project.dogwalker.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.project.dogwalker.domain.user.QUser customer;

    public final QPayHistory payHistory;

    public final NumberPath<Long> reserveId = createNumber("reserveId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> serviceDateTime = createDateTime("serviceDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> servicePrice = createNumber("servicePrice", Integer.class);

    public final EnumPath<WalkerServiceStatus> status = createEnum("status", WalkerServiceStatus.class);

    public final NumberPath<Integer> timeUnit = createNumber("timeUnit", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.project.dogwalker.domain.user.QUser walker;

    public QWalkerReserveServiceInfo(String variable) {
        this(WalkerReserveServiceInfo.class, forVariable(variable), INITS);
    }

    public QWalkerReserveServiceInfo(Path<? extends WalkerReserveServiceInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalkerReserveServiceInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalkerReserveServiceInfo(PathMetadata metadata, PathInits inits) {
        this(WalkerReserveServiceInfo.class, metadata, inits);
    }

    public QWalkerReserveServiceInfo(Class<? extends WalkerReserveServiceInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new com.project.dogwalker.domain.user.QUser(forProperty("customer")) : null;
        this.payHistory = inits.isInitialized("payHistory") ? new QPayHistory(forProperty("payHistory"), inits.get("payHistory")) : null;
        this.walker = inits.isInitialized("walker") ? new com.project.dogwalker.domain.user.QUser(forProperty("walker")) : null;
    }

}

