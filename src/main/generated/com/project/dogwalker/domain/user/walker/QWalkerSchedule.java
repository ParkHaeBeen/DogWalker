package com.project.dogwalker.domain.user.walker;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWalkerSchedule is a Querydsl query type for WalkerSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalkerSchedule extends EntityPathBase<WalkerSchedule> {

    private static final long serialVersionUID = 1846845838L;

    public static final QWalkerSchedule walkerSchedule = new QWalkerSchedule("walkerSchedule");

    public final StringPath dayOfWeek = createString("dayOfWeek");

    public final NumberPath<Integer> endTime = createNumber("endTime", Integer.class);

    public final NumberPath<Integer> startTime = createNumber("startTime", Integer.class);

    public final NumberPath<Long> walkerId = createNumber("walkerId", Long.class);

    public final NumberPath<Long> walkerScId = createNumber("walkerScId", Long.class);

    public QWalkerSchedule(String variable) {
        super(WalkerSchedule.class, forVariable(variable));
    }

    public QWalkerSchedule(Path<? extends WalkerSchedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWalkerSchedule(PathMetadata metadata) {
        super(WalkerSchedule.class, metadata);
    }

}

