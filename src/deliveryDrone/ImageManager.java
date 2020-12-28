package deliveryDrone;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageManager {
	
	BufferedImage backgroundImg;
	BufferedImage houseImg;
	BufferedImage warehouseImg;
	BufferedImage chargingStationImg;

	BufferedImage packageImg_TH;
	BufferedImage packageImg_TWH;
	BufferedImage envelopeImg;

	BufferedImage lightningImg;
	BufferedImage greenLightImg;
	BufferedImage redLightImg;
	BufferedImage greenArrowImg;
	BufferedImage redArrowImg;
	
	BufferedImage ffIconImg;
	
	ImageManager(){
		try {
			backgroundImg = ImageIO.read(new File("img/background_grass.jpeg"));
			houseImg = ImageIO.read(new File("img/red_house_gray.png"));
			warehouseImg = ImageIO.read(new File("img/warehouse.png"));
			chargingStationImg = ImageIO.read(new File("img/charging_station.png"));

			packageImg_TWH = ImageIO.read(new File("img/package_small.png"));
			packageImg_TH = ImageIO.read(new File("img/package_bright_color.png"));
			envelopeImg = ImageIO.read(new File("img/envelopes_red_blue.png"));

			lightningImg = ImageIO.read(new File("img/lightning.png"));
			redLightImg = ImageIO.read(new File("img/RED_LED.png"));
			greenLightImg = ImageIO.read(new File("img/GREEN_LED.png"));
			redArrowImg = ImageIO.read(new File("img/arrow_red.png"));
			greenArrowImg = ImageIO.read(new File("img/arrow_green.png"));
			ffIconImg = ImageIO.read(new File("img/ff_icon.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage(String imageName) {
		switch(imageName) {
			case "backgroundImg":
				return backgroundImg;
			case "houseImg":
				return houseImg;
			case "warehouseImg":
				return warehouseImg;
			case "chargingStationImg":
				return chargingStationImg;
			case "packageImg_TWH":
				return packageImg_TWH;
			case "packageImg_TH":
				return packageImg_TH;
			case "envelopeImg":
				return envelopeImg;
			case "lightningImg":
				return lightningImg;
			case "redLightImg":
				return redLightImg;
			case "greenLightImg":
				return greenLightImg;
			case "redArrowImg":
				return redArrowImg;
			case "greenArrowImg":
				return greenArrowImg;
			case "ffIconImg":
				return ffIconImg;
			default:
				return null;
		}
	}

}
