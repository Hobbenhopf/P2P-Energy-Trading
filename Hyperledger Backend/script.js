
/**
 * @param {de.tbi.prototyp.GeneratePower} generate
 * @transaction 
 */
async function generatePower(generate) {
    if (generate.source === 'solar') {
        generate.userData.quantitySolar += generate.quantity;
    } else {
        generate.userData.quantityMisc += generate.quantity;
    }

    let assetRegistry = await getAssetRegistry('de.tbi.prototyp.UserData');
    await assetRegistry.update(generate.userData);
}

/**
 * @param {de.tbi.prototyp.ConsumePower} consume
 * @transaction 
 */
async function consumePower(consume) {
    consume.userData.quantitySolar -= consume.quantity;

    if (consume.userData.quantitySolar < 0) {
        consume.userData.quantityMisc += consume.userData.quantitySolar;
        consume.userData.quantitySolar = 0;
    }

    let assetRegistry = await getAssetRegistry('de.tbi.prototyp.UserData');
    await assetRegistry.update(consume.userData);
}

/**
 * @param {de.tbi.prototyp.AcceptOffer} offer
 * @transaction 
 */
async function acceptOffer(offer) {
    var buyer = offer.buyer;
    var seller = offer.tradeData.seller;

    offer.tradeData.buyer = offer.buyer;

    var quantityType = "quantityMisc";

    if (offer.tradeData.source === 'solar') {
        quantityType = "quantitySolar";
    }

    seller[quantityType] -= offer.tradeData.quantity;
    seller.balance += offer.tradeData.price;

    buyer[quantityType] += offer.tradeData.quantity;
    buyer.balance -= offer.tradeData.price;

    let userDataRegistry = await getAssetRegistry('de.tbi.prototyp.UserData');
    await userDataRegistry.update(buyer);
    await userDataRegistry.update(seller);

    let tradeOfferRegistry = await getAssetRegistry('de.tbi.prototyp.TradeOffer');
    await tradeOfferRegistry.update(offer.tradeData);
}


/**
 * Smart Contract function to trade an asset. 
 * 
 * @param {*} data the trade data object containing the buyer, seller and the asset 
 */
function doTrade(data) {
    // could do any kind of checks on the trade data
    if (!isValidCall(data)) {
        return Error("Invalid trade call");
    }


    data.buyer.balance -= 100;
    data.seller.balance += 100;
    data.asset.owner = data.buyer;

    update(data);
}