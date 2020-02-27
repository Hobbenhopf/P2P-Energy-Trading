package data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import model.SmartMeter;
import model.TradeOffer;
import model.User;
import model.UserData;
import sirius.kernel.Sirius;
import sirius.kernel.commons.Amount;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.Exceptions;
import sirius.kernel.xml.Outcall;
import sirius.kernel.xml.StructuredOutput;
import sirius.web.services.JSONCall;
import sirius.web.services.JSONStructuredOutput;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Register(classes = BlockchainConnector.class)
public class BlockchainConnector {

    private JSONCall get(String url) throws IOException {
        String baseUrl = Sirius.getSettings().get("blockchain.url").asString();
        return JSONCall.to(new URL(baseUrl + url));
    }


    private JSONCall post(String url, Consumer<JSONStructuredOutput> requestConsumer) throws IOException, NoSuchFieldException, IllegalAccessException {
        String baseUrl = Sirius.getSettings().get("blockchain.url").asString();
        JSONCall call = JSONCall.to(new URL(baseUrl + url));
        setRequestMode(call, "POST");

        JSONStructuredOutput out = call.getOutput();
        out.beginResult();
        requestConsumer.accept(out);
        out.endResult();

        return call;
    }

    private JSONCall put(String url, Consumer<JSONStructuredOutput> requestConsumer) throws IOException, NoSuchFieldException, IllegalAccessException {
        String baseUrl = Sirius.getSettings().get("blockchain.url").asString();
        JSONCall call = JSONCall.to(new URL(baseUrl + url));
        setRequestMode(call, "PUT");

        JSONStructuredOutput out = call.getOutput();
        out.beginResult();
        requestConsumer.accept(out);
        out.endResult();

        return call;
    }

    public User fetchUser(String userId) {
        try {
            return fetchUserFromBlockchain(userId);
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private User fetchUserFromBlockchain(String userId) throws IOException, NoSuchFieldException, IllegalAccessException {
        JSONCall call = get("/api/User/" + userId);

        User user = new User();
        user.setUserId(userId);

        try {
            JSONObject response = call.getInput();

            user.setFirstname(response.getString("firstName"));
            user.setLastname(response.getString("lastName"));
        } catch (FileNotFoundException e) {
            Exceptions.ignore(e);
            tryCreateUser(userId);
        }

        return user;
    }

    private void tryCreateUser(String userId) throws IOException, NoSuchFieldException, IllegalAccessException {
        JSONCall call = post("/api/User", out -> {
            out.property("$class", "de.tbi.prototyp.User");
            out.property("userId", userId);
            out.property("firstName", " ");
            out.property("lastName", " ");
        });

        call.getInput();
    }

    public void updateUser(User user) {
        JSONCall call = null;
        try {
            call = put("/api/User/" + user.getUserId(), out -> {
                out.property("$class", "de.tbi.prototyp.User");
                out.property("userId", user.getUserId());
                out.property("firstName", user.getFirstname());
                out.property("lastName", user.getLastname());
            });

            call.getInput();
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }

    }

    public synchronized UserData fetchUserData(String userId) {
        try {
            return fetchuserDataFromBlockchain(userId);
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private UserData fetchuserDataFromBlockchain(String userId) throws IOException, NoSuchFieldException, IllegalAccessException {
        String userDataId = getUserDataId(userId);

        User user = fetchUser(userId);
        SmartMeter smartMeter = fetchSmartMeter(userId);
        UserData userData = new UserData();
        userData.setId(userDataId);
        userData.setSmartMeter(smartMeter);
        userData.setUser(user);

        JSONCall call = get("/api/UserData/" + userDataId);

        try {
            JSONObject response = call.getInput();

            userData.setBalance(Amount.ofMachineString(response.getString("balance")));
            userData.setQuantityMisc(Amount.ofMachineString(response.getString("quantityMisc")));
            userData.setQuantitySolar(Amount.ofMachineString(response.getString("quantitySolar")));
        } catch (FileNotFoundException e) {
            Exceptions.ignore(e);
            tryCreateUserData(userData);
        }

        return userData;
    }

    private void tryCreateUserData(UserData userData) throws IOException, NoSuchFieldException, IllegalAccessException {
        JSONCall call = post("/api/UserData", out -> writeUserData(out, userData));
        call.getInput();
    }

    public synchronized void updateUserData(UserData userData) {
        JSONCall call = null;
        try {
            call = put("/api/UserData/" + userData.getId(), out -> writeUserData(out, userData));
            call.getInput();
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private void writeUserData(StructuredOutput out, UserData userData) {
        out.property("$class", "de.tbi.prototyp.UserData");
        out.property("userDataId", userData.getId());
        out.property("quantitySolar", userData.getQuantitySolar().getAmount());
        out.property("quantityMisc", userData.getQuantityMisc().getAmount());
        out.property("balance", userData.getBalance().getAmount());
        out.property("user", userData.getUser().generateBlockchainReference());
        out.property("smartMeter", userData.getSmartMeter().generateBlockchainReference());
    }

    private String getUserDataId(String userId) {
        return userId + "userdata";
    }

    private void setRequestMode(JSONCall call, String mode) throws NoSuchFieldException, IllegalAccessException, ProtocolException {
        Outcall outcall = call.getOutcall();
        Field connectionField = outcall.getClass().getDeclaredField("connection");
        connectionField.setAccessible(true);

        HttpURLConnection connection = (HttpURLConnection) connectionField.get(outcall);
        connection.setRequestMethod(mode);
    }


    public SmartMeter fetchSmartMeter(String userId) {
        try {
            return fetchSmartMeterFromBlockchain(userId);
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private SmartMeter fetchSmartMeterFromBlockchain(String userId) throws IOException, NoSuchFieldException, IllegalAccessException {
        String smartMeterId = generateSmartMeterId(userId);
        JSONCall call = get("/api/SmartMeter/" + smartMeterId);

        SmartMeter smartMeter = new SmartMeter();
        smartMeter.setSmartMeterId(smartMeterId);

        try {
            JSONObject response = call.getInput();

            smartMeter.setLocation(response.getString("location"));
        } catch (FileNotFoundException e) {
            Exceptions.ignore(e);
            tryCreateSmartMeter(userId);
        }

        return smartMeter;
    }

    private void tryCreateSmartMeter(String userId) throws IOException, NoSuchFieldException, IllegalAccessException {
        JSONCall call = post("/api/SmartMeter", out -> {
            out.property("$class", "de.tbi.prototyp.SmartMeter");
            out.property("smartMeterId", generateSmartMeterId(userId));
            out.property("location", " ");
        });

        call.getInput();
    }

    private String generateSmartMeterId(String userId) {
        return "SmartMeter-" + userId;
    }

    public List<TradeOffer> fetchTradeOffers() {

        try {
            JSONCall call = get("/api/TradeOffer");
            JSONArray response = JSON.parseArray(new String(ByteStreams.toByteArray(call.getOutcall().getInput()), call.getOutcall().getContentEncoding()));

            List<TradeOffer> offers = new ArrayList<>();

            for (int i = 0; i < response.size(); i++) {
                TradeOffer offer = parseTradeOffer(response.getJSONObject(i));

                if (offer.getBuyer().getId().equals(offer.getSeller().getId())) {
                    offers.add(offer);
                }
            }

            return offers;
        } catch (IOException e) {
            Exceptions.ignore(e);
        }
        return Collections.emptyList();
    }

    public TradeOffer fetchTradeOffer(String offerId) {
        try {
            return fetchTradeOfferFromBlockchain(offerId);
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private TradeOffer fetchTradeOfferFromBlockchain(String offerId) throws IOException, NoSuchFieldException, IllegalAccessException {
        JSONCall call = get("/api/TradeOffer/" + offerId);

        try {
            JSONObject response = call.getInput();
            return parseTradeOffer(response);
        } catch (FileNotFoundException e) {
            throw Exceptions.createHandled().error(e).withSystemErrorMessage("Could not find the offer").handle();
        }
    }

    private TradeOffer parseTradeOffer(JSONObject response) {
        TradeOffer offer = new TradeOffer();
        offer.setId(response.getString("tradeOfferId"));
        offer.setQuantity(Amount.ofMachineString(response.getString("quantity")));
        offer.setPrice(Amount.ofMachineString(response.getString("price")));
        offer.setSource(response.getString("source"));

        String buyer = UserData.parseUserIdFromReference(response.getString("buyer"));
        String seller = UserData.parseUserIdFromReference(response.getString("seller"));

        offer.setBuyer(fetchUserData(buyer));
        offer.setSeller(fetchUserData(seller));
        return offer;
    }

    public void createTradeOffer(TradeOffer offer) {
        try {
            JSONCall call = post("/api/TradeOffer", out -> writeToOutput(out, offer));

            call.getInput();
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    public void acceptTradeOffer(TradeOffer offer, UserData buyer) {
        try {
            JSONCall call = post("/api/AcceptOffer", out -> {
                out.property("$class", "de.tbi.prototyp.AcceptOffer");
                out.property("tradeData", offer.generateBlockchainReference());
                out.property("buyer", buyer.generateBlockchainReference());
            });

            call.getInput();
        } catch (Exception e) {
            throw Exceptions.handle(e);
        }
    }

    private void writeToOutput(StructuredOutput out, TradeOffer offer) {
        out.property("$class", "de.tbi.prototyp.TradeOffer");
        out.property("tradeOfferId", offer.getId());
        out.property("quantity", offer.getQuantity().getAmount());
        out.property("price", offer.getPrice().getAmount());
        out.property("source", offer.getSource());
        out.property("seller", offer.getSeller().generateBlockchainReference());
        out.property("buyer", offer.getBuyer().generateBlockchainReference());
    }
}
