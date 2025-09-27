package com.example.demo.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
  // JWT 서명(Signature)에 사용되는 문자열 키(Secret Key)
  // - 토큰 위조를 막는 "열쇠" 역할
  // - 짧고 단순하면 해커가 쉽게 추측할 수 있어 보안 취약
  // - 반드시 길고, 복잡하게 설정해야 함
  // - 실제 배포 환경에서는 코드에 직접 작성하지 않고 properties 또는 환경변수로 관리가 안전
  private String strKey = "MySuperSecretKey!2025@Kosa#Backend";
  // 서명 및 암호화를 위한 비밀 키(String -> SecretKey로 변환해서 사용)
  private SecretKey secretKey;
  // JWT의 유효 기간(단위: 밀리세컨)
  private long jwtDuration = 21 * 24 * 60 * 60 * 1000;

  public JwtService() throws Exception {
    // 문자열 키를 UTF-8 바이트 배열 변환
    byte[] bytes = strKey.getBytes("UTF-8");
    // 받은 배열을 HMAC 서명에 사용할 비밀 키 객체로 변환
    secretKey = Keys.hmacShaKeyFor(bytes);
  }

  // JWT 생성 메소드
  public String createJwt(Integer userId, String userLoginId, String userEmail){
    // JWT를 생성하는 빌더 얻기
    JwtBuilder jwtBuilder = Jwts.builder();

    // JWT에 포함하는 Payload 추가
    jwtBuilder.subject(userLoginId); // 토큰 주제(대표 식별자): 로그인 아이디
    jwtBuilder.claim("userId", userId); 
    jwtBuilder.claim("userEmail", userEmail);

    // JWT 유효기간 설정
    jwtBuilder.expiration(new Date(new Date().getTime() + jwtDuration));

    // SecretKey 설정
    jwtBuilder.signWith(secretKey);

    // JWT 얻기
    String jwt = jwtBuilder.compact(); // compac() 압축

    return jwt; 
  }

  // Jwt 토큰 유효 검사
  public boolean validateJwt(String jwt){
    boolean result = false;

    try{
      // JWT를 해석하는 JwtParser 얻기
      JwtParserBuilder jwtParserBuilder = Jwts.parser();
      jwtParserBuilder.verifyWith(secretKey);
      JwtParser jwtParser = jwtParserBuilder.build();
      
      // JWT를 해석 Jws= "서명된 JWT(JSON Web Signature)"
      Jws<Claims> jws = jwtParser.parseSignedClaims(jwt);
      result = true;
      
    }catch (ExpiredJwtException e){
      log.info("기간이 만료된 토큰입니다.");
    } catch (io.jsonwebtoken.security.SecurityException e){
      log.info("잘못된 서명입니다.");
    } catch(Exception e){
      log.info("토큰을 해석할 수 없습니다.");
    }
    
    return result;
  }
  
  // Claims값을 가져오는 메소드
  public Map<String,String> getClaims(String jwt){
    // JWT를 해석하는 JwtParser 얻기
    JwtParserBuilder jwtParserBuilder = Jwts.parser();
    jwtParserBuilder.verifyWith(secretKey);
    JwtParser jwtParser = jwtParserBuilder.build();

    // Jwt를 해석
    Jws<Claims> jws = jwtParser.parseSignedClaims(jwt);
    Claims claims = jws.getPayload();

    // claims값 받아오기
    Map<String, String> map = new HashMap<>();
    map.put("userLoginId", claims.getSubject());
    map.put("userId", claims.get("userId").toString());
    map.put("userEmail", claims.get("userEmail").toString());

    return map;

  }

}
