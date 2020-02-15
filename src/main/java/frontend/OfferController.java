package frontend;

import sirius.kernel.di.std.Register;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Page;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;

@Register(classes = Controller.class)
public class OfferController extends BasicController {

    @Routed("/create-offer")
    public void createOffer(WebContext ctx) {
        ctx.respondWith().template("/templates/frontend/create-offer.html.pasta");
    }

    @Routed("/list-offers")
    public void listOffers(WebContext ctx) {
        ctx.respondWith().template("/templates/frontend/list-offers.html.pasta", new Page());
    }

    @Routed("/accept-offer/:1")
    public void acceptOffer(WebContext ctx) {
        ctx.respondWith().redirectToGet("/home");
    }
}
