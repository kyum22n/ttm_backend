package com.example.demo.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component // 빈을 주입 시킬 수 있음
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
  @Autowired
  private JwtService jwtService;

  // 전처리
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
     log.info("전처리 실행");

    // Preflight request로 요청한 것은 통과
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
      return true;
    }

    // 1) 요청 매핑 메소드에 @Login이 붙어 있는지 확인
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Login login = handlerMethod.getMethodAnnotation(Login.class);
    
    if (login != null) {
      // @Login 붙어 있을 경우
      // 2) JWT가 있는지 확인
      String jwt = null;

      // 2-1) 요청 헤더에 실려왔는지 확인
      String authorization = request.getHeader("Authorization");
      if(authorization != null ){
        // Authorization: Bearer xxxxxx의 형태 값 체크
        if(!authorization.substring(7).equals("")){
          jwt = authorization.substring(7);
        }
      }

      // 2-2) 쿼리 스트링 형태로 실려왔는지 확인
      String jwtParam = request.getParameter("jwt");
      if(jwtParam != null){
        jwt =jwtParam;
      }

      if(jwt == null){
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT가 없습니다.");
        return false;

      } else {
        if (jwtService.validateJwt(jwt)) { // 유효할 경우
          return true;
        } else { // 유효하지 않을 경우
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT가 유효하지 않습니다.");
          return false;
        }
      }
    } else {
      // @Login이 붙어 있지 않을 경우
      return true;
    }

  }
}
