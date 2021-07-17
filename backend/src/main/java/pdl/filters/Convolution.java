package filters;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;
import net.imglib2.view.Views;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.util.Intervals;
import net.imglib2.Interval;

public class Convolution {

	/**
	 * Question 1.1
	 */
	public static void meanFilterSimple(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output) {
		final RandomAccess<UnsignedByteType> r = input.randomAccess();
		final RandomAccess<UnsignedByteType> r2 = output.randomAccess();

		final int iw = (int) input.max(0);
		final int ih = (int) input.max(1);
		int pixelValue;
		int value_finale = 0;
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				r2.setPosition(x, 0);
				r2.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				final UnsignedByteType val_out = r.get();
				if(x==0 || x==iw-1){
					value_finale = 0;
				}
				if(y==0 || y==ih-1){
					value_finale = 0;
				}
				else{
					for(int i=-1; i<1; i++){
						for(int j=-1; j<1; j++){
							r.setPosition(i, 0);
							r.setPosition(j, 1);
							final UnsignedByteType val2 = r.get();
							value_finale = value_finale + val2.get();
						}
					}val_out.set(value_finale/9);
				}
				
			}
		}		
	}

	/**
	 * Question 1.2
	 */
	public static void meanFilterWithBorders(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int size) {
		if(size%2 == 1 && size != 1){
			final RandomAccess<UnsignedByteType> r = input.randomAccess();
			final int iw = (int) input.max(0);
			final int ih = (int) input.max(1);
			int pixelValue;
			int value_finale = 0;
			for (int x = 0; x <= iw; ++x) {
				for (int y = 0; y <= ih; ++y) {
					r.setPosition(x, 0);
					r.setPosition(y, 1);
					final UnsignedByteType val = r.get();
					if(x==0 || x==iw-1){
						value_finale = 0;
					}
					if(y==0 || y==ih-1){
						value_finale = 0;
					}
					else{
						for(int i= -(size/2); i<size/2; i++){
							for(int j=-(size/2); j<size/2; j++){
								r.setPosition(i, 0);
								r.setPosition(j, 1);
								final UnsignedByteType val2 = r.get();
								value_finale = value_finale + val2.get();
							}
						}val.set(value_finale/9);
					}
					
				}
			}	
		}
		
	}

	/**
	 * Question 1.3
	 */
	public static void meanFilterWithNeighborhood(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int size) {

	}

	/**
	 * Question 2.1
	 */
	public static void convolution(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
			int[][] kernel) {
		final RandomAccess<UnsignedByteType> r = input.randomAccess();
		final int iw = (int) input.max(0);
		final int ih = (int) input.max(1);
		int pixelValue;
		int value_finale = 0;
		for (int x = 0; x <= iw; ++x) {
			for (int y = 0; y <= ih; ++y) {
				r.setPosition(x, 0);
				r.setPosition(y, 1);
				final UnsignedByteType val = r.get();
				if(x==0 || x==iw-1){
					value_finale = 0;
				}
				if(y==0 || y==ih-1){
					value_finale = 0;
				}
				else{
					for(int i=-1; i<1; i++){
						for(int j=-1; j<1; j++){
							r.setPosition(i, 0);
							r.setPosition(j, 1);
							final UnsignedByteType val2 = r.get();
							value_finale = value_finale + val2.get() / kernel[i][j];
						}
					}val.set(value_finale);
				}
				
			}
		}		
	}

	/**
	 * Question 2.3
	 */
	public static void gaussFilterImgLib(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output) {

	}
	
	public static void main(final String[] args) throws ImgIOException, IncompatibleTypeException {

		// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}
		final String filename = args[0];
		final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
		final ImgOpener imgOpener = new ImgOpener();
		final Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(filename, factory).get(0);
		imgOpener.context().dispose();

		// output image of same dimensions
		final Dimensions dim = input;
		final Img<UnsignedByteType> output = factory.create(dim);

		// mean filter
		meanFilterSimple(input, output);

		final String outPath = args[1];
		File path = new File(outPath);
		if (path.exists()) {
			path.delete();
		}
		ImgSaver imgSaver = new ImgSaver();
		imgSaver.saveImg(outPath, output);
		imgSaver.context().dispose();
		System.out.println("Image saved in: " + outPath);
	}

}
