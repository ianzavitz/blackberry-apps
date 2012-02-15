package com.zavitz.mytimes;

import java.io.*;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.*;

public class Images {

	public static Bitmap clock;
	public static final int A = 10;
	public static final int P = 11;
	public static final int M = 12;
	public static final int COLON = 13;
	
	static {
		clock = getImage("clock.png");
	}
	
	public static Bitmap getImage(String s) {
		Bitmap toReturn = null;
		InputStream input = (Images.class).getResourceAsStream("/" + s);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			copy(input,output);
		} catch(Exception e) {}
		byte[] buff = output.toByteArray();
		EncodedImage image = EncodedImage.createEncodedImage(buff, 0, buff.length);
		toReturn = image.getBitmap();
		return toReturn;
	}
	
	public static Bitmap getScaledImage(String s, int width) {
		Bitmap toReturn = null;
		InputStream input = (Images.class).getResourceAsStream("/" + s);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			copy(input,output);
		} catch(Exception e) {}
		byte[] buff = output.toByteArray();
		EncodedImage image = EncodedImage.createEncodedImage(buff, 0, buff.length);
		image = scaleImageToWidth(image,width);
		toReturn = image.getBitmap();
		return toReturn;
	}
	
	public static EncodedImage scaleImageToWidth(EncodedImage encoded, int newWidth) {
		return scaleToFactor(encoded, encoded.getWidth(), newWidth);
	}
	
	public static EncodedImage scaleImageToHeight(EncodedImage encoded, int newHeight) {
		return scaleToFactor(encoded, encoded.getHeight(), newHeight);
	}
	
	public static EncodedImage scaleToFactor(EncodedImage encoded, int curSize, int newSize) {
		int numerator = Fixed32.toFP(curSize);
		int denominator = Fixed32.toFP(newSize);
		int scale = Fixed32.div(numerator, denominator);

		return encoded.scaleImage32(scale, scale);
	}
	
	public static Bitmap getScaledImage(String s, int width, int height) {
		Bitmap toReturn = null;
		InputStream input = (Images.class).getResourceAsStream("/" + s);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			copy(input,output);
		} catch(Exception e) {}
		byte[] buff = output.toByteArray();
		EncodedImage image = EncodedImage.createEncodedImage(buff, 0, buff.length).scaleImage32(width, height);
		toReturn = image.getBitmap();
		return toReturn;
	}
	
    private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
    	byte[] s_copyBuffer = new byte[65536];
    	synchronized (s_copyBuffer) {
            for (int bytesRead = inputStream.read(s_copyBuffer); bytesRead >= 0; bytesRead = inputStream.read(s_copyBuffer))
                if (bytesRead > 0)
                    outputStream.write(s_copyBuffer, 0, bytesRead);
        }
    }
	
}
