package frontend;

import data.BlockchainConnector;
import model.TradeOffer;
import model.User;
import model.UserData;
import sirius.kernel.commons.Amount;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Message;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;
import sirius.web.security.UserContext;

import java.util.List;
import java.util.UUID;

@Register(classes = Controller.class)
public class OfferController extends BasicController {
    @Part
    private BlockchainConnector connector;

    @Routed("/create-offer")
    @LoginRequired
    public void createOffer(WebContext ctx) {
        User user = connector.fetchUser(UserContext.getCurrentUser().getUserId());
        UserData userData = connector.fetchUserData(user.getUserId());

        if (ctx.isUnsafePOST()) {
            String source = ctx.getParameter("source");
            Amount quantityToSell = ctx.get("quantity").getAmount().fill(Amount.ZERO);
            Amount price = ctx.get("price").getAmount().fill(Amount.ZERO);

            if (!quantityToSell.isPositive() || price.isNegative()) {
                UserContext.message(Message.error("Invalid data"));
            } else {
                createOffer(userData, source, quantityToSell, price);
                UserContext.message(Message.success("Angebot erstellt"));
                ctx.respondWith().redirectToGet("/list-offers");
            }
        }

        ctx.respondWith().template("/templates/frontend/create-offer.html.pasta", userData);
    }

    private void createOffer(UserData seller, String source, Amount quantity, Amount price) {
        TradeOffer offer = new TradeOffer();
        offer.setId(UUID.randomUUID().toString());
        offer.setBuyer(seller);
        offer.setSeller(seller);
        offer.setSource(source);
        offer.setPrice(price);
        offer.setQuantity(quantity);

        connector.createTradeOffer(offer);
    }

    @Routed("/list-offers")
    @LoginRequired
    public void listOffers(WebContext ctx) {
        List<TradeOffer> tradeOffers = connector.fetchTradeOffers();
        ctx.respondWith().template("/templates/frontend/list-offers.html.pasta", tradeOffers);
    }

    @Routed("/accept-offer/:1")
    @LoginRequired
    public void acceptOffer(WebContext ctx, String offerId) {
        TradeOffer offer = connector.fetchTradeOffer(offerId);
        UserData userData = connector.fetchUserData(UserContext.getCurrentUser().getUserId());


        connector.acceptTradeOffer(offer, userData);
        ctx.respondWith().redirectToGet("/home");
    }
}
