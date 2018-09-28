
package inmethod.commons.usb.test;

import inmethod.commons.usb.*;

import org.hid4java.HidDevice;

public class testUsb {

	private static final Integer VENDOR_ID = 0x1241;
	private static final Integer PRODUCT_ID = 0x0150;
	private static final int PACKET_LENGTH = 8;

	public static void main(String[] args) {
		UsbHidTools aUsbHidTools = new UsbHidTools();

		for (HidDevice a : aUsbHidTools.getHidDevices()) {
			System.out.println(a);
		}
		HidDevice aHidDevice = aUsbHidTools.connectHidDevice(VENDOR_ID, PRODUCT_ID);
		if (aHidDevice != null) {
			byte[] aCmd = new byte[1];
			aCmd[0] = 0x09;
			try {
				byte[] aResultSet = aUsbHidTools.getResponsedDataBySendCommand(aHidDevice, aCmd, PACKET_LENGTH);
				System.out.print("< [");
				for (byte b : aResultSet) {
					System.out.printf(" %02x", b);
				}
				System.out.println("]");
				aCmd[0] = 0x00;
				aResultSet = aUsbHidTools.getResponsedDataBySendCommand(aHidDevice, aCmd, PACKET_LENGTH);
				for (byte b : aResultSet) {
					System.out.printf(" %02x", b);
				}
				System.out.println("]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("no use device");
		}
		aUsbHidTools.shutdownHidServices();
		aUsbHidTools = null;
	}

}
