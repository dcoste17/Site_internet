package pdl.backend;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



//import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import io.scif.img.SCIFIOImgPlus;

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
import net.imglib2.Cursor;


@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws IOException {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    File repertoire = new File("./src/main/resources/images");
    if(!repertoire.exists()){
      throw new IOException("repertory /images not found");
    }

    File newF = new File("./src/main/resources/images/file.jpg");
    File liste[] = repertoire.listFiles();

    if (liste != null) {
        for (int i = 0; i < liste.length; i++) {
          System.out.format("fichiers : %s%n", liste[i].getName());
          liste[i].toString();

          try {
            byte[] fileContent;
            fileContent = Files.readAllBytes(liste[i].toPath());
            String fileName = liste[i].getName();
            String ext = "";
            ext = fileName.substring(fileName.lastIndexOf("."));
            System.out.println(ext);
            if (!ext.equals(".jpeg") && !ext.equals(".jpg") ){
              throw new IOException("format is not supported");
            }
            Image img = new Image(fileName, fileContent);
            images.put(img.getId(), img);
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
      }
  }

  @Override
  public Optional<Image> retrieve(final long id) {
        return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    List<Image> listes = new ArrayList<Image>();
    for (Map.Entry<Long,Image> i : images.entrySet()) {
      listes.add(i.getValue());
    }
    return listes;
  }

  @Override
  public void create(final Image img){
    File newF = new File("./src/main/resources/images/"+img.getName());
    byte[] fileContent;
    fileContent = img.getData();
    String fileName = img.getName();
    String ext = "";
    ext = fileName.substring(fileName.lastIndexOf("."));
    System.out.println(ext);
    if (!ext.equals(".jpeg") && !ext.equals(".jpg") ){
      img.setCount(img.getCount() - 1);
      System.out.println("format is not supported");
    }
    else{
      try {
        images.put(img.getId(),img);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(newF));
        out.write(fileContent);
      } catch(Exception e){
        System.out.println("bijour");
      }
    }
  }

  @Override
  public void update(final Image img, final String[] params) {
    // Not used
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
    File newF = new File("./src/main/resources/images/"+img.getName());
    if (newF.exists()){
      newF.delete();
      for (Map.Entry<Long,Image> i : images.entrySet()) {
        System.out.println(i);
          if(i.getValue().getId() > img.getId()){
          i.getValue().setId(i.getValue().getId()-1);
        }
      }
    }
  }
/***********************************FILTERS************************************* */
   
    public static void blueFilter (Img<UnsignedByteType> input) {
          final RandomAccess <UnsignedByteType> r = input.randomAccess ();
          final int iw = (int) input.max (0);
          final int ih = (int) input.max (1);
          
          // les valeurs sur les canaux rouges et vert sont mises à 0
          for (int channel = 0; channel <= 1; channel ++) {
              for (int y = 0; y <= ih; ++ y) {
                  for (int x = 0; x <= iw; ++ x) {
                      r.setPosition (x, 0);
                      r.setPosition (y, 1);
                      r.setPosition (channel, 2);
                      r.get (). set (0);
                  }
              }
          }	
    }
    
    public static void augDimLuminosite_cursor(Img<UnsignedByteType> img, int delta) {
      final Cursor<UnsignedByteType> cursor = img.cursor();
      while (cursor.hasNext()) {
        cursor.fwd();
        if (cursor.get().get() + delta < 0)
          cursor.get().set(0);
        else if (cursor.get().get() + delta > 255)
          cursor.get().set(255);
        else
          cursor.get().set(cursor.get().get() + delta);
      }
    }
    public static void conversionCG (Img <UnsignedByteType> input) {
      final RandomAccess <UnsignedByteType> r = input.randomAccess ();
      final int iw = (int) input.max (0);
      final int ih = (int) input.max (1);
      
      // les valeurs sur les canaux rouges et vert sont mises à 0
      
    int red=0,green=0,blue=0,result=0;
          for (int x = 0; x <= iw; ++ x) {
              for (int y = 0; y <= ih; ++ y) {
          r.setPosition (x, 0);
          r.setPosition (y, 1);

          r.setPosition (0, 2);
          red=r.get().get();
            

          r.setPosition (1, 2);
          green=r.get().get();
          
          r.setPosition (2, 2);
          blue=r.get().get();
      result=(int) (red*0.3+green*0.59+blue*0.11);
      if (result < 0){
        r.setPosition (0, 2);
        r.get().set(0);
        r.setPosition (1, 2);
        r.get().set(0);
        r.setPosition (2, 2);
        r.get().set(0);
      }
      else if (result> 255){
          r.setPosition (0, 2);
          r.get().set(255);
          r.setPosition (1, 2);
          r.get().set(255);
          r.setPosition (2, 2);
          r.get().set(255);
      }
      else{
          r.setPosition (0, 2);
          r.get().set(result);
          r.setPosition (1, 2);
          r.get().set(result);
          r.setPosition (2, 2);
          r.get().set(result);
      }
          }
      }	
}
  public static float trouver_min(float r,float g, float b){
  float min=Float.MAX_VALUE;
  if(r<min)
    min=r;
  if(g<min)
    min=g;
  if(b<min)
    min=b;
  return min;
}
public static float trouver_max(float r,float g, float b){
  float max=Float.MIN_VALUE;
  if(r>max)
    max=r;
  if(g>max)
    max=g;
  if(b>max)
    max=b;
  return max;
}
/**
 * Question 4.1
 */
public static void rgbToHsv(int r, int g, int b, float[] hsv){
  float r1= (float) r/255;
  float g1= (float) g/255;
  float b1= (float) b/255;
  float min=trouver_min(r1, g1, b1);
  float max=trouver_max(r1, g1, b1);
  float delta=max-min;
    /****** Hue ***** */
    if(delta==0)
      hsv[0]=0;
    else if(max==r1)
      hsv[0]= (int) (60*(((g1-b1)/delta)%6));
    else if(max==g1)
      hsv[0]=(int) (60*(((b1-r1)/delta)+2));
    else if(max==b1)
      hsv[0]=(int) (60*(((r1-g1)/delta)+4));

    /****** Saturation ***** */
    if(max==0)
      hsv[1]=0;	
    else
      hsv[1]=(float) (delta/max)*100;
    /****** Value ***** */
    hsv[2]=(int) (max *100);	
}
/**
 * Question 4.1.
 */
public static void hsvToRgb(float h, float s, float v, int[] rgb){
  s=s/100;
  v=v/100;
  float r1=0,g1=0,b1=0;
  
  float c=v*s;
  float x=c*(1-Math.abs( ((h/60)%2)-1 ));
  float m=v-c;
  
  if(h>=0 && h<60){
    r1=c;
    g1=x;
    b1=0;
  }
  if(h>=60 && h<120){
    r1=x;
    g1=c;
    b1=0;
  }
  if(h>=120 && h<180){
    r1=0;
    g1=c;
    b1=x;
    }
  if(h>=180 && h<240){
    r1=0;
    g1=x;
    b1=c;
  }
  if(h>=240 && h<300){
    r1=x;
    g1=0;
    b1=c;
  }
  if(h>=300 && h<360){
    r1=c;
    g1=0;
    b1=x;
  }
  rgb[0]=(int)(((r1+m)*255));
  rgb[1]=(int)(((g1+m)*255));
  rgb[2]=(int)(((b1+m)*255));
}
/**
 * Question 4.2
 */
public static void conversionrgbToHsv(Img<UnsignedByteType> input,float hue){
  float[] tabhsv= new float[3];
  int[] tabrgb=new int[3];
  final RandomAccess <UnsignedByteType> r = input.randomAccess ();
      final int iw = (int) input.max (0);
      final int ih = (int) input.max (1);
      
    int red=0,green=0,blue=0;
          for (int x = 0; x <= iw; ++ x) {
              for (int y = 0; y <= ih; ++ y) {
        r.setPosition (x, 0);
          r.setPosition (y, 1);

          r.setPosition (0, 2);
          red=r.get().get();
            

          r.setPosition (1, 2);
          green=r.get().get();
          
          r.setPosition (2, 2);
          blue=r.get().get();

          rgbToHsv(red, green, blue, tabhsv);
          hsvToRgb(hue, tabhsv[1], tabhsv[2], tabrgb);

          r.setPosition (0, 2);
          r.get().set(tabrgb[0]);
          r.setPosition (1, 2);
          r.get().set(tabrgb[1]);
          r.setPosition (2, 2);
          r.get().set(tabrgb[2]);
      }
    }
}
/**
 * Question 5.1
 */
public static int[] histogrammeDeImage(Img<UnsignedByteType> img){
  int histogramme[]=new int[256];
  final RandomAccess<UnsignedByteType> r = img.randomAccess();
  final int iw = (int) img.max(0);
  final int ih = (int) img.max(1);
  for(int i=0;i<256;i++){
    int cpt=0;
    for (int x = 0; x <= iw; ++x) {
      for (int y = 0; y <= ih; ++y) {
        r.setPosition(x, 0);
        r.setPosition(y, 1);
        final UnsignedByteType val = r.get();
        if(val.get()==i){
          cpt++;
        }
      }
    }
    histogramme[i]=cpt;
  }
  return histogramme;
}
public static int[] histogrammeCumule(Img<UnsignedByteType> img){
  int []h=histogrammeDeImage(img);
  int[]histogrammeCumule=new int [256];
  histogrammeCumule[0] =h[0];
  for (int i = 1; i < 256; i++) {	
    histogrammeCumule[i]=histogrammeCumule[i-1]+h[i];
  }
  return histogrammeCumule;
}
public static void egalisationHistogramme(Img<UnsignedByteType> img) {
  int tab[]=histogrammeCumule(img);
  final RandomAccess<UnsignedByteType> r = img.randomAccess();
  final int iw = (int) img.max(0);
  final int ih = (int) img.max(1);
  for (int x = 0; x <= iw; ++x) {
    for (int y = 0; y <= ih; ++y) {
      r.setPosition(x, 0);
      r.setPosition(y, 1);
      if ((tab[r.get().get()]*255)/(iw*ih) < 0){
        r.setPosition (0, 2);
        r.get().set(0);
        r.setPosition (1, 2);
        r.get().set(0);
        r.setPosition (2, 2);
        r.get().set(0);
      }
      else if ((tab[r.get().get()]*255)/(iw*ih) > 255){
        r.setPosition (0, 2);
        r.get().set(255);
        r.setPosition (1, 2);
        r.get().set(255);
        r.setPosition (2, 2);
        r.get().set(255);
      }
      else{
        r.setPosition (0, 2);
        r.get().set((tab[r.get().get()]*255)/(iw*ih));
        r.setPosition (1, 2);
        r.get().set((tab[r.get().get()]*255)/(iw*ih));
        r.setPosition (2, 2);
        r.get().set((tab[r.get().get()]*255)/(iw*ih));
      }
    }
  }

}
/**
 * Question 5.2
 */
public static void dynamicImageMinMax(Img<UnsignedByteType> img, int min, int max) {
  final RandomAccess<UnsignedByteType> r = img.randomAccess();
  final int iw = (int) img.max(0);
  final int ih = (int) img.max(1);
  int red=0,green=0,blue=0;
  float tabhsv[]=new float[3];
  int tabrgb[]=new int[3];
  for (int x = 0; x <= iw; ++x) {
    for (int y = 0; y <= ih; ++y) {
      r.setPosition(x, 0);
      r.setPosition(y, 1);
      //final UnsignedByteType val = r.get();
      float delta=0;

          r.setPosition (x, 0);
          r.setPosition (y, 1);

          r.setPosition (0, 2);
          red=r.get().get();
            

          r.setPosition (1, 2);
          green=r.get().get();
          
          r.setPosition (2, 2);
          blue=r.get().get();

          rgbToHsv(red, green, blue, tabhsv);
          //System.out.println("tab2:    "+tabhsv[2]);
          delta = (255 * (tabhsv[2]- min)) / (max - min);
          //System.out.println("delta:    "+delta);
          hsvToRgb(tabhsv[0], tabhsv[1], delta, tabrgb);

        

      if (delta < 0){
        r.setPosition (0, 2);
        r.get().set(0);
        r.setPosition (1, 2);
        r.get().set(0);
        r.setPosition (2, 2);
        r.get().set(0);
        }
      else if (delta > 100){
        r.setPosition (0, 2);
        r.get().set(100);
        r.setPosition (1, 2);
        r.get().set(100);
        r.setPosition (2, 2);
        r.get().set(100);
      }
      else{
        r.setPosition (0, 2);
        r.get().set(tabrgb[0]);
        r.setPosition (1, 2);
        r.get().set(tabrgb[1]);
        r.setPosition (2, 2);
        r.get().set(tabrgb[2]);
      }
    }
  }
}

}






