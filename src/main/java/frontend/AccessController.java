package frontend;

import sirius.kernel.di.std.Register;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.UserContext;

@Register(classes = Controller.class)
public class AccessController extends BasicController {

    @Routed("/")
    public void index(WebContext ctx) {
        if (UserContext.getCurrentUser().isLoggedIn()) {
            home(ctx);
            return;
        }
        login(ctx);
    }

    @Routed("/login")
    public void login(WebContext ctx) {
        if (UserContext.getCurrentUser().isLoggedIn()) {
            ctx.respondWith().redirectToGet("/home");
            return;
        }

        ctx.respondWith().template("/templates/frontend/login.html.pasta");
    }

    @Routed("/logout")
    public void logout(WebContext ctx) {
        ctx.respondWith().redirectToGet("/login");
    }

    @Routed("/home")
    public void home(WebContext ctx) {
        ctx.respondWith().template("/templates/frontend/home.html.pasta");
    }
}
