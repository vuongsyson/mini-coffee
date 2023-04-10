package com.shopapp.config.audit;

import com.shopapp.config.Constants;
import com.shopapp.security.SecurityUtils;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

    @Override
    public String provide() {
        return SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM);
    }
}
