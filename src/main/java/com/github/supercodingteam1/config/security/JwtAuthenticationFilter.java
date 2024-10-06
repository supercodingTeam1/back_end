package com.github.supercodingteam1.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.exception.CustomAuthenticationException;
import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.repository.entity.user.UserRole;
import com.github.supercodingteam1.service.security.CustomUserDetailService;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.parsers.ReturnTypeParser;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProviderService tokenProvider;

    //JWT 필터 제외 페이지 설정
    private static  final  String[] excludedUrlPatterns = {
            "/", "/static/**", "/favicon.ico",
            "/css/**","/js/**","/images/**",
            "/auth/signup","/auth/login" , "/auth/reissue", "/auth/duplicate",
            "/auth/refreshToken","/items", "/items/**",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/",
            "/webjars/"
    };
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 필터 제외 페이지 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return exclusionPages(request);
    }

    public static boolean exclusionPages(HttpServletRequest request){
        String requestUrl = request.getRequestURI();
        for (String pattern : excludedUrlPatterns) {

            if (pattern.contains("**")) { // URL 패턴에 **이 있으면, ** 앞부분만 비교하여 제외합니다.
                String patternBeforeDoubleStar = pattern.substring(0, pattern.indexOf("**"));
                if (requestUrl.startsWith(patternBeforeDoubleStar)) {
                    return true;
                }
            } else {
                // URL 패턴에 **이 없으면 같음을 비교합니다.
                if (requestUrl.equals(pattern)) {
                    return true;
                }
            }
        }
        return false; // 제외할 URL 패턴이 없는 경우 false를 반환합니다.
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Swagger 관련 URI 체크
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.equals("/swagger-ui.html") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars")) {
            filterChain.doFilter(request, response); // 필터를 건너뜁니다.
            return;
        }

        String jwtToken = jwtTokenProvider.resolveToken(request);
        System.out.println(request.getHeader("origin"));

        //테스트
        System.out.println("Request Headers:");
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                System.out.println(headerName + ": " + request.getHeader(headerName))
        );

        log.info("=========  doFilterInternal getRequestURI  : {}", request.getRequestURI());

        try{
            log.info("Filter running ..");
            //요청에서 토큰 가져오기

            //토큰 검사하기 . JWT 이므로 인가 서버에 요청하지 않고도 검증 가능.  userId 가져오기. 위조된 경우 예외 처리된다.
            if(jwtToken!=null && !jwtToken.equalsIgnoreCase("null")){
                String userId = null;
                try{
                    userId = tokenProvider.validateAndGetUserId(jwtToken);
                }catch(Exception e){
                    throw new CustomAuthenticationException("접근 토큰시간이 만료되었습니다.","ACCESS_TOKEN_EXPIRED");
                }

                //JWT 토큰로그인 인증에서는 API ,  oauth2 는  loadUserApiByUsername 커스텀으로 생성한 메서드로  id 값으로 인증처리
                User user = userRepository.findByUserId(Integer.parseInt(userId));
                CustomUserDetails customUserDetails = customUserDetailService.loadUserByUsername(user.getEmail());

                if(customUserDetails!=null){
                    log.info("필터 === customUserDetails  {}", customUserDetails.getUser().getUser_role().stream().map(userRole -> userRole.getRoleName().getRole()).toList());

                    authSetConfirm( request,  customUserDetails);
                    filterChain.doFilter(request, response);

                    //테스트
                    System.out.println("Response Headers:");
                    response.getHeaderNames().forEach(headerName ->
                            System.out.println(headerName + ": " + response.getHeader(headerName))
                    );

                }else throw new CustomAuthenticationException("해당하는 유저가 없습니다.", "USER_NOT_FOUND");

            }else   throw new CustomAuthenticationException("유효하지 않은 토큰 입니다.","INVALID_TOKEN" );

        }catch (CustomAuthenticationException e  ){
            log.info("JWT CustomAuthenticationException 에러 처리 : "+e.getMessage());

            // 에러처리 방법 : 커스텀 에러 메시지는 다음과 같이 printWriter 전송 처리 한다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized로 변경
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            ResponseDTO errorRes = ResponseDTO.builder()
                    .status(401)  // 상태 코드도 401로 설정
                    .message(e.getMessage())
                    .build();


            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(errorRes);
            PrintWriter printWriter=response.getWriter();
            printWriter.print(result);
            printWriter.flush();
            printWriter.close();

        }catch (Exception e){
            log.info("JWT 기타  에러 메시지처리  ");
            //2.에러처리 방법 : 기타  에러 메시지는 다음과 같이  throw new  호출하여  JwtAuthenticationEntryPoint 클래스로  전송 처리 시킨다.
            request.setAttribute("message","JWT 토큰 필터 처리 에러입니다.");
            request.setAttribute("errorCode",e.getMessage());
            throw new BadCredentialsException("Invalid");
        }
    }

    private void authSetConfirm(HttpServletRequest request, CustomUserDetails customUserDetails){
        //인증 완료; SecurityContextHolder 에 등록해야 인증된 사용자라고 생각한다.
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails ,// userId,//인증된 사용자의 정보. 문자열이 아니어도 아무거나 넣을 수 있다. 보통 UserDetails 를 넣는다.
                null,
                customUserDetails.getAuthorities() //권한 설정값을 넣어 준다.
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 정상 토큰이면 SecurityContext 에 저장
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Http 요청의 헤더를 파싱해 Bearer 토큰을 리턴한다.
     * @param request
     * @return
     */
    public static String parseBearerToken(HttpServletRequest request){
        String bearerToken = request.getHeader("X-AUTH-TOKEN");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
