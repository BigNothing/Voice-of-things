import java.net.*;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


/*public class Blinks
{
    

    public static void main(String[] args) throws InterruptedException
    {
        final GpioController gpio = GpioFactory.getInstance();
        
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLed", PinState.HIGH);
        
        pin.setShutdownOptions(true , PinState.LOW);
        
        Thread.sleep(5000);
        
        pin.low();
        
        Thread.sleep(5000);
        
        pin.toggle();
        
        Thread.sleep(5000);
        
        pin.toggle();
        
        Thread.sleep(5000);
        
        pin.pulse(1000, true);
        
        gpio.shutdown();
        
        
    }
}*/

public class Server {
    
   
   

    public static void main(String[] args) {
        
        final GpioController gpio = GpioFactory.getInstance();
        
        final GpioPinDigitalOutput redPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyRedLed", PinState.HIGH);
        final GpioPinDigitalOutput greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyGreenLed", PinState.HIGH);
        final GpioPinDigitalOutput yellowPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "MyYellowLed", PinState.HIGH);
        
        redPin.setShutdownOptions(true , PinState.LOW);
        greenPin.setShutdownOptions(true , PinState.LOW);
        yellowPin.setShutdownOptions(true , PinState.LOW);
        
        int nreq = 1;
       
        System.out.println("running....");
        
        try
        {
            ServerSocket sock = new ServerSocket (5500);
            System.out.println("Socket 5500 is open");
         
            for (int i=0 ; i<50 ; i++)
            {
                System.out.println("Waiting client");
                Socket newsock = sock.accept();
     
                System.out.println("Creating thread ...");
                
                Thread t = new ThreadHandler(newsock,nreq, redPin, greenPin, yellowPin);
                
                System.out.println("Thread running....");
                t.start();
                
            }
            
            System.out.println("Closing server...");
            
            if(sock != null) sock.close();
          
        }
        catch (Exception e)
        {
            System.out.println("IO error " + e);
            
        }
        
        gpio.shutdown();
        System.out.println("End successful!");
    }
}
