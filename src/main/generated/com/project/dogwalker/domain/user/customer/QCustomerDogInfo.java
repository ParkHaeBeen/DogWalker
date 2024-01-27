package com.project.dogwalker.domain.user.customer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCustomerDogInfo is a Querydsl query type for CustomerDogInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomerDogInfo extends EntityPathBase<CustomerDogInfo> {

    private static final long serialVersionUID = -2112320589L;

    public static final QCustomerDogInfo customerDogInfo = new QCustomerDogInfo("customerDogInfo");

    public final NumberPath<Long> customerDogId = createNumber("customerDogId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> dogBirth = createDateTime("dogBirth", java.time.LocalDateTime.class);

    public final StringPath dogDescription = createString("dogDescription");

    public final StringPath dogImgUrl = createString("dogImgUrl");

    public final StringPath dogName = createString("dogName");

    public final StringPath dogType = createString("dogType");

    public final NumberPath<Long> masterId = createNumber("masterId", Long.class);

    public QCustomerDogInfo(String variable) {
        super(CustomerDogInfo.class, forVariable(variable));
    }

    public QCustomerDogInfo(Path<? extends CustomerDogInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCustomerDogInfo(PathMetadata metadata) {
        super(CustomerDogInfo.class, metadata);
    }

}

