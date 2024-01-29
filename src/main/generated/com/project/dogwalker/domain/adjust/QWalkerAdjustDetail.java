package com.project.dogwalker.domain.adjust;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalkerAdjustDetail is a Querydsl query type for WalkerAdjustDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalkerAdjustDetail extends EntityPathBase<WalkerAdjustDetail> {

    private static final long serialVersionUID = 12603849L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalkerAdjustDetail walkerAdjustDetail = new QWalkerAdjustDetail("walkerAdjustDetail");

    public final EnumPath<AdjustDetailStatus> adjustDetailStatus = createEnum("adjustDetailStatus", AdjustDetailStatus.class);

    public final QWalkerAdjust walkerAdjust;

    public final NumberPath<Long> walkerAdjustDetailId = createNumber("walkerAdjustDetailId", Long.class);

    public final NumberPath<Integer> walkerAdjustPrice = createNumber("walkerAdjustPrice", Integer.class);

    public final NumberPath<Long> walkerReserveServiceId = createNumber("walkerReserveServiceId", Long.class);

    public QWalkerAdjustDetail(String variable) {
        this(WalkerAdjustDetail.class, forVariable(variable), INITS);
    }

    public QWalkerAdjustDetail(Path<? extends WalkerAdjustDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalkerAdjustDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalkerAdjustDetail(PathMetadata metadata, PathInits inits) {
        this(WalkerAdjustDetail.class, metadata, inits);
    }

    public QWalkerAdjustDetail(Class<? extends WalkerAdjustDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.walkerAdjust = inits.isInitialized("walkerAdjust") ? new QWalkerAdjust(forProperty("walkerAdjust")) : null;
    }

}

