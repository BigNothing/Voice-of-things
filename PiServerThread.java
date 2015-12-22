import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class ThreadHandler extends Thread {

    Socket newsock;
    int n;
    
    static HashMap<String, ArrayList<String>> comData = new HashMap<String, ArrayList<String>>();
    
    BufferedReader inp;
    PrintWriter outp;
    
    Serial serial;
    final GpioPinDigitalOutput redPin;
    final GpioPinDigitalOutput greenPin;
    final GpioPinDigitalOutput yellowPin;

    ThreadHandler(Socket s, int v, GpioPinDigitalOutput redPin, GpioPinDigitalOutput greenPin,
            GpioPinDigitalOutput yellowPin) {

        newsock = s;
        n = v;

        this.redPin = redPin;
        this.greenPin = greenPin;
        this.yellowPin = yellowPin;

    }

    public void run() {

        try {

            outp = new PrintWriter(newsock.getOutputStream(), true);
            inp = new BufferedReader(new InputStreamReader(newsock.getInputStream()));

            serial = SerialFactory.createInstance();
            serial.open("/dev/ttyACM0", 9600);

            serial.addListener(new SerialDataListener() {
                @Override
                public void dataReceived(SerialDataEvent event) {
                    // print out the data received to the console
                    System.out.print(event.getData().toString());
                }
            });

            outp.println("Hello  \n");
            outp.flush();

            boolean more_data = true;
            String line;
            char ardCommand = '0';
            String voiceCom = "";
            String tempName = "";
            ArrayList<String> temp = new ArrayList<String>();
            ArrayList<String> sequence = new ArrayList<String>();
            
            ArrayList<String> asd = new ArrayList<String>();
            asd.add("red");
            comData.put("red",asd);
            
            asd = new ArrayList<String>();
            asd.add("green");
            comData.put("green",asd);
            
            asd = new ArrayList<String>();
            asd.add("yellow");
            comData.put("yellow",asd);
            
            asd = new ArrayList<String>();
            asd.add("team name");
            comData.put("team name",asd);
            
            asd = new ArrayList<String>();
            asd.add("Mario dance");
            comData.put("Mario dance",asd);
            
            asd = new ArrayList<String>();
            asd.add("go");
            comData.put("go",asd);
            
            asd = new ArrayList<String>();
            asd.add("backward");
            comData.put("backward",asd);
            
            
            asd = new ArrayList<String>();
            asd.add("go crazy");
            comData.put("go crazy",asd);
            
            
            asd = new ArrayList<String>();
            asd.add("turn right");
            comData.put("turn right",asd);
            
            asd = new ArrayList<String>();
            asd.add("turn left");
            comData.put("turn left",asd);
            
            asd = new ArrayList<String>();
            asd.add("tango");
            comData.put("tango",asd);
            
            asd = new ArrayList<String>();
            asd.add("turn around");
            comData.put("turn around",asd);
            
            
            
            

            while (more_data) {

                        line = inp.readLine();

                System.out.println("Message '" + line + "' echoed back to client.");

                if (line == null) {

                    System.out.println("line = null");
                    more_data = false;

                } else {

                     System.out.println(line);
                    if (line.trim().equals("program")) {
                        
                        outp.println("Please enter new task name \n");
                        outp.flush();
                        
                        tempName=inp.readLine();
                        System.out.println(tempName );
                        while (!voiceCom.equals("stop")) {
                            
                            outp.println("Waiting new task \n");
                            outp.flush();
                            
                            voiceCom=inp.readLine();
                            System.out.println(voiceCom );
                            if ((comData.containsKey(voiceCom))) {

                                temp.add(voiceCom);
                                 System.out.println("command was added");

                            } //else {
                                
                                //outp.println("Invalid task, please enter again \n");
                                //outp.flush();
                                
                            //}

                        }
                        comData.put(tempName, temp);
                        System.out.println("new sequence was added ");
                        outp.println("New task was added  \n");
                            outp.flush();
                         
                       
            

                    }else {
                        
                        if ((comData.containsKey(line.trim()))) {

                            execute(line.trim());

                        } else {
                            
                            outp.println("Invalid task, please enter again \n");
                            outp.flush();
                            
                        }
                        
                        
                        
                    }


                    if (line.trim().equals("QUIT"))
                        more_data = false;
                }
            }

            if (outp != null)
                outp.close();
            if (inp != null)
                inp.close();

            newsock.close();
            System.out.println("Disconnected from client number: " + n);

        } catch (Exception e) {

            System.out.println("IO error " + e);

        }

    }
    
    public void execute(String command){
        System.out.println(command);
        ArrayList<String> sequence = comData.get(command);
        
        int lenght = sequence.size();
        
        for(int i=0; i < lenght ; i++){
            
            System.out.println(sequence.get(i));
            if(sequence.get(i).equals("red")){
                
                redPin.low();
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    
                    e.printStackTrace();
                }
                redPin.toggle();
                outp.println("red light had blinked \n");
                
                /*
                 * pin.toggle();
                 * pin.pulse(1000, true);
                 */
                
            }
            
            if (sequence.get(i).equals("green")) {

                greenPin.low();
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    
                    e.printStackTrace();
                }
                greenPin.toggle();
                outp.println("green light had blinked   \n");

            }

            if (sequence.get(i).equals("yellow")) {

                yellowPin.low();
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    
                   e.printStackTrace();
                }
                yellowPin.toggle();
                outp.println("yellow light had blinked   \n");

            }

            if (sequence.get(i).equals("team name")) {

                

                try {

                    serial.write('0'); // command to Arduino

                } catch (IllegalStateException ex) {

                    ex.printStackTrace();

                }
                outp.println("The best team name is Last Hope   \n");

            }
            
            if (sequence.get(i).equals("Mario dance")) {

                outp.println("Mario is dancing  \n");
               

                try {

                    serial.write('A'); // command to Arduino

                } catch (IllegalStateException ex) {

                    ex.printStackTrace();

                }

            }
            
            if(sequence.get(i).equals("go")){
                    
                        try{
                           
                           serial.write('B');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       
                       outp.println("going forward  \n");
                       
            }
            if(sequence.get(i).equals("backward")){
                    
                        try{
                           
                           serial.write('C');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       
                       outp.println("Moving backward \n");
                       
            }
            
            if(sequence.get(i).equals("go crazy")){
                    
                        try{
                           
                           serial.write('H');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       
                       outp.println("going crazy  \n");
                       
            }
            
            if(sequence.get(i).equals("turn right")){
                    
                        try{
                           
                           serial.write('D');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       
                       outp.println("turning right  \n");
                       
            }
            
            if(sequence.get(i).equals("turn left")){
                    
                        try{
                           
                           serial.write('E');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       outp.println("turning left  \n");
            }
            
            if(sequence.get(i).equals("tango")){
                    
                        try{
                           
                           serial.write('G');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       outp.println("tango baby  \n");
                       
            }
            
            if(sequence.get(i).equals("turn around")){
                    
                        try{
                           
                           serial.write('F');  //command to Arduino
                           
                       }catch(IllegalStateException ex){
            
                           ex.printStackTrace();
                    
                       }
                       
                       outp.println("round and around  \n");
                       
            }
            
            
        }
        
    }
    
    
    
    
    
}
