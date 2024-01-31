package com.project.dogwalker.domain.walkerservice;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalkerServiceRoute is a Querydsl query type for WalkerServiceRoute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalkerServiceRoute extends EntityPathBase<WalkerServiceRoute> {

    private static final long serialVersionUID = 1561787717L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalkerServiceRoute walkerServiceRoute = new QWalkerServiceRoute("walkerServiceRoute");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.project.dogwalker.domain.reserve.QWalkerReserveServiceInfo reserveInfo;

    public final NumberPath<Long> routeId = createNumber("routeId", Long.class);

    public final StringPath routes = createString("routes");

    public QWalkerServiceRoute(String variable) {
        this(WalkerServiceRoute.class, forVariable(variable), INITS);
    }

    public QWalkerServiceRoute(Path<? extends WalkerServiceRoute> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalkerServiceRoute(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalkerServiceRoute(PathMetadata metadata, PathInits inits) {
        this(WalkerServiceRoute.class, metadata, inits);
    }

    public QWalkerServiceRoute(Class<? extends WalkerServiceRoute> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reserveInfo = inits.isInitialized("reserveInfo") ? new com.project.dogwalker.domain.reserve.QWalkerReserveServiceInfo(forProperty("reserveInfo"), inits.get("reserveInfo")) : null;
    }

}

