# MSwipe Wisepad Cordova plugin
Cordova plugin to communication with MSwipe Wisepad device via bluetooth.

## Platform Support
* android

# Usage

## How to add plugin
Type following command from CLI to add this plugin

```
    cordova plugin add cordova-plugin-k-mswipe
```

The plugin creates the object `KMswipe` into DOM.

## Methods

- [bluetoothSerial.isEnabled](#isEnabled)
- [bluetoothSerial.enable](#enable)


- [KMswipe.config](#config)
- [KMswipe.verifyMarchent](#verifyMarchent)
- [KMswipe.pay](#pay)
- [KMswipe.disconnect](#disconnect)



## isEnabled

[Use this plugin ](https://github.com/don/BluetoothSerial#isenabled)

## enable

[Use this plugin ](https://github.com/don/BluetoothSerial#enable)

## config

### Description

This method should call once while platform get ready. This should call once in per application lifecycle.

### Types

```
config(
    configuartion: MswipeConfig, 
    callbackSuccess: (res: any) => void, 
    callbackError: (err: any) => void
    ): void;
```

## verifyMarchent

### Description

Marchent should verified before making call the `pay` method. Marchent credential

### Types

```
verifyMarchent(
    credential: MarchentCredential,
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## pay

### Description

This method is reponsible to initiat a transaction via Wisepad device.

### Types

```
pay(
    paymentInfo: PaymentInfo,
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```

## disconnect

### Description

This method must call after success or failuar transaction.

### Types

```
disconnect(
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void): void;
```