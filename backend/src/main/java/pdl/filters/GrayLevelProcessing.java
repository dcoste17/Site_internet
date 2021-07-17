package imageProcessing;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import io.scif.SCIFIO;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*; 
import java.awt.Color;
import javax.swing.*; 
import java.lang.reflect.Field; 
import net.imglib2.Cursor;

public class GrayLevelProcessing{

	public static void threshold(Img<UnsignedByteType> img, int t) {
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		long begin = System.nanoTime();
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				if (val.get() < t)
				    val.set(0);
				else
				    val.set(255);
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );

	}
	public void rgbToHsv(int r, int g, int b, float[] hsv){
		int max = r;
		int min = r;
		if(g > max){
			max = g;
		}
		if(b > max){
			max = b;
		}
		if(g < min){
			min = g;
		}
		if(b < min){
			min = b;
		}
		hsv[2] = max;
		if(max == 0){
			hsv[1] = 0;
		}
		if(max != 0){
			hsv[1] = 1-(min/max);
		}
		if(max == min){
			hsv[0] = 0;
		}
		if(max == r){
			hsv[0] = (60 * (g-b)/(max-min) + 360) % 360;
		}
		if(max == g){
			hsv[0] = (60 * (b-r)/(max-min) + 120);
		}
		if(max == b){
			hsv[0] = (60 * (r-g)/(max-min) + 240);
		}

}
public void hsvToRgb(float h, float s, float v, int[] rgb){
		float hi = (h/60) % 60;
		float f = h/60-hi;
		float l = v * (1-s);
		float m = v * (1 - f * s);
		float n = v / (1-(1-f) * s);
		if(hi == 0){
			rgb[0] = (int)v;
			rgb[1] = (int)n;
			rgb[2] = (int)l;
		}
		if(hi == 1){
			rgb[0] = (int)m;
			rgb[1] = (int)v;
			rgb[2] = (int)l;
		}
		if(hi == 2){
			rgb[0] = (int)l;
			rgb[1] = (int)v;
			rgb[2] = (int)n;
		}
		if(hi == 3){
			rgb[0] = (int)l;
			rgb[1] =(int) m;
			rgb[2] = (int)v;
		}
		if(hi == 4){
			rgb[0] = (int)n;
			rgb[1] = (int)l;
			rgb[2] = (int)v;
		}
		if(hi == 5){
			rgb[0] = (int)v;
			rgb[1] = (int)l;
			rgb[2] = (int)m;
		}
}

	public static void luminosity(Img<UnsignedByteType> img, int delta){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();
		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		/*int r_c[];
		int g = 0;
		int b = 0;*/
		long begin = System.nanoTime();
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				//BufferedImage dst = new BufferedImage(iw, ih, img.getType());
				//r_c = dst.getRGB(0, 0, iw, ih, null, 0, iw);
				int value = val.get();
				if(value+delta > 255){
					val.set(254);
				}
				if(value+delta < 0){
					val.set(0);
				}
				else{
					val.set(val.get()+delta);
				}
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}

	public static void color_img(Img<UnsignedByteType> img, int teinte){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();
		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		int [] r_c = new int [3];
		float [] hsv = new float [3];
		int g = 0;
		int b = 0;
		long begin = System.nanoTime();
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				//BufferedImage dst = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
				//r_c = dst.getRGB(0, 0, iw, ih, null, 0, iw);
				//rgbToHsv(r_c[0], r_c[1], r_c[2], hsv);
				int value = val.get();
				
				val.set(val.get()+teinte);
				
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}

	public static void luminosityWithCursor(Img<UnsignedByteType> img, int delta){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();
		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		final Cursor< UnsignedByteType > cursor = img.cursor();
		long begin = System.nanoTime();
		while ( cursor.hasNext() )
        {
            cursor.fwd();
            final UnsignedByteType val = cursor.get();
			if(val.get()+delta > 255){
				val.set(255);
			}
			if(val.get()+delta < 0){
				val.set(0);
			}
			else{
				val.set(val.get()+delta);
			}
		
        }
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}

	public static void extend_dynamique(Img<UnsignedByteType> img, int pixel_min, int pixel_max){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		int min_luminosity = 255;
		int max_luminosity = 0;
		int new_value = 0;
		long begin = System.nanoTime();
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				if (val.get() < min_luminosity){
					min_luminosity = val.get();
				}
				if(val.get() > max_luminosity){
					max_luminosity = val.get();
				}
			}
		}System.out.printf("%d", min_luminosity);
		System.out.printf("%d", max_luminosity);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				new_value = ((val.get() - min_luminosity)*255) / (max_luminosity - min_luminosity);
				if(new_value < pixel_min){
					val.set(pixel_min);
				} 
				else if(new_value > pixel_max){
					val.set(pixel_max);
				}
				else{
					val.set(new_value);
				}
				
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}

	public static void extend_dynamique_With_Table(Img<UnsignedByteType> img, int pixel_min, int pixel_max){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();

		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		int min_luminosity = 255;
		int max_luminosity = 0;
		int [] values = new int [256];
		int new_value;
		long begin = System.nanoTime();
		for (int i = 0; i <= 255; ++i){
			values[i] = ((i - min_luminosity)*255) / (max_luminosity - min_luminosity);
		}
		
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				if (val.get() < min_luminosity){
					min_luminosity = val.get();
				}
				if(val.get() > max_luminosity){
					max_luminosity = val.get();
				}
			}
		}System.out.printf("%d", min_luminosity);
		System.out.printf("%d", max_luminosity);

		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				new_value = values[val.get()];
				if(new_value < pixel_min){
					val.set(pixel_min);
				} 
				else if(new_value > pixel_max){
					val.set(pixel_max);
				}
				else{
					val.set(new_value);
				}
				
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}

	public static void egalisation_hist(Img<UnsignedByteType> img){
		final RandomAccess<UnsignedByteType> r = img.randomAccess();
		final int iw = (int) img.max(0);
		final int ih = (int) img.max(1);
		int [] values_hist = new int [256];
		int [] hist_cumule = new int [256];
		int value_cumule = 0;
		int result = 0;
		int nbr_pixels = 0;
		long begin = System.nanoTime();
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				values_hist[val.get()]++;
				nbr_pixels++;
				
			}
		}
		for (int i = 0; i < 256; ++i){
			value_cumule = value_cumule + values_hist[i];
			hist_cumule[i] = value_cumule;
		}
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				result = (hist_cumule[val.get()] * 255) / nbr_pixels;
				val.set(result);
				
			}
		}
		long end = System.nanoTime();
		long duration = end - begin;
		System.out.printf( "temps écoulé : %d", duration );
	}
	
	public static void main(final String[] args) throws ImgIOException, IncompatibleTypeException {
		// load image
		if (args.length < 2) {
			System.out.println("missing input and/or output image filenames");
			System.exit(-1);
		} 
		final String filename = args[0];
		if (!new File(filename).exists()) {
			System.err.println("File '" + filename + "' does not exist");
			System.exit(-1);
		}

		final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
		final ImgOpener imgOpener = new ImgOpener();
		final Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(filename, factory).get(0);
		imgOpener.context().dispose();

		// process image
		//threshold(input, 200);
		//thresholdWithCursor(input, 200);
		//luminosity(input, -50);
		//luminosityWithCursor(input, 60);
		//extend_dynamique(input, 70, 180);
		//extend_dynamique_With_Table(input, 70, 100);
		//egalisation_hist(input);
		
		// save output image
		final String outPath = args[1];
		File path = new File(outPath);
		// clear the file if it already exists.
		if (path.exists()) {
			path.delete();
		}
		ImgSaver imgSaver = new ImgSaver();
		imgSaver.saveImg(outPath, input);
		imgSaver.context().dispose();
		System.out.println("Image saved in: " + outPath);		
	}

}
