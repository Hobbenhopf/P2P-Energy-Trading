package model;

import sirius.kernel.commons.Amount;
import sirius.kernel.commons.Strings;

public class UserData {

    private String id;
    private Amount quantitySolar = Amount.ZERO;
    private Amount quantityMisc = Amount.ZERO;
    private Amount balance = Amount.ZERO;

    private User user;
    private SmartMeter smartMeter;

    public String generateBlockchainReference() {
        return "resource:de.tbi.prototyp.UserData#" + id;
    }

    public static String parseUserIdFromReference(String reference) {
        return Strings.split(Strings.split(reference, "resource:de.tbi.prototyp.UserData#").getSecond(), "userdata").getFirst();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Amount getQuantitySolar() {
        return quantitySolar;
    }

    public void setQuantitySolar(Amount quantitySolar) {
        this.quantitySolar = quantitySolar;
    }

    public Amount getQuantityMisc() {
        return quantityMisc;
    }

    public void setQuantityMisc(Amount quantityMisc) {
        this.quantityMisc = quantityMisc;
    }

    public Amount getBalance() {
        return balance;
    }

    public void setBalance(Amount balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SmartMeter getSmartMeter() {
        return smartMeter;
    }

    public void setSmartMeter(SmartMeter smartMeter) {
        this.smartMeter = smartMeter;
    }
}
