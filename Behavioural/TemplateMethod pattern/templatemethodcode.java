abstract  class  FileSaver{
final void save(){

   readfile() ;       
   printfilename();
   printfilesize();
   printfiletype();
   savefile();
   
   }
   

 void readfile(){
System.out.println("i m readingfile function ");
}
    void printfilename(){
System.out.println("i m printing filename function");

    }
     void printfilesize(){ 
System.out.println("i m printing filesize function");

    }

void printfiletype(  ){
System.out.println("i  am printing filetype function");
}
abstract void savefile();

}



 


 class  S3bucketSaver extends  FileSaver{
    
 @Override 
void savefile(){
System.out.println("i will have the logic of saving the userfile into S3");
}



 }
  class  DatabaseSaver extends  FileSaver{
    
 @Override 
void savefile(){
System.out.println("i will have the logic of saving the userfile into database");
}



 }
 class  Saver extends  FileSaver{
    
 @Override 
void savefile(){
System.out.println("i will have the logic of saving the userfile into hardisk");
}



 }


 
  class  callingSavemethod {
    public static  void main (String[] args){
        FileSaver  file = new DatabaseSaver();
        file.save();  
    }
      
  }
 

 