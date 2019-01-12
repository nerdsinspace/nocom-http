package com.matt.nocom.server.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class Util {
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
    return new UsernamePasswordAuthenticationToken(details.getUsername(), details.getPassword(), details.getAuthorities());
  }
}
