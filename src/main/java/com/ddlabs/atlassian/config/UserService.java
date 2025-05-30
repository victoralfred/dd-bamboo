package com.ddlabs.atlassian.config;

import com.atlassian.bamboo.crypto.instance.SecretEncryptionService;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
@BambooComponent
public class UserService {
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final SecretEncryptionService secretEncryptionService;
    public UserService(UserManager userManager, LoginUriProvider loginUriProvider, SecretEncryptionService secretEncryptionService) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.secretEncryptionService = secretEncryptionService;
    }

    public void redirectToLoginPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(loginUriProvider.getLoginUri(getLoginUrl(req)).toString());
    }

    public boolean isAuthenticatedUserAndAdmin() {
        Optional<UserProfile> userProfile = Optional.ofNullable(userManager.getRemoteUser());
        return userProfile.isPresent() && (userManager.isSystemAdmin(userProfile.get().getUserKey()));
    }
    public String encrypt(String word) {
        return secretEncryptionService.encrypt(word);
    }
    public String decrypt(String word) {
        return secretEncryptionService.decrypt(word);
    }
    public void authCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAuthenticatedUserAndAdmin()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             redirectToLoginPage(req, resp);
            return;
        }
    }
    private URI getLoginUrl(HttpServletRequest req) {
        String redirectUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            redirectUrl += "?" + queryString;
        }
        return URI.create(redirectUrl);
    }
}
