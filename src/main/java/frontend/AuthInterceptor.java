package frontend;

import sirius.kernel.di.std.Register;
import sirius.web.controller.Controller;
import sirius.web.controller.Interceptor;
import sirius.web.controller.Message;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;
import sirius.web.security.UserContext;

import java.lang.reflect.Method;

@Register
public class AuthInterceptor implements Interceptor {
    @Override
    public boolean before(WebContext ctx, boolean jsonCall, Controller controller, Method method) throws Exception {
        boolean requiresLogin = method.getAnnotationsByType(LoginRequired.class).length > 0;

        if (requiresLogin && !UserContext.getCurrentUser().isLoggedIn()) {
            UserContext.message(Message.info("You need to login to use the application"));
            ctx.respondWith().redirectToGet("/login");
            return true;
        }

        return false;
    }

    @Override
    public boolean beforePermissionError(String permission, WebContext ctx, boolean jsonCall, Controller controller, Method method) throws Exception {
        return false;
    }

    @Override
    public boolean shouldExecuteRoute(WebContext ctx, boolean jsonCall, Controller controller) {
        return true;
    }
}
