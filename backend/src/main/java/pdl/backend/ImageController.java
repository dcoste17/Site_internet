package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import io.scif.img.SCIFIOImgPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.charset.StandardCharsets;

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

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getImage(@PathVariable("id") long id) {

    Optional<Image> imgFile =imageDao.retrieve(id);
    return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imgFile.get().getData());    
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<byte[]> deleteImage(@PathVariable("id") long id) {
    Optional<Image> imgFile =imageDao.retrieve(id);
    imageDao.delete(imgFile.get());
    return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imgFile.get().getData());
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<Void> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
        try{
          byte[] bytes = file.getBytes();
          String fileName = file.getOriginalFilename();
          Image img = new Image(fileName, bytes);
          imageDao.create(img);
          redirectAttributes.addFlashAttribute("reponse",true);
          return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e){
          System.out.println(e);
        }
   return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    String ext = "";
    for (Image img : imageDao.retrieveAll()) {
      ObjectNode mappers = mapper.createObjectNode();
      ext = img.getName().substring(img.getName().lastIndexOf("."));
      mappers.put("Id",img.getId());
      mappers.put("Name",img.getName());
      mappers.put("Type",ext);
      try {
        if (img.getWidth() <0 || img.getHeight() < 0){
          System.out.println("ERROROROROROROROROr");
        }
        else {mappers.put("Size",""+img.getWidth()+"*"+img.getHeight()+"*3"); }
      } catch (Exception e) {
        //TODO: handle exception
      }
      nodes.add(mappers);
    }
    return nodes;
  }
/* **************************************************FILTERS************************************************** */
  @RequestMapping(value = "/images/{id}", params = {"algo","p1","p2"}, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  //prototypes de la fonctions avec les paramètre restant |
  //public ResponseEntity<byte[]> filtreImage(@PathVariable("id") long id, @RequestParam(name="algo",required = true) String algo, @RequestParam(value ="p1",required = false) int x,@RequestParam(value ="p2",required = false) int y) {
    public ResponseEntity<byte[]> filtreImage(@PathVariable("id") long id, @RequestParam("algo") String algo, String p1, String p2) {
    Optional<Image> imgFile2 =imageDao.retrieve(id);
    Optional<Image> imgFile = imgFile2;
    System.out.println(id);

    Image image = imgFile.get();
    byte[] byteFile = image.getData();
    SCIFIOImgPlus<UnsignedByteType> finalImage = null;
    try {
      finalImage = ImageConverter.imageFromJPEGBytes(byteFile);
    }catch (Exception e){
      System.out.println("error");
    }
    Img<UnsignedByteType> input = (Img<UnsignedByteType>) finalImage;
		
    
    switch (algo) {
      case "blueFilter":
        imageDao.blueFilter(input);
        try {
          byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
          if( tmp == null){
          }
          String fileName = imgFile2.get().getName();
          String ext = "";
          ext = fileName.substring(fileName.lastIndexOf("."));
          System.out.println(ext);
          fileName = fileName.substring(0, fileName.lastIndexOf("."));
          fileName = fileName + "_bf" + ext;
          Image img = new Image(fileName, tmp);
          imageDao.create(img);
        }catch (Exception e){
          System.out.println("error2");
        }
        break;
        
      
      case "augDimLuminosite_cursor":
        int param1 = Integer.parseInt(p1);
        imageDao.augDimLuminosite_cursor(input, param1);
        try {
          byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
          if( tmp == null){  
            System.out.println("NUUUUUUUUUUUUUUUUL");
          }
          String fileName = imgFile2.get().getName();
          String ext = "";
          ext = fileName.substring(fileName.lastIndexOf("."));
          System.out.println(ext);
          fileName = fileName.substring(0, fileName.lastIndexOf("."));
          fileName = fileName + "Lum" + ext;
          Image img = new Image(fileName, tmp);
          imageDao.create(img);
        }catch (Exception e){
          System.out.println("error2");
        }
        break;

      case "conversionCG":
        imageDao.conversionCG(input);
        try {
          byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
          if( tmp == null){
            System.out.println("NUUUUUUUUUUUUUUUUL");

          }
          String fileName = imgFile2.get().getName();
          String ext = "";
          ext = fileName.substring(fileName.lastIndexOf("."));
          System.out.println(ext);
          fileName = fileName.substring(0, fileName.lastIndexOf("."));
          fileName = fileName + "convCG" + ext;
          Image img = new Image(fileName, tmp);
          imageDao.create(img);
        }catch (Exception e){
          System.out.println("error2");
        }
        break;
      

      case "conversionrgbToHsv":
      float param2 = Float.parseFloat(p1);
      imageDao.conversionrgbToHsv(input, param2);
      try {
        byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
        if( tmp == null){
        }
        String fileName = imgFile2.get().getName();
        String ext = "";
        ext = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(ext);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName = fileName + "convrgbHsv" + ext;
        Image img = new Image(fileName, tmp);
        imageDao.create(img);
      }catch (Exception e){
        System.out.println("error2");
      }
        break;

      case "egalisationHistogramme":
        imageDao.egalisationHistogramme(input);
      try {
        byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
        if( tmp == null){
        }
        String fileName = imgFile2.get().getName();
        String ext = "";
        ext = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(ext);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName = fileName + "egalis" + ext;
        Image img = new Image(fileName, tmp);
        imageDao.create(img);
      }catch (Exception e){
        System.out.println("error2");
      }
        break;

      case "dynamicImageMinMax":
        int r1 = Integer.parseInt(p1);
        int r2 = Integer.parseInt(p2);
        imageDao.dynamicImageMinMax(input,r1,r2);
        try {
          byte[] tmp = ImageConverter.imageToJPEGBytes(finalImage);
          if( tmp == null){
          }
          String fileName = imgFile2.get().getName();
          String ext = "";
          ext = fileName.substring(fileName.lastIndexOf("."));
          System.out.println(ext);
          fileName = fileName.substring(0, fileName.lastIndexOf("."));
          fileName = fileName + "dynMinMax" + ext;
          Image img = new Image(fileName, tmp);
          imageDao.create(img);
        }catch (Exception e){
          System.out.println("error2");
        }
        break;
      
      default:
        break;
  }
  return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imgFile.get().getData());
  }
  @RequestMapping(value = "frontend/src/views/member_space", method = RequestMethod.GET)
  public  ResponseEntity<Void> check_credentials( String user,String pswd) {
    System.out.println("je m'appele daniel");
    if (DataBase.data_base.containsKey(user) && DataBase.data_base.containsValue(pswd)){
        return new ResponseEntity<>(HttpStatus.OK);
    }
    System.out.println("introuvable");
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            
  }

  @RequestMapping(value = "/inscription", params = {"user","pswd"}, method = RequestMethod.POST)
  public  ResponseEntity<Void> inscription( @RequestParam("user")String username, @RequestParam("pswd") String pswd, String email) {
    System.out.println("je m'appele thierry");
    User user = new Users(username, pswd, email);

    //DataBase.data_base.put(user,pswd);
    return new ResponseEntity<>(HttpStatus.OK);
            
  }

}