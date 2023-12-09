package com.project.dogwalker.domain.user.elastic;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;


@SpringBootTest
class CustomWalkerSearchRepositoryImplTest {

  @Autowired
  private WalkerSearchRepository walkerSearchRepository;

  @Test
  void test(){
    //given
    User user1= User.builder()
        .userLat(12.0)
        .userId(1L)
        .userLnt(3.0)
        .userEmail("a1@naver.com")
        .userPhoneNumber("010-1234-1234")
        .userName("test1")
        .userRole(Role.WALKER)
        .build();

    User user2= User.builder()
        .userLat(12.5)
        .userId(2L)
        .userLnt(3.0)
        .userEmail("a1@naver.com")
        .userPhoneNumber("010-1234-1234")
        .userName("test123")
        .userRole(Role.WALKER)
        .build();
    //walkerSearchRepository.save(WalkerDocument.of(user1));
   // walkerSearchRepository.save(WalkerDocument.of(user2));

    WalkerInfoSearchCond searchCond=WalkerInfoSearchCond.builder()
        .walkerName("test")
        .lat(13.0)
        .lnt(3.0)
        .startPage(3)
        .build();
    //when
    System.out.println("search start");
    Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond);
    System.out.println("search end");
    //then
    System.out.println("ddddddddddddd");
    System.out.println(walkerDocuments.getTotalElements());
    for (WalkerDocument walkerDocument : walkerDocuments) {
      System.out.println("ddd= "+walkerDocument);
    }
  }
}