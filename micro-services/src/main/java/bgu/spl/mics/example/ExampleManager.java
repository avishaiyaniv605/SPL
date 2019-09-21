package bgu.spl.mics.example;

import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExampleManager {

    public static void main(String[] args) {
        Map<String, ServiceCreator> serviceCreators = new HashMap<>();
        serviceCreators.put("ev-handler", ExampleEventHandlerService::new);
        serviceCreators.put("brod-listener", ExampleBroadcastListenerService::new);
        serviceCreators.put("sender", ExampleMessageSenderService::new);

//        Scanner sc = new Scanner(System.in);

        String[] argu = {"start sender sender1 event",
                         "start brod-listener brod1 2",
                         "start ev-handler hand1 1",
                         "start ev-handler hand2 2",
                         "start sender sender2 broadcast",
                         "start sender sender2 event",
                         "start sender sender3 event",
                         "start sender sender4 broadcast",
                         "start sender sender5 event",
                         "quit"};

        int arguIndex = 0;
        boolean quit = false;
        try {
            System.out.println("Example manager is started - supported commands are: start,quit");
            System.out.println("Supporting services: " + serviceCreators.keySet());
            while (!quit && arguIndex < argu.length) {
                Thread.currentThread().sleep(10);
//                String line = sc.nextLine();
                String line = argu[arguIndex];
                arguIndex++;
                System.out.println('\n' + line);
                String[] params = line.split("\\s+");

                if (params.length > 0) {

                    switch (params[0]) {
                        case "start":
                            try {
                                if (params.length < 3) {
                                    throw new IllegalArgumentException("Expecting service type and id, supported types: " + serviceCreators.keySet());
                                }
                                ServiceCreator creator = serviceCreators.get(params[1]);
                                if (creator == null) {
                                    throw new IllegalArgumentException("unknown service type, supported types: " + serviceCreators.keySet());
                                }

                                new Thread(creator.create(params[2], Arrays.copyOfRange(params, 3, params.length))).start();
                            } catch (IllegalArgumentException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            }

                            break;
                        case "quit":
                            quit = true;
                            break;
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("Unexpected Error!!!!");
            t.printStackTrace();
        } finally {
            System.out.println("Manager Terminating - UNGRACEFULLY!");
//            sc.close();
            System.exit(0);
        }
    }
}
