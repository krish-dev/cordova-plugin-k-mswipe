var exec = require('cordova/exec');
/**
 * @name KMswipe
 * @description This plugin is responsible to communicate with MSwipe Wisepad device
 * @author Krishnendu Sekhar Das
 */
function KMswipe() { }

KMswipe.prototype.config = function (configuartion, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'KMswipe', 'config', [configuartion]);
};

KMswipe.prototype.verifyMarchent = function (credential, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'KMswipe', 'verifyMarchent', [credential]);
};

KMswipe.prototype.pay = function (paymentData, successCallback, errorCallback) {
    let paymentInfo = {
        amount: paymentData.amount,
        mobileNumber: paymentData.mobileNumber,
        invoiceNumber: paymentData.invoiceNumber,
        email: paymentData.hasOwnProperty('email') && paymentData.email ? paymentData.email : "",
        notes: paymentData.hasOwnProperty('notes') && paymentData.notes ? paymentData.notes : "",
        extraNote1: paymentData.hasOwnProperty('extraNote1') && paymentData.extraNote1 ? paymentData.extraNote1 : "",
        extraNote2: paymentData.hasOwnProperty('extraNote2') && paymentData.extraNote2 ? paymentData.extraNote2 : "",
        extraNote3: paymentData.hasOwnProperty('extraNote3') && paymentData.extraNote3 ? paymentData.extraNote3 : "",
        extraNote4: paymentData.hasOwnProperty('extraNote4') && paymentData.extraNote4 ? paymentData.extraNote4 : "",
        extraNote5: paymentData.hasOwnProperty('extraNote5') && paymentData.extraNote5 ? paymentData.extraNote5 : "",
        extraNote6: paymentData.hasOwnProperty('extraNote6') && paymentData.extraNote6 ? paymentData.extraNote6 : "",
        extraNote7: paymentData.hasOwnProperty('extraNote7') && paymentData.extraNote7 ? paymentData.extraNote7 : "",
        extraNote8: paymentData.hasOwnProperty('extraNote8') && paymentData.extraNote8 ? paymentData.extraNote8 : "",
        extraNote9: paymentData.hasOwnProperty('extraNote9') && paymentData.extraNote9 ? paymentData.extraNote9 : "",
        extraNote10: paymentData.hasOwnProperty('extraNote10') && paymentData.extraNote10 ? paymentData.extraNote10 : ""
    }
    exec(successCallback, errorCallback, 'KMswipe', 'pay', [paymentInfo]);
};

KMswipe.prototype.disconnect = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'KMswipe', 'disconnect', []);
};

module.exports = new KMswipe();