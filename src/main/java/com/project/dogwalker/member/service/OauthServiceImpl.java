package com.project.dogwalker.member.service;

import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.project.dogwalker.exception.ErrorCode.NOT_WRITE_SERVICE_PRICE;

import com.project.dogwalker.domain.token.RefreshToken;
import com.project.dogwalker.domain.token.RefreshTokenRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.customer.CustomerDogInfo;
import com.project.dogwalker.domain.user.customer.CustomerDogInfoRepository;
import com.project.dogwalker.domain.user.walker.WalkerSchedule;
import com.project.dogwalker.domain.user.walker.WalkerScheduleRepository;
import com.project.dogwalker.domain.user.walker.WalkerServicePrice;
import com.project.dogwalker.domain.user.walker.WalkerServicePriceRepository;
import com.project.dogwalker.exception.member.LoginMemberNotFoundException;
import com.project.dogwalker.exception.member.WalkerNotWritePriceException;
import com.project.dogwalker.member.aws.AwsService;
import com.project.dogwalker.member.client.AllOauths;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OauthServiceImpl implements OauthService{

  private final AllOauths oauthClients;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtProvider;
  private final RefreshTokenProvider refreshTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final WalkerScheduleRepository walkerScheduleRepository;
  private final WalkerServicePriceRepository walkerServicePriceRepository;
  private final AwsService awsService;
  private final CustomerDogInfoRepository customerDogInfoRepository;

  /**
   * 해당 type 로그인 url로 이동하게
   */
  @Override
  public String requestUrl(final String type){
    return oauthClients.requestUrl(type);
  }


  /**
   * 토큰 받은후 사용자 이메일, 이름 정보 얻은후 db에서 조회후 새로운 회원이면 exception반환
   * @param code 구글,네이버에서 오는 코드
   * @param type 구글,네이버 중 어느 로그인인지
   */
  @Override
  public LoginResult login(final String code,final String type){
    final ClientResponse clientReponse = oauthClients.login(type,code);
    log.info("respoonse ={}",clientReponse);

    Optional<User> userExist = userRepository.findByUserEmail(clientReponse.getEmail());

    if(!userExist.isPresent()){
      throw new LoginMemberNotFoundException(NOT_EXIST_MEMBER,clientReponse.getIdToken());
    }

    final User user = userExist.get();
    final String accessToken = jwtProvider.generateToken(user.getUserEmail() , user.getUserRole());
    final RefreshToken token = refreshTokenProvider.generateRefreshToken(user.getUserId());
    final String refreshToken = token.getRefreshToken();
    refreshTokenRepository.save(token);

    return LoginResult.builder()
        .name(clientReponse.getName())
        .email(clientReponse.getEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }


  /**
   * customer 회원가입 및 강이지 정보 등록
   * @param request
   * @param dotImg
   */
  @Override
  public LoginResult joinCustomer(final JoinUserRequest request ,final MultipartFile dotImg) {
    log.info("request = {}",request.getCommonRequest());
    final ClientResponse userInfo = oauthClients.getUserInfo(request.getCommonRequest().getLoginType() ,
        request.getCommonRequest().getAccessToken());

    User newUser = User.from(request.getCommonRequest(),userInfo.getEmail());
    newUser.setUserRole(Role.USER);
    final User joinUser = userRepository.save(newUser);

    //aws s3 이미지 업로드
    final String imgUrl = awsService.saveDogImg(dotImg);

    //강이지 정보 저장
    customerDogInfoRepository.save(CustomerDogInfo.builder()
                                                  .masterId(joinUser.getUserId())
                                                  .dogImgUrl(imgUrl)
                                                  .dogBirth(request.getDogBirth())
                                                  .dogName(request.getDogName())
                                                  .dogType(request.getDogType())
                                                  .dogDescription(request.getDogDescription())
                                                  .build());

    //토큰 생성
    final String accessToken= jwtProvider.generateToken(joinUser.getUserEmail(),joinUser.getUserRole());
    final RefreshToken token=refreshTokenProvider.generateRefreshToken(joinUser.getUserId());
    final String refreshToken=token.getRefreshToken();
    refreshTokenRepository.save(token);

    return LoginResult.builder()
        .name(joinUser.getUserName())
        .email(joinUser.getUserEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  /**
   * walker 회원가입
   * @param request
   */
  @Override
  public LoginResult joinWalker(final JoinWalkerRequest request) {
    final ClientResponse userInfo = oauthClients.getUserInfo(request.getCommonRequest().getLoginType() ,
        request.getCommonRequest().getAccessToken());
    log.info("userInfo = {}",userInfo);
    User newUser = User.from(request.getCommonRequest(),userInfo.getEmail());
    newUser.setUserRole(Role.WALKER);
    final User joinUser = userRepository.save(newUser);

    //워커 예약 불가 날짜 저장
    log.info("shedule = {}, size = {}",request.getSchedules(),request.getSchedules().size());
    if(request.getSchedules().size()!=0) {

      List<WalkerSchedule> walkerSchedules = request.getSchedules().stream()
          .map(schedule -> WalkerSchedule.builder()
              .walkerId(joinUser.getUserId())
              .dayOfWeek(schedule.getDayOfWeek())
              .startTime(schedule.getStartTime())
              .endTime(schedule.getEndTime())
              .build())
          .collect(Collectors.toList());

      walkerScheduleRepository.saveAll(walkerSchedules);

    }

    //서비스 수행자가 30,40,50분마다 설정한 가격 저장
    if(request.getServicePrices().size()!=0){
      List<WalkerServicePrice> timePerPrice = request.getServicePrices().stream()
          .map(price -> WalkerServicePrice.builder()
              .walkerId(joinUser.getUserId())
              .serviceFee(price.getTimeFee())
              .timeUnit(price.getTimeUnit())
              .build())
          .collect(Collectors.toList());

      walkerServicePriceRepository.saveAll(timePerPrice);
    }else{
      throw new WalkerNotWritePriceException(NOT_WRITE_SERVICE_PRICE);
    }

    //토큰 생성
    final String accessToken= jwtProvider.generateToken(joinUser.getUserEmail(),joinUser.getUserRole());
    final RefreshToken token=refreshTokenProvider.generateRefreshToken(joinUser.getUserId());
    final String refreshToken=token.getRefreshToken();
    refreshTokenRepository.save(token);

    return LoginResult.builder()
        .name(joinUser.getUserName())
        .email(joinUser.getUserEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }
}
