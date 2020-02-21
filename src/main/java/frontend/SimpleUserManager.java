package frontend;

import sirius.kernel.di.std.Register;
import sirius.kernel.settings.Extension;
import sirius.web.http.WebContext;
import sirius.web.security.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class SimpleUserManager extends GenericUserManager {

    protected SimpleUserManager(ScopeInfo scope, Extension config) {
        super(scope, config);
    }

    /**
     * Used to create <tt>public</tt> user managers.
     */
    @Register(name = "simple")
    public static class Factory implements UserManagerFactory {

        @Nonnull
        @Override
        public UserManager createManager(@Nonnull ScopeInfo scope, @Nonnull Extension config) {
            return new SimpleUserManager(scope, config);
        }
    }

    @Override
    protected Object getUserObject(UserInfo user) {
        return null;
    }

    @Nullable
    @Override
    protected Set<String> computeRoles(@Nullable WebContext webContext, String userId) {
        return Collections.singleton("flag-logged-in");
    }

    @Nonnull
    @Override
    protected String computeUsername(@Nullable WebContext webContext, String userId) {
        return userId;
    }

    @Nonnull
    @Override
    protected String computeTenantname(@Nullable WebContext webContext, String tenantId) {
        return tenantId;
    }

    @Nonnull
    @Override
    protected String computeLang(WebContext webContext, String userId) {
        return "en";
    }

    @Nullable
    @Override
    public UserInfo findUserByName(@Nullable WebContext webContext, String user) {
        UserInfo userInfo = UserInfo.Builder.createUser(user).withPermissions(computeRoles(webContext, user)).build();
        return userInfo;
    }

    @Nullable
    @Override
    public UserInfo findUserByUserId(String userId) {
        return findUserByName(null, userId);
    }

    @Nullable
    @Override
    public UserInfo findUserByCredentials(@Nullable WebContext webContext, String user, String password) {
        return findUserByName(webContext, user);
    }

    @Override
    public UserInfo createUserWithTenant(UserInfo originalUser, String tenantId) {
        throw new UnsupportedOperationException();
    }
}
