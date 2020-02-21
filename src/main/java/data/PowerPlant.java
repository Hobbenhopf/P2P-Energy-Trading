package data;

import model.UserData;
import sirius.kernel.async.BackgroundLoop;
import sirius.kernel.commons.Amount;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Register(classes = BackgroundLoop.class)
public class PowerPlant extends BackgroundLoop {

    private static List<UserData> currentUserDataList = new ArrayList<>();

    @Part
    private BlockchainConnector connector;

    @Override
    public double maxCallFrequency() {
        return 0.1;
    }

    @Nonnull
    @Override
    public String getName() {
        return "power-plant";
    }

    @Nullable
    @Override
    protected String doWork() throws Exception {
        currentUserDataList.forEach(userData -> {
            userData = connector.fetchUserData(userData.getUser().getUserId());
            userData.setQuantitySolar(fillAmount(userData.getQuantitySolar()));
            userData.setQuantityMisc(fillAmount(userData.getQuantityMisc()));


            connector.updateUserData(userData);
        });

        return null;
    }

    public static synchronized void addUserData(UserData userData) {
        if (currentUserDataList.stream().map(UserData::getId).noneMatch(id -> Strings.areEqual(id, userData.getId()))) {
            currentUserDataList.add(userData);
        }
    }

    private Amount fillAmount(Amount currentAmount) {
        if (currentAmount.isZero()) {
            return currentAmount.add(Amount.of(1000).times(Amount.of(Math.random())));
        }

        return currentAmount.add(Amount.of(50).times(Amount.of(Math.random())));
    }
}
