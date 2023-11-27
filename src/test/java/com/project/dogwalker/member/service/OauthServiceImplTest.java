package com.project.dogwalker.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.project.dogwalker.domain.token.RefreshToken;
import com.project.dogwalker.domain.token.RefreshTokenRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.customer.CustomerDogInfoRepository;
import com.project.dogwalker.domain.user.walker.WalkerScheduleRepository;
import com.project.dogwalker.domain.user.walker.WalkerServicePriceRepository;
import com.project.dogwalker.exception.member.LoginMemberNotFoundException;
import com.project.dogwalker.member.aws.AwsService;
import com.project.dogwalker.member.client.AllOauths;
import com.project.dogwalker.member.client.Oauth;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinCommonRequest;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerPrice;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerSchedule;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class OauthServiceImplTest {

  @Mock
  private AllOauths oauthClients;
  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtTokenProvider jwtProvider;

  @Mock
  private RefreshTokenProvider refreshTokenProvider;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private WalkerScheduleRepository walkerScheduleRepository;

  @Mock
  private WalkerServicePriceRepository walkerServicePriceRepository;

  @Mock
  private AwsService awsService;

  @Mock
  private CustomerDogInfoRepository customerDogInfoRepository;
  @InjectMocks
  private OauthServiceImpl oauthService;

  @Test
  @DisplayName("기존 회원 로그인 성공 - token, refreshtoken 발급 O")
  void loginSuccess() {
    //given
    String code = "testCode";
    String type = "google";
    String accessToken = "accessToken";
    String refreshToken="refreshToken";
    String idToken="idToken";
    ClientResponse clientResponse = new ClientResponse("test@gmail.com","test",idToken);
    Optional<User> user= Optional.ofNullable(User.builder()
                                                .userId(1L)
                                                .userLat(12.0)
                                                .userLnt(3.0)
                                                .userEmail(clientResponse.getEmail())
                                                .userPhoneNumber("010-1234-1234")
                                                .userName("test")
                                                .userRole(Role.USER)
                                            .build());

    given(oauthClients.login(type,code)).willReturn(clientResponse);
    given(userRepository.findByUserEmail(clientResponse.getEmail())).willReturn(user);
    given(jwtProvider.generateToken(anyString(),any())).willReturn(accessToken);
    given(refreshTokenProvider.generateRefreshToken(any())).willReturn(
        RefreshToken.builder().refreshToken(refreshToken).build());
    given(refreshTokenRepository.save(any())).willReturn(new RefreshToken());

    //when
    LoginResult result = oauthService.login(code , type);

    //then
    assertThat(result.getEmail()).isEqualTo(clientResponse.getEmail());
    assertThat(result.getName()).isEqualTo("test");
    assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
    assertThat(result.getAccessToken()).isEqualTo(accessToken);
  }

  @Test
  @DisplayName("새로운 회원 로그인시 exception 반환")
  void loginSuccessNewMember() {
    //given
    String code = "testCode";
    String type = "google";
    String idToken="idToken";
    ClientResponse clientResponse = new ClientResponse("test@gmail.com","test",idToken);
    Optional<User> user= Optional.empty();
    Map<String,Oauth> map=new HashMap<>();

    //여기서 에러가 나옵니다
    given(oauthClients.login(type,code)).willReturn(clientResponse);
    given(userRepository.findByUserEmail(clientResponse.getEmail())).willReturn(Optional.empty());

    //when then
    assertThrows(LoginMemberNotFoundException.class ,()-> oauthService.login(code , type));

  }

  @Test
  @DisplayName("서비스 이용 고객으로 회원가입 성공")
  void joinCustomer() {
    //given
    String accessToken = "accessToken";
    String refreshToken="refreshToken";
    String type = "google";
    String idToken="idToken";
    String imgUrl="url";

    ClientResponse clientResponse = new ClientResponse("test@gmail.com","test",idToken);
    User user= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(clientResponse.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("test")
        .userRole(Role.USER)
        .build();

    JoinCommonRequest commonRequest=JoinCommonRequest.builder()
        .name("test")
        .accessToken(idToken)
        .loginType(type)
        .build();

    JoinUserRequest request=JoinUserRequest.builder()
        .commonRequest(commonRequest)
        .build();

    MultipartFile multipartFile=mock(MultipartFile.class);
    RefreshToken refreshTokenObject=RefreshToken.builder().refreshToken(refreshToken).build();

    given(oauthClients.getUserInfo(type,idToken)).willReturn(clientResponse);
    given(userRepository.save(any())).willReturn(user);
    given(awsService.saveDogImg(any())).willReturn(imgUrl);
    given(jwtProvider.generateToken(user.getUserEmail(),user.getUserRole())).willReturn(accessToken);
    given(refreshTokenProvider.generateRefreshToken(anyLong())).willReturn(refreshTokenObject);
    given(refreshTokenRepository.save(any())).willReturn(refreshTokenObject);

    //when
    LoginResult loginResult = oauthService.joinCustomer(request , multipartFile);

    //then
    assertThat(loginResult.getName()).isEqualTo(request.getCommonRequest().getName());
    assertThat(loginResult.getEmail()).isEqualTo(clientResponse.getEmail());
    assertThat(loginResult.getAccessToken()).isEqualTo(accessToken);
    assertThat(loginResult.getRefreshToken()).isEqualTo(refreshToken);
  }

  @Test
  @DisplayName("서비스 수행자로 회원 가입 성공")
  void joinWalker() {
    //given
    String accessToken = "accessToken";
    String refreshToken="refreshToken";
    String type = "google";
    String idToken="idToken";
    String email="test@gmail.com";

    RefreshToken refreshTokenObject=RefreshToken.builder().refreshToken(refreshToken).build();
    ClientResponse clientResponse = new ClientResponse(email,"test",idToken);
    User user= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(email)
        .userPhoneNumber("010-1234-1234")
        .userName("test")
        .userRole(Role.WALKER)
        .build();

    JoinCommonRequest commonRequest=JoinCommonRequest.builder()
        .name(user.getUserName())
        .accessToken(idToken)
        .loginType(type)
        .build();

    JoinWalkerSchedule schedule1=JoinWalkerSchedule.builder().build();
    JoinWalkerSchedule schedule2=JoinWalkerSchedule.builder().build();
    List<JoinWalkerSchedule> schedules=List.of(schedule1,schedule2);

    JoinWalkerPrice price1=JoinWalkerPrice.builder().build();
    JoinWalkerPrice price2=JoinWalkerPrice.builder().build();
    List<JoinWalkerPrice> prices=List.of(price1,price2);


    JoinWalkerRequest walkerRequest=JoinWalkerRequest.builder()
        .commonRequest(commonRequest)
        .schedules(schedules)
        .servicePrices(prices)
        .build();

    given(oauthClients.getUserInfo(type,idToken)).willReturn(clientResponse);
    given(userRepository.save(any())).willReturn(user);
    given(jwtProvider.generateToken(user.getUserEmail(),user.getUserRole())).willReturn(accessToken);
    given(refreshTokenProvider.generateRefreshToken(anyLong())).willReturn(refreshTokenObject);
    given(refreshTokenRepository.save(any())).willReturn(refreshTokenObject);

    //when
    LoginResult loginResult = oauthService.joinWalker(walkerRequest);

    //then
    assertThat(loginResult.getName()).isEqualTo(user.getUserName());
    assertThat(loginResult.getEmail()).isEqualTo(clientResponse.getEmail());
    assertThat(loginResult.getAccessToken()).isEqualTo(accessToken);
    assertThat(loginResult.getRefreshToken()).isEqualTo(refreshToken);
  }
}