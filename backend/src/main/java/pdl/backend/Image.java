package pdl.backend;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;

  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }

  public void setId(long k){
    this.id = k;
  }

  public Long getCount(){
    return this.count;
  }

  public void setCount(Long count){
    this.count = count;
  }
  
  public int getWidth () throws IOException{
    try {
      BufferedImage img  = createImageFromBytes(this.getData());
      return img.getWidth();
    } catch (Exception e) {
      return -1; 
   }
  }
  
  public int getHeight () throws IOException{
    try {
      BufferedImage img  = createImageFromBytes(this.getData());
      return img.getHeight();
    } catch (Exception e) {
      return -1;
    }
  }

    private BufferedImage createImageFromBytes(byte[] imageData) {
      ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
      try {
          return ImageIO.read(bais);
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }
}
