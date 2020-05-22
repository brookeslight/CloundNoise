package main;

import java.awt.image.BufferedImage;
import java.util.Random;

public class CloudNoise {
	private float[][] noise;
	
	public CloudNoise(int width, int height, int cells) {
		//variables (works best when cells is a perfect square) and the sqrt of cell size is a factor of the size
		int cellPerAxisX = (int) Math.sqrt(cells);
		int cellPerAxisY = (int) Math.sqrt(cells);
		int cellSizeX = width/cellPerAxisX;
		int cellSizeY = height/cellPerAxisY;
		double maxDistance = Math.hypot(cellSizeX, cellSizeY);
		
		//create points
		Random r = new Random();
		Point[][] points = new Point[cellPerAxisX][cellPerAxisY];
		for(int i = 0; i < cellPerAxisX; i++) {
			for(int j = 0; j < cellPerAxisY; j++) {
				float x = (float)(i*cellSizeX + r.nextDouble()*(((i+1)*cellSizeX)-(i*cellSizeX)));
				float y = (float)(j*cellSizeY + r.nextDouble()*(((j+1)*cellSizeY)-(j*cellSizeY)));
				points[i][j] = new Point(x, y);
			}
		}
		
		//tile
		Point[][] tiledPoints = new Point[(3*cellPerAxisX)][(3*cellPerAxisY)];
		for(int a = 0; a < 3; a++) {
			for(int b = 0; b < 3; b++) {
				for(int i = 0; i < cellPerAxisX; i++) {
					for(int j = 0; j < cellPerAxisY; j++) {
						float x = points[i][j].x + cellSizeX*a*cellPerAxisX;
						float y = points[i][j].y + cellSizeY*b*cellPerAxisY;
						tiledPoints[((a*cellPerAxisX)+i)][((b*cellPerAxisY)+j)] = new Point(x, y);
					}
				}
			}
		}
		//generate
		float[][] tiledNoise = new float[3*width][3*height];
		for(int x = 0; x < tiledNoise.length; x++) {
			for(int y = 0; y < tiledNoise[0].length; y++) {
				tiledNoise[x][y] = (float) ((this.shortestDistance(x, y, cellSizeX, cellSizeY, tiledPoints) / maxDistance));
			}
		}
		//sub array noise
		this.noise = new float[width][height];
		for(int i = 0; i < noise.length; i++) {
			for(int j = 0; j < noise[0].length; j++) {
				noise[i][j] = tiledNoise[width+i][height+j]; //might be width+1
			}
		}
	}
	
	private double shortestDistance(int x, int y, int cellSizeX, int cellSizeY, Point[][] points) {
		double shortestDistance = Double.MAX_VALUE;
		int i = (x/cellSizeX)-1;
		int j = (y/cellSizeY)-1;
		for(int a = 0; a < 3; a++) {
			for(int b = 0; b < 3; b++) {
				if((a+i) >= 0 && (b+j) >= 0 && (a+i) < points.length && (b+j) < points[0].length) {
					double d = points[(a+i)][(b+j)].distanceTo(x, y);
					if(d < shortestDistance) {
						shortestDistance = d;
					}
				}
			}
		}
		return shortestDistance;
	}
	
	public BufferedImage toImage() {
		BufferedImage img = new BufferedImage(this.noise.length, this.noise[0].length, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				int o = (int) (this.noise[x][y]*255);
				img.setRGB(x, y, ((o&0x0ff)<<16)|((o&0x0ff)<<8)|(o&0x0ff));
			}
		}
		return img;
	}
	
	public float get(int x, int y) {
		int i = x;
		int j = y;
		if(x < 0 || x >= this.noise.length) {
			i = x - this.noise.length*(x/this.noise.length);
		}
		if(y < 0 || y >= this.noise.length) {
			j = y - this.noise[0].length*(y/this.noise[0].length);
		}
		return this.noise[i][j];
	}
	
	class Point {
		private float x;
		private float y;
		
		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		//returns distance to point
		public double distanceTo(int x, int y) {
			return Math.hypot((x-this.x), (y-this.y));
		}
		
		@Override
		public String toString() {
			return "(" + this.x + "," + this.y + ")";
		}
	}
}