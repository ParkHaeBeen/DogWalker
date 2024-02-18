package com.project.dogwalker.domain.user.elastic;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.support.RepositoryTest;
import com.project.dogwalker.walkersearch.dto.request.WalkerInfoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@RepositoryTest
class CustomWalkerSearchRepositoryImplTest{

  @Autowired
  private WalkerSearchRepository walkerSearchRepository;

  @Test
  @DisplayName("elastic insert 성공")
  void elastic_insert_test(){
    //이전 데이터 영향받지 않기 위해 삭제후 테스트 진행
    //walkerSearchRepository.deleteAll();

    //given
    for(int i=1;i<15;i++){
      User user = User.builder()
          .userId((long)i)
          .userLat(37.300422)
          .userLnt(127.074458)
          .userEmail("test"+i+"@gmail.com")
          .userName("test"+i)
          .userRole(Role.WALKER)
          .userPhoneNumber("010-1234-123"+i)
          .build();
      walkerSearchRepository.save(WalkerDocument.of(user));
    }

    WalkerInfoRequest searchCond= WalkerInfoRequest.builder()
        .name("test")
        .lat(37.3017387)
        .lnt(127.0735513)
        .build();

    Pageable pageable = PageRequest.of(0,10);

    //when
    Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond,pageable);

    //then
    //Assertions.assertThat(walkerDocuments.getTotalElements()).isEqualTo(14);
    //Assertions.assertThat(walkerDocuments.getSize()).isEqualTo(10);
  }
}