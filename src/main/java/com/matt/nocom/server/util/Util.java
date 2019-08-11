package com.matt.nocom.server.util;

import com.matt.nocom.server.Logging;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

public class Util implements Logging {
  private static final String[] REMOTE_IP_HEADERS = {
      "X-REAL-IP"
  };

  public static boolean arraysSameLength(Object[]... arrays) {
    if (arrays.length > 0) {
      int len = arrays[0].length;
      for(int i = 1; i < arrays.length; ++i) // start at 1 since we don't need to compare
        if(len != arrays[i].length)
          return false;
    }
    return true;
  }

  public static InetAddress stringToAddress(String address) {
    try {
      return InetAddress.getByName(address);
    } catch (UnknownHostException e) {
      throw new Error(e);
    }
  }

  public static UsernamePasswordAuthenticationToken toAuthenticationToken(UserDetails details) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
    token.setDetails(details);
    return token;
  }

  public static List<RequestMatcher> antMatchers(String... uris) {
    return Arrays.stream(uris)
        .map(uri -> new AntPathRequestMatcher(uri, null))
        .collect(Collectors.toList());
  }

  public static String findRemoteAddr(ServletRequest request) {
    return Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .map(Util::findRemoteAddr)
        .orElseThrow(() -> new Error("request is not of a HttpServletRequest type"));
  }

  public static String findRemoteAddr(HttpServletRequest request) {
    return Optional.of(request)
        .map(req -> {
          // for reverse proxy compatibility
          for(String header : REMOTE_IP_HEADERS) {
            String content = req.getHeader(header);
            if(content != null)
              return content; // return first valid header
          }
          return null;
        })
        .orElse(request.getRemoteAddr());
  }

  public static InetAddress getRemoteAddr(ServletRequest request) {
    return Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .map(Util::getRemoteAddr)
        .orElseThrow(() -> new Error("request is not of a HttpServletRequest type"));
  }

  public static InetAddress getRemoteAddr(HttpServletRequest request) {
    return Optional.of(request)
        .map(req -> {
          // for reverse proxy compatibility
          for(String header : REMOTE_IP_HEADERS) {
            String content = req.getHeader(header);
            if(content != null) {
              try {
                return stringToAddress(content); // return first valid header
              } catch (Throwable t) {
                LOGGER.warn("{} is not a valid ip header", content);
              }
            }
          }
          return null;
        })
        .orElse(stringToAddress(request.getRemoteAddr()));
  }
}
