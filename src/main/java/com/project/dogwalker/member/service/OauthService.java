package com.project.dogwalker.member.service;

import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import org.springframework.web.multipart.MultipartFile;

public interface OauthService {

  String requestUrl(String type);
  LoginResult login(String code, String type);
  LoginResult joinCustomer(JoinUserRequest request, MultipartFile dotImg);
  LoginResult joinWalker(JoinWalkerRequest request);
  IssueToken generateToken(String refreshToken);
  String generateNewRefreshToken(String accessToken);
}
