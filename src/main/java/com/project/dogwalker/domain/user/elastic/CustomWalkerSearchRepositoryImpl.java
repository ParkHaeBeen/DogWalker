package com.project.dogwalker.domain.user.elastic;


import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;



@RequiredArgsConstructor
public class CustomWalkerSearchRepositoryImpl implements CustomWalkerSearchRepository{

  private final ElasticsearchOperations elasticsearchOperations;


  private final double distanceMax=10;

  @Override
  public Page <WalkerDocument> searchByName(final WalkerInfoSearchCond walkerInfoSearchCond) {
    final PageRequest pageable = PageRequest.of(walkerInfoSearchCond.getStartPage() ,
        walkerInfoSearchCond.getSize());


    NativeSearchQuery query1=createCond(walkerInfoSearchCond,pageable);
    SearchHits <WalkerDocument> searchHits = elasticsearchOperations.search(query1 , WalkerDocument.class);

    List <WalkerDocument> list = searchHits.stream().map(SearchHit::getContent)
        .collect(Collectors.toList());

    return new PageImpl <>(list, pageable, searchHits.getTotalHits());
  }


  private NativeSearchQuery createCond(final WalkerInfoSearchCond walkerInfoSearchCond ,final Pageable pageable){

    GeoPoint bottomRight = new GeoPoint(12.0, 4.0);
    GeoPoint topLeft = new GeoPoint(14.5, 1.0);

    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.geoBoundingBoxQuery("location")
            .setCorners(topLeft.getLat(), topLeft.getLon(), bottomRight.getLat(), bottomRight.getLon()))
        .build();

    return searchQuery;
  }
}
