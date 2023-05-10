/*
package com.cict.core.audit;

import com.cict.auth.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Value("${jwt.header:Authorization}")
    private String tokenHeader;
    private final JwtTokenUtil jwtTokenUtil;

    public AuditorAwareImpl(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Optional<String> getCurrentAuditor() {

        final String activeProfile = System.getProperty("spring.profiles.active", "unknown");
        if(activeProfile.equalsIgnoreCase("tmp"))
            return Optional.of("eduard");

        //Added first to run the Command line
if( (RequestContextHolder.getRequestAttributes()) ==null){
            return Optional.of("eduard");
        }


        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String header = request.getHeader(tokenHeader);
        String username = "system";
        if (!ObjectUtils.isEmpty(header)) {
//            final String fromToken = jwtTokenUtil.getUsernameFromToken( header.substring(7)); // remove coz i saw no bearer prefix needed
            final String fromToken = jwtTokenUtil.getUsernameFromToken( header );

            if(!ObjectUtils.isEmpty(fromToken)) {
                username = fromToken;
            }
        }
        return Optional.of(username);
    }

}
*/
