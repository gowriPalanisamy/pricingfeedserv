package com.tigeranalytics.pricingfeed.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigeranalytics.pricingfeed.controller.PFAuthPolicy;
import com.tigeranalytics.pricingfeed.exception.PricingFeedError;
import com.tigeranalytics.pricingfeed.exception.PricingFeedErrorType;
import com.tigeranalytics.pricingfeed.exception.ResponseVO;
import com.tigeranalytics.pricingfeed.model.UserInfoDetails;
import com.tigeranalytics.pricingfeed.serviceImpl.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final RequestMappingHandlerMapping handlerMapping;

    @Value("${userlifecycleserv.api.url}")
    private String userlifecycleservApiUrl;

    public JwtAuthFilter(RestTemplate restTemplate, RequestMappingHandlerMapping handlerMapping) {
        this.restTemplate = restTemplate;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
        // Retrieve the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userAccNo = null;

        // Check if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract token
            if(isTokenExpired(token)){
                logger.error("Token got expired. Please generate the new token");
                throw new PricingFeedError(PricingFeedErrorType.EXPIRED_TOKEN,"Expired Token... " +
                        "Token got expired.. please generate the new token.");
            }
            userAccNo = jwtService.extractUserAccountNumber(token); // Extract account Number from token
            checkIfUserExists(userAccNo);
            IsUserAuthorizedToAccessResource(jwtService.extractUserRoles(token),request);
        }
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (PricingFeedError er){
                populateErrorResponse(response, er);
        } catch (Exception ex) {
            ResponseVO errorResponse = new ResponseVO();
            errorResponse.setName("UnExpected Error");
            errorResponse.setMessage(ex.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(convertObjectToJson(errorResponse));
        }
    }

    private void populateErrorResponse(HttpServletResponse response, PricingFeedError er) throws IOException {
        ResponseVO errorResponse = new ResponseVO();
        errorResponse.setName(er.getPricingFeedErrorType().name());
        errorResponse.setMessage(er.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(convertObjectToJson(errorResponse));
    }

    private void IsUserAuthorizedToAccessResource(List<String> rolesFromAuthToken,HttpServletRequest request) throws
            Exception {
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null) {
                Object handler = handlerExecutionChain.getHandler();

                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    System.out.println("Handler Method: " + handlerMethod);
                    System.out.println("Controller: " + handlerMethod.getBeanType().getName());
                    System.out.println("Method Name: " + handlerMethod.getMethod().getName());
                    PFAuthPolicy pfAuthPolicy = (PFAuthPolicy) handlerMethod.getMethodAnnotation(PFAuthPolicy.class);
                    String[] roles = pfAuthPolicy.roles();
                    boolean isUserAuthorized = false;
                    for (String role : roles) {
                        if (rolesFromAuthToken.contains(role)) {
                            isUserAuthorized = true;
                            break;
                        }
                    }
                    if (!isUserAuthorized) {
                        logger.error("User do not have permission to access the resource");
                        throw new PricingFeedError(PricingFeedErrorType.ACCESS_FORBIDDEN, "User Access Denied " +
                                "User do not have permission to access the resource");
                    }
                } else {
                    System.out.println("Handler: " + handler);
                }
            } else {
                System.out.println("No handler found for the request.");
            }
    }

    private void checkIfUserExists(String userAccNo) {
        // If the token is valid and no authentication is set in the context
        if (userAccNo != null) {
            String url = userlifecycleservApiUrl + "/api/v1/users/{userAccNo}";
            ResponseEntity<UserInfoDetails> userInfoResponse = restTemplate.getForEntity(url, UserInfoDetails.class,
                    userAccNo);
            if(userInfoResponse.getStatusCode()!=HttpStatus.OK){
                logger.error("USer does not exist Invalid Account Number for Account Number: {} ",userAccNo);
                throw new PricingFeedError(PricingFeedErrorType.USER_NOT_FOUND,"User not found" +
                        "USer does not exist Invalid Account Number "+ userAccNo);
            }
        }else{
            logger.error("Token does not contain the user details");
            throw new PricingFeedError(PricingFeedErrorType.INVALID_TOKEN,"Invalid token... " +
                    "Token does not contain the user details");
        }
    }

    private Boolean isTokenExpired(String token) {
        try{
            return extractExpiration(token).before(new Date());
        }catch (ExpiredJwtException ex){
            return true;
        }

    }

    public Date extractExpiration(String token) throws ExpiredJwtException {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String convertObjectToJson(Object inputObj) {
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = null;
        try {
            orderJson = objectMapper.writeValueAsString(inputObj);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return orderJson;
    }
}

