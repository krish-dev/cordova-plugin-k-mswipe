import { GatewayEnvironment, NetworkSource } from "./enums";

export interface Mswipe {
    config(
        configuartion: MswipeConfig,
        callbackSuccess: (res: any) => void,
        callbackError: (err: any) => void): void;

    verifyMarchent(
        credential: MarchentCredential,
        callbackSuccess: (res: any) => void,
        callbackError: (err: any) => void): void;

    pay(
        paymentInfo: PaymentInfo,
        callbackSuccess: (res: any) => void,
        callbackError: (err: any) => void): void;

    disconnect(
        callbackSuccess: (res: any) => void,
        callbackError: (err: any) => void): void;

}

export interface MswipeConfig {
    environment: GatewayEnvironment;
    network: NetworkSource
}

export interface MarchentCredential {
    userId: string;
    pin: string;
}

export interface PaymentInfo {
    amount: string;
    mobileNumber: string;
    invoiceNumber: string;
    email?: string;
    notes?: string;
    extraNote1?: string;
    extraNote2?: string;
    extraNote3?: string;
    extraNote4?: string;
    extraNote5?: string;
    extraNote6?: string;
    extraNote7?: string;
    extraNote8?: string;
    extraNote9?: string;
    extraNote10?: string;
}

export interface TransactionInfo {
    bankName: string;
    merchantName: string;
    merchantAdd: string;
    dateTime: string;
    mId: string;
    tId: string;
    batchNo: string;
    voucherNo: string;
    refNo: string;
    saleType: string;
    cardNo: string;
    cardLast4Digits: string;
    trxType: string;
    cardType: string;
    expDate: string;
    emvSigExpDate: string;
    cardHolderName: string;
    currency: string;
    cashAmount: string;
    baseAmount: string;
    tipAmount: string;
    totalAmount: string;
    authCode: string;
    rrNo: string;
    certif: string;
    appId: string;
    appName: string;
    tvr: string;
    tsi: string;
    appVersion: string;
    isPinVarifed: string;
    stan: string;
    cardIssuer: string;
    emiPerMonthAmount: string;
    total_Pay_Amount: string;
    noOfEmi: string;
    interestRate: string;
    billNo: string;
    firstDigitsOfCard: string;
    isConvenceFeeEnable: string;
    invoiceNo: string;
    trxDate: string;
    trxImgDate: string;
    chequeDate: string;
    chequeNo: string;
}
