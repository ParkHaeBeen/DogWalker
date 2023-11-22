package com.project.dogwalker.member.service;

import com.project.dogwalker.domain.user.customer.CustomerDogInfo;
import com.project.dogwalker.domain.user.customer.CustomerDogInfoRepository;
import com.project.dogwalker.member.aws.AwsService;
import com.project.dogwalker.member.client.Oauth;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenProvider;
import com.project.dogwalker.domain.token.RefreshToken;
import com.project.dogwalker.domain.token.RefreshTokenRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.walker.WalkerSchedule;
import com.project.dogwalker.domain.user.walker.WalkerScheduleRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OauthServiceImpl implements OauthService{

  private final Map<String,Oauth> oauthList;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtProvider;
  private final RefreshTokenProvider refreshTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final WalkerScheduleRepository walkerScheduleRepository;
  private final AwsService awsService;
  private final CustomerDogInfoRepository customerDogInfoRepository;

  /**
   * 해당 type 로그인 url로 이동하게
   */
  @Override
  public String requestUrl(String type){
    return oauthList.get(type).getLoginView();
  }


  /**
   * 토큰 받은후 사용자 이메일, 이름 정보 얻은후 db에서 조회후 새로운 회원이면 회원가입뷰로 가서 작성후 회원 저장
   * @param code 구글에서 오는 코드
   * @param type 구글,카카오 중 어느 로그인인지
   */
  @Override
  @Transactional
  public LoginResult login(String code,String type){
    ClientResponse clientReponse = oauthList.get(type).login(code);
    log.info("respoonse ={}",clientReponse);
    Optional<User> userExist = userRepository.findByUserEmail(clientReponse.getEmail());

    boolean newMember=true;
    String accessToken = null;
    String refreshToken = null;

    if(userExist.isPresent()){
      User user = userExist.get();
      newMember=false;
      accessToken= jwtProvider.generateToken(user.getUserEmail(),user.getUserRole());
      RefreshToken token=refreshTokenProvider.generateRefreshToken(user.getUserId());
      refreshToken=token.getRefreshToken();
      refreshTokenRepository.save(token);
    }

    return LoginResult.builder()
        .name(clientReponse.getName())
        .email(clientReponse.getEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .newMember(newMember)
        .build();
  }


  /**
   * customer 회원가입 및 강이지 정보 등록
   * @param request
   * @param dotImg
   */
  @Override
  public LoginResult joinCustomer(JoinUserRequest request , MultipartFile dotImg) {
    User newUser = User.from(request.getCommonRequest());
    newUser.setUserRole(Role.USER);
    User joinUser = userRepository.save(newUser);

    //aws s3 이미지 업로드
    String imgUrl = awsService.saveDogImg(dotImg);

    //강이지 정보 저장
    customerDogInfoRepository.save(CustomerDogInfo.builder()
                                                  .dogMaster(joinUser)
                                                  .dogImgUrl(imgUrl)
                                                  .dogBirth(request.getDogBirth())
                                                  .dogName(request.getDotName())
                                                  .dogType(request.getDotType())
                                                  .dogDescription(request.getDogDescription())
                                                  .build());

    //토큰 생성
    String accessToken= jwtProvider.generateToken(joinUser.getUserEmail(),joinUser.getUserRole());
    RefreshToken token=refreshTokenProvider.generateRefreshToken(joinUser.getUserId());
    String refreshToken=token.getRefreshToken();
    refreshTokenRepository.save(token);

    return LoginResult.builder()
        .name(joinUser.getUserName())
        .email(joinUser.getUserEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .newMember(false)
        .build();
  }

  /**
   * walker 회원가입
   * @param request
   */
  @Override
  @Transactional
  public LoginResult joinWalker(JoinWalkerRequest request) {
    User newUser = User.from(request.getCommonRequest());
    newUser.setUserRole(Role.WALKER);
    User joinUser = userRepository.save(newUser);

    //워커 예약 불가 날짜 저장
    if(request.getSchedules().size()!=0) {
      request.getSchedules().stream()
          .map(schedule -> walkerScheduleRepository.save(WalkerSchedule.builder()
              .walkerId(joinUser.getUserId())
              .dayOfWeek(schedule.getDayOfWeek())
              .startTime(schedule.getStartTime())
              .endTime(schedule.getEndTime())
              .build()));
    }

    //토큰 생성
    String accessToken= jwtProvider.generateToken(joinUser.getUserEmail(),joinUser.getUserRole());
    RefreshToken token=refreshTokenProvider.generateRefreshToken(joinUser.getUserId());
    String refreshToken=token.getRefreshToken();
    refreshTokenRepository.save(token);

    return LoginResult.builder()
        .name(joinUser.getUserName())
        .email(joinUser.getUserEmail())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .newMember(false)
        .build();
  }
}
