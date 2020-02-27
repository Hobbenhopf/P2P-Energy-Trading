package frontend;

import data.BlockchainConnector;
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

@Register(classes = Controller.class)
public class PaymentController extends BasicController {

    @Part
    private BlockchainConnector connector;


    @Routed("/balance")
    @LoginRequired
    public void showBalance(WebContext ctx) {
        User user = connector.fetchUser(UserContext.getCurrentUser().getUserId());
        UserData userData = connector.fetchUserData(user.getUserId());


        if (ctx.isUnsafePOST()) {
            Amount quantityChange = ctx.get("amount").getAmount().fill(Amount.ZERO);
            userData.setBalance(userData.getBalance().add(quantityChange));

            if (userData.getBalance().isNegative()) {
                UserContext.message(Message.error("You can not withdraw more money than you have."));
            } else {
                connector.updateUserData(userData);
            }
        }

        ctx.respondWith().template("/templates/frontend/balance.html.pasta", userData);
    }
}
