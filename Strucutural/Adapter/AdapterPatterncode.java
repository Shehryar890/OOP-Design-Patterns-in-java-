interface  PaymentService{
    void  pay( int amount ); 
}

interface ThirdpartyGateway{
    void charge(double balance); 
}

   class paymentAdapter implements  PaymentService{
    private ThirdpartyGateway  gateway;
    paymentAdapter(ThirdpartyGateway gateway){
        this.gateway = gateway;
    }
        
      @Override 
      void pay(int amount){
     
       double gatewayamount  = amount; 
        gateway.charge(gatewayamount);


      }


    }
   

   