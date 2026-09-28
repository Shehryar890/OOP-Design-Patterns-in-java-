class User{

       void createUser(User user){
        System.out.println(" i have logic of creating the user");
       }
       void deleteUser(int userid ){
        System.out.println(" i have logic of deleting the user")

       }
}


interface Command {
    void execute();   // my concrete implementations will be created by the caller according to what command they need for this request 
}
class RegisterUserCommand  implements  Command{
// fields 
       private User user ; 
       private User  userdata ; 
       /// constructor  
       RegisterUserCommand(User user   , User userdata){
        this.user = user;  
        this.userdata = userdata; 

       }

public void execute() {
     System.out.println(" im the command that when exectue will create user")
         user.createUser(userdata);

      }
    
    
}

class DeleteUserCommand  implements  Command{
// fields 
        int userid ; 
       User user ; 

     
       /// constructor  
       DeleteUserCommand(int userid , User user){
        this.userid = userid;  
        this.user  = user; 
     

       }

public void execute() {
     System.out.println(" im the command that when exectue will delete user")
         user.deleteUser(userid);

      }
    
    
}
      
      

class UserService implements  Command {                             /// invoker that now needs commands to execute 
       
       Command command ; 
       UserService (Command command){
        this.command = command;
       }
        
           void exectue(){            ////// if caller calls the commands itsel
            command.execute();
           }
           void executecommand(Command command){
            command.execute();       
           }
       
}
      
      
