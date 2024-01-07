package com.project.dogwalker.domain.user.elastic;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.walkersearch.dto.WalkerInfoSearchCond;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;


@SpringBootTest
class CustomWalkerSearchRepositoryImplTest {

  @Autowired
  private WalkerSearchRepository walkerSearchRepository;

  @Test
  @DisplayName("elastic insert 성공")
  void elastic_insert_test(){
    //이전 데이터 영향받지 않기 위해 삭제후 테스트 진행
    walkerSearchRepository.deleteAll();

    //given
    User user1= User.builder()
        .userLat(37.300422)
        .userId(1L)
        .userLnt(127.074458)
        .userEmail("test1@naver.com")
        .userPhoneNumber("010-1234-1234")
        .userName("test1")
        .userRole(Role.WALKER)
        .build();

    User user2= User.builder()
        .userLat(37.3004)
        .userId(2L)
        .userLnt(127.074459)
        .userEmail("test123@naver.com")
        .userPhoneNumber("010-1234-1234")
        .userName("test123")
        .userRole(Role.WALKER)
        .build();
    walkerSearchRepository.save(WalkerDocument.of(user1));
    walkerSearchRepository.save(WalkerDocument.of(user2));

    WalkerInfoSearchCond searchCond=WalkerInfoSearchCond.builder()
        .walkerName("test")
        .lat(37.3017387)
        .lnt(127.0735513)
        .startPage(0)
        .build();
    //when
    Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond);

    //then
    Assertions.assertThat(walkerDocuments.getTotalElements()).isEqualTo(2);
  }
}