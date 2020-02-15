package frontend;

import sirius.kernel.di.std.Register;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;

@Register(classes = Controller.class)
public class PaymentController extends BasicController {

    @Routed("/balance")
    public void showBalance(WebContext ctx) {
        ctx.respondWith().template("/templates/frontend/balance.html.pasta");
    }
}
