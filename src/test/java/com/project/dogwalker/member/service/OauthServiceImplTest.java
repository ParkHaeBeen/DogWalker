package com.project.dogwalker.member.service;

import static com.project.dogwalker.domain.user.Role.USER;
import static com.project.dogwalker.support.fixture.UserFixture.USER_ONE;
import static com.project.dogwalker.support.fixture.UserFixture.USER_TWO;
import static com.project.dogwalker.support.fixture.UserFixture.WALKER_ONE;
import static com.project.dogwalker.support.fixture.UserFixture.WALKER_TWO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.project.dogwalker.domain.token.RefreshToken;
import com.project.dogwalker.domain.token.RefreshTokenRepository;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.customer.CustomerDogInfoRepository;
import com.project.dogwalker.domain.user.walker.WalkerScheduleRepository;
import com.project.dogwalker.domain.user.walker.WalkerServicePriceRepository;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.exception.member.AuthMemberException;
import com.project.dogwalker.exception.member.MemberException;
import com.project.dogwalker.exception.unauth.TokenException;
import com.project.dogwalker.member.aws.AwsService;
import com.project.dogwalker.member.client.AllOauths;
import com.project.dogwalker.member.client.Oauth;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.member.dto.join.JoinCommonRequest;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerPrice;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerSchedule;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenProvider;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Transactional
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
  private WalkerSearchRepository walkerSearchRepository;
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
    ClientResponse clientResponse = new ClientResponse("oauth1@gmail.com","oauth1",idToken);
    Optional<User> user= Optional.ofNullable(User.builder()
                                                .userId(1L)
                                                .userLat(12.0)
                                                .userLnt(3.0)
                                                .userEmail(clientResponse.getEmail())
                                                .userPhoneNumber("010-1234-1234")
                                                .userName("oauth1")
                                                .userRole(USER)
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
    assertThat(result.getName()).isEqualTo("oauth1");
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
    ClientResponse clientResponse = new ClientResponse("auth2@gmail.com","auth2",idToken);
    Optional<User> user= Optional.empty();
    Map<String,Oauth> map=new HashMap<>();

    given(oauthClients.login(type,code)).willReturn(clientResponse);
    given(userRepository.findByUserEmail(clientResponse.getEmail())).willReturn(Optional.empty());

    //when then
    assertThrows(AuthMemberException.class ,()-> oauthService.login(code , type));

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

    User user= USER_ONE.생성();
    ClientResponse clientResponse = new ClientResponse(user.getUserEmail(),user.getUserName(),idToken);

    JoinCommonRequest commonRequest=JoinCommonRequest.builder()
        .name(user.getUserName())
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

    User user= WALKER_TWO.생성();

    RefreshToken refreshTokenObject=RefreshToken.builder().refreshToken(refreshToken).build();
    ClientResponse clientResponse = new ClientResponse(user.getUserEmail(),"test",idToken);
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
    given(walkerSearchRepository.save(any())).willReturn(WalkerDocument.of(user));

    //when
    LoginResult loginResult = oauthService.joinWalker(walkerRequest);

    //then
    assertThat(loginResult.getName()).isEqualTo(user.getUserName());
    assertThat(loginResult.getEmail()).isEqualTo(clientResponse.getEmail());
    assertThat(loginResult.getAccessToken()).isEqualTo(accessToken);
    assertThat(loginResult.getRefreshToken()).isEqualTo(refreshToken);
  }

  @Test
  @DisplayName("서비스 수행자로 회원 가입 가격 미기재로 실패")
  void joinWalker_fail() {
    //given
    String accessToken = "accessToken";
    String refreshToken="refreshToken";
    String type = "google";
    String idToken="idToken";

    User user= WALKER_TWO.생성();

    RefreshToken refreshTokenObject=RefreshToken.builder().refreshToken(refreshToken).build();
    ClientResponse clientResponse = new ClientResponse(user.getUserEmail(),"test",idToken);
    JoinCommonRequest commonRequest=JoinCommonRequest.builder()
        .name(user.getUserName())
        .accessToken(idToken)
        .loginType(type)
        .build();

    JoinWalkerSchedule schedule1=JoinWalkerSchedule.builder().build();
    JoinWalkerSchedule schedule2=JoinWalkerSchedule.builder().build();
    List<JoinWalkerSchedule> schedules=List.of(schedule1,schedule2);


    JoinWalkerRequest walkerRequest=JoinWalkerRequest.builder()
        .commonRequest(commonRequest)
        .schedules(schedules)
        .build();

    given(oauthClients.getUserInfo(type,idToken)).willReturn(clientResponse);
    given(userRepository.save(any())).willReturn(user);

    //when
    assertThrows(MemberException.class,()->oauthService.joinWalker(walkerRequest));

  }
  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급 - 성공")
  void generateToken_success(){
    //given
    String accessToken = "accessToken";
    String refreshToken="refreshToken";

    User user= WALKER_TWO.생성();

    RefreshToken refreshTokenObject=RefreshToken.builder()
        .refreshToken(refreshToken)
        .userId(user.getUserId())
        .expiredAt(LocalDateTime.now().plusDays(10))
        .build();

    given(refreshTokenRepository.findByRefreshToken(anyString())).willReturn(Optional.of(refreshTokenObject));
    given(refreshTokenProvider.isNotExpired(any())).willReturn(false);
    given(userRepository.findById(refreshTokenObject.getUserId())).willReturn(Optional.of(user));
    given(jwtProvider.generateToken(anyString(),any())).willReturn(accessToken);
    given(refreshTokenProvider.generateRefreshToken(anyLong())).willReturn(refreshTokenObject);
    given(refreshTokenRepository.save(any())).willReturn(refreshTokenObject);

    //when
    IssueToken issueToken = oauthService.generateToken(refreshToken);

    //then
    assertThat(issueToken.getAccessToken()).isEqualTo(accessToken);
    assertThat(issueToken.getRefreshToken()).isEqualTo(refreshTokenObject.getRefreshToken());
  }

  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급 - 실패 : refresh token not exist")
  void generateToken_fail_notExist(){
    //given
    String refreshToken="refreshToken";

    given(refreshTokenRepository.findByRefreshToken(anyString())).willReturn(Optional.empty());


    //when
    //then
    assertThrows(TokenException.class,()->oauthService.generateToken(refreshToken));

  }

  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급 - 실패 : refreshtoken 만료기한 지남")
  void generateToken_fail_expired(){
    //given
    String refreshToken="refreshToken";
    User user= WALKER_ONE.생성();

    RefreshToken refreshTokenObject=RefreshToken.builder()
        .refreshToken(refreshToken)
        .userId(user.getUserId())
        .expiredAt(LocalDateTime.now().minusDays(10))
        .build();

    given(refreshTokenRepository.findByRefreshToken(anyString())).willReturn(Optional.of(refreshTokenObject));
    //when
    //then
    assertThrows(MemberException.class,()->oauthService.generateToken(refreshToken));
  }
  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급 - 실패 : user db에 없음")
  void generateToken_fail_notMember(){
    //given
    String refreshToken="refreshToken";

    User user= WALKER_ONE.생성();

    RefreshToken refreshTokenObject=RefreshToken.builder()
        .refreshToken(refreshToken)
        .userId(user.getUserId())
        .expiredAt(LocalDateTime.now().plusDays(10))
        .build();

    given(refreshTokenRepository.findByRefreshToken(anyString())).willReturn(Optional.of(refreshTokenObject));
    given(userRepository.findById(refreshTokenObject.getUserId())).willReturn(Optional.empty());

    //when
    //then
    assertThrows(MemberException.class,()->oauthService.generateToken(refreshToken));
  }

  @Test
  @DisplayName("RefreshToken 재발급 - 성공")
  void generateNewRefreshToken_success(){
    //given
    String refreshToken="refreshToken";
    String accessToken = "accessToken";

    User user= USER_ONE.생성();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(user.getUserEmail())
        .role(USER)
        .build();

    RefreshToken refreshTokenObject=RefreshToken.builder()
        .refreshToken(refreshToken)
        .userId(user.getUserId())
        .expiredAt(LocalDateTime.now().plusDays(10))
        .build();

    given(jwtProvider.validateToken(anyString())).willReturn(true);
    given(jwtProvider.getMemberInfo(anyString())).willReturn(memberInfo);
    given(userRepository.findByUserEmail(anyString())).willReturn(Optional.of(user));
    given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.of(refreshTokenObject));
    given(refreshTokenProvider.generateRefreshToken(anyLong())).willReturn(refreshTokenObject);
    given(refreshTokenRepository.save(any())).willReturn(refreshTokenObject);

    //when
    String newRefreshToken = oauthService.generateNewRefreshToken(accessToken);

    //then
    Assertions.assertThat(newRefreshToken).isEqualTo(refreshToken);
  }

  @Test
  @DisplayName("RefreshToken 재발급 - 실패 : 토큰 만료기간 지남")
  void generateNewRefreshToken_fail(){
    //given
    String refreshToken="refreshToken";
    String accessToken = "accessToken";


    given(jwtProvider.validateToken(anyString())).willReturn(false);

    //when
    //then
    assertThrows(TokenException.class,()->oauthService.generateNewRefreshToken(accessToken));
  }

  @Test
  @DisplayName("RefreshToken 재발급 - 실패 : User 못찾음")
  void generateNewRefreshToken_fail_notFoundUser(){
    //given
    String refreshToken="refreshToken";
    String email="test@gmail.com";
    String accessToken = "accessToken";

    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(USER)
        .build();


    given(jwtProvider.validateToken(anyString())).willReturn(true);
    given(jwtProvider.getMemberInfo(anyString())).willReturn(memberInfo);
    given(userRepository.findByUserEmail(anyString())).willReturn(Optional.empty());

    //when
    //then
    assertThrows(MemberException.class,()->oauthService.generateNewRefreshToken(accessToken));
  }

  @Test
  @DisplayName("RefreshToken 재발급 - 실패 : refreshtoken이 db에 없음")
  void generateNewRefreshToken_fail_refreshTokenNotFound(){
    //given
    String accessToken = "accessToken";
    User user= USER_TWO.생성();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(user.getUserEmail())
        .role(USER)
        .build();


    given(jwtProvider.validateToken(anyString())).willReturn(true);
    given(jwtProvider.getMemberInfo(anyString())).willReturn(memberInfo);
    given(userRepository.findByUserEmail(anyString())).willReturn(Optional.of(user));
    given(refreshTokenRepository.findByUserId(anyLong())).willReturn(Optional.empty());

    //when
    //then
    assertThrows(TokenException.class,()->oauthService.generateNewRefreshToken(accessToken));
  }
}