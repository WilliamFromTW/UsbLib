package inmethod.commons.usb;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.hid4java.event.HidServicesEvent;

public class UsbHidTools implements HidServicesListener {
	private HidServices hidServices;

	public UsbHidTools() {
		// Get HID services
		HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
		hidServicesSpecification.setAutoShutdown(true);
		hidServicesSpecification.setScanInterval(1000);
		hidServicesSpecification.setPauseInterval(5000);
		hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);

		hidServices = HidManager.getHidServices(hidServicesSpecification);
		hidServices.addHidServicesListener(this);
		hidServices.start();
	}

	public List<HidDevice> getHidDevices() {
		return hidServices.getAttachedHidDevices();
	}

	/**
	 * 
	 * @param VendorId
	 *            format: 0xffff
	 * @param Product_id
	 *            format: 0xffff
	 * @return
	 */
	public HidDevice connectHidDevice(int VendorId, int Product_id) {
		HidDevice hidDevice = hidServices.getHidDevice(VendorId, Product_id, null);
		if (hidDevice == null)
			return null;
		else {
			return hidDevice;
		}
	}

	/**
	 * rescan use hid devices.
	 */
	public void scanHidDevices() {
		if (hidServices != null)
			hidServices.scan();
	}

	/**
	 * this object will be shutdown.
	 */
	public void shutdownHidServices() {
		if (hidServices != null)
			hidServices.shutdown();
	}

	/**
	 * 
	 * @param hidDevice
	 * @param aCmd
	 *            size must <= PACKET_LENGTH
	 * @param iPACKET_LENGTH
	 *            data length be read every scan
	 * @return
	 */
	public byte[] getResponsedDataBySendCommand(HidDevice hidDevice, byte[] aCmd, int iPACKET_LENGTH) throws Exception {

		if (hidDevice == null)
			throw new Exception("error , message = HID device not open or null");
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}

		int val = hidDevice.write(aCmd, aCmd.length, (byte) 0x00);
		if (val >= 0) {
			;//System.out.println("command sent success!");
		} else {
			;//System.err.println(hidDevice.getLastErrorMessage());
			throw new Exception("error , message = " + hidDevice.getLastErrorMessage());
		}

		// Prepare to read a single data packet
		boolean moreData = true;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (moreData) {
			byte data[] = new byte[iPACKET_LENGTH];
			val = hidDevice.read(data, 1000);
			switch (val) {
			case -1:
				System.err.println(hidDevice.getLastErrorMessage());
				throw new Exception("error , message = " + hidDevice.getLastErrorMessage());
			case 0:
				moreData = false;
				break;
			default:
				baos.write(data);
				break;
			}
		}
		return baos.toByteArray();
	}

	@Override
	public void hidDeviceAttached(HidServicesEvent event) {
		;//System.out.println("Device attached: " + event);
	}

	@Override
	public void hidDeviceDetached(HidServicesEvent event) {
		;//System.err.println("Device detached: " + event);
	}

	@Override
	public void hidFailure(HidServicesEvent event) {
		;//System.err.println("HID failure: " + event);
	}

}
