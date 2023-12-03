package com.project.dogwalker.reserve.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.reserve.ReserveAlreadyException;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
class ReserveServiceImplTest {

  @Mock
  private WalkerReserveServiceRepository walkerReserveServiceRepository;

  @Mock
  private PayHistoryRespository payHistoryRespository;

  @Mock
  private UserRepository userRepository;

  @MockBean
  private RedissonClient redissonClient;

  @InjectMocks
  private ReserveServiceImpl reserveService;


  @Test
  @DisplayName("예약이 되어 있지 않음")
  void isReserved_success(){
    //given
    ReserveCheckRequest request=ReserveCheckRequest.builder()
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .walkerId(1L)
        .build();

    given(walkerReserveServiceRepository.findByWalkerUserIdAndServiceDateTime(anyLong(),any()))
        .willReturn(Optional.empty());

    //when
    reserveService.isReserved(request);
    //then

  }

  @Test
  @DisplayName("예약이 되어 있어 실패")
  void isReserve_fail(){
    //given
    ReserveCheckRequest request=ReserveCheckRequest.builder()
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .walkerId(1L)
        .build();
    WalkerReserveServiceInfo walkerReserveServiceInfo=WalkerReserveServiceInfo.builder().build();
    given(walkerReserveServiceRepository.findByWalkerUserIdAndServiceDateTime(anyLong(),any()))
        .willReturn(Optional.of(walkerReserveServiceInfo));

    //when
    //then
    Assertions.assertThrows(ReserveAlreadyException.class,()->reserveService.isReserved(request));

  }

}