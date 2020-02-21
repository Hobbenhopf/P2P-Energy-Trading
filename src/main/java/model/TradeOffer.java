package model;

import sirius.kernel.commons.Amount;

public class TradeOffer {
    private String id;
    private String source;
    private Amount quantity;
    private Amount price;

    private UserData seller;
    private UserData buyer;

    public String generateBlockchainReference() {
        return "resource:de.tbi.prototyp.TradeOffer#" + id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Amount getQuantity() {
        return quantity;
    }

    public void setQuantity(Amount quantity) {
        this.quantity = quantity;
    }

    public Amount getPrice() {
        return price;
    }

    public void setPrice(Amount price) {
        this.price = price;
    }

    public UserData getSeller() {
        return seller;
    }

    public void setSeller(UserData seller) {
        this.seller = seller;
    }

    public UserData getBuyer() {
        return buyer;
    }

    public void setBuyer(UserData buyer) {
        this.buyer = buyer;
    }
}
