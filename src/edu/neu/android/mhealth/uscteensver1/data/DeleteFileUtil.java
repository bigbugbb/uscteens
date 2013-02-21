import java.io.File;

public class DeleteFileUtil {     
    /**   
     *    
     * @param
     * @return 
     */    
    public static boolean delete(String fileName){     
        File file = new File(fileName);     
        if (!file.exists()) {                   
            return false;     
        } else {     
            if (file.isFile()){                  
                return deleteFile(fileName);     
            } else {     
                return deleteDirectory(fileName);     
            }     
        }     
    }     
         
    /**   
     *    
     * @param   fileName   
     * @return  
     */    
    public static boolean deleteFile(String fileName){     
        File file = new File(fileName);     
        if (file.isFile() && file.exists()) {     
            file.delete();                   
            return true;     
        } else {                    
            return false;     
        }     
    }     
         
    /**   
     * 
     * @param   dir  
     * @return  
     */    
    public static boolean deleteDirectory(String dir){     
           
        if(!dir.endsWith(File.separator)){     
            dir = dir+File.separator;     
        }     
        File dirFile = new File(dir);     
       
        if(!dirFile.exists() || !dirFile.isDirectory()){     
            
            return false;     
        }     
        boolean flag = true;     
         
        File[] files = dirFile.listFiles();     
        for (int i = 0; i < files.length; i++){     
            
            if (files[i].isFile()){     
                flag = deleteFile(files[i].getAbsolutePath());     
                if(!flag){     
                    break;     
                }     
            } else {     
                flag = deleteDirectory(files[i].getAbsolutePath());     
                if (!flag){     
                    break;     
                }     
            }     
        }     
             
        if (!flag) {       
            return false;     
        }     
                 
        if (dirFile.delete()) {                  
            return true;     
        } else {         
            return false;     
        }     
    }     
           
}