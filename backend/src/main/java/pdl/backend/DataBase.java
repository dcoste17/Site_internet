package pdl.backend;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class DataBase{
    public static final Map<String, String> data_base = new HashMap<>();

    boolean add (String username, String password){
        if(data_base.containsKey(username)){
            return false;
        }
        data_base.put(username, password);
        return true;
    }

    
}