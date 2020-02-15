package frontend;

import io.netty.handler.codec.http.HttpResponseStatus;
import sirius.biz.web.BizController;
import sirius.kernel.di.std.Register;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.UserContext;

@Register(classes = Controller.class)
public class AccessController extends BizController {

    @Routed("/")
    public void index(WebContext ctx) {
        login(ctx);
    }

    @Routed("/login")
    public void login(WebContext ctx) {
        ctx.respondWith().direct(HttpResponseStatus.OK, "Hello World");
    }
}
