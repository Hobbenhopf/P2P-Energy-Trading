package frontend;

import com.alibaba.fastjson.JSONObject;
import data.BlockchainConnector;
import data.PowerPlant;
import model.User;
import model.UserData;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;
import sirius.web.security.UserContext;
import sirius.web.services.JSONStructuredOutput;

@Register(classes = Controller.class)
public class AccessController extends BasicController {

    @Part
    private BlockchainConnector connector;

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
        UserContext.get().getUserManager().logout(ctx);
        ctx.respondWith().redirectToGet("/login");
    }

    @Routed("/home")
    @LoginRequired
    public void home(WebContext ctx) {
        User user = fetchUser();
        UserData userData = connector.fetchUserData(user.getUserId());

        PowerPlant.addUserData(userData);
        ctx.respondWith().template("/templates/frontend/home.html.pasta", user, userData);
    }

    @Routed(value = "/update-user-data", jsonCall = true)
    @LoginRequired
    public void updateUserData(WebContext ctx, JSONStructuredOutput out) {
        JSONObject request = ctx.getJSONContent();
        String userId = request.getString("userId");

        User user = connector.fetchUser(userId);
        user.setFirstname(request.getString("firstname"));
        user.setLastname(request.getString("lastname"));

        if (Strings.isEmpty(user.getFirstname())) {
            user.setFirstname(" ");
        }

        if (Strings.isEmpty(user.getLastname())) {
            user.setLastname(" ");
        }

        connector.updateUser(user);
    }

    private User fetchUser() {
        return connector.fetchUser(UserContext.getCurrentUser().getUserId());
    }
}
