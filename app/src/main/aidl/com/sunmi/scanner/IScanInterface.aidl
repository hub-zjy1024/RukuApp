// IScanInterface.aidl
package com.sunmi.scanner;

// Declare any non-default types here with import statements

 interface IScanInterface {
    void sendKeyEvent(in KeyEvent key);
    void scan();
    void stop();
    int getScannerModel();
}
