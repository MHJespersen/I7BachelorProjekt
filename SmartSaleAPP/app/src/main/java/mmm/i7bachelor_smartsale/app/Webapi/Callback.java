package mmm.i7bachelor_smartsale.app.Webapi;

import mmm.i7bachelor_smartsale.app.Models.ExchangeRates;

//Idea inspired from https://stackoverflow.com/questions/3398363/how-to-define-callbacks-in-android
public interface Callback {
    void OnApiCallback(ExchangeRates exchangeRates);
}
