package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Client {
    //Ĭ����Ϣ--��ַ---�˿ں�
    private static String SERVER_ADDRESS = "localhost";

    private static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        if(args.length>1) {//�޸Ķ�Ӧ����
            SERVER_ADDRESS=args[0];
        SERVER_PORT= Integer.parseInt(args[1]);
        }
        //�½������������ͻ������ӣ����м���
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        System.out.println("���ӷ����� " + SERVER_ADDRESS + ":" + SERVER_PORT);
            //������Ӧ��I/O��
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));//����������ͻ���
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);

        System.out.print("���������û���: ");
        String name = consoleReader.readLine();
        socketWriter.println(name);//���͸�������

        System.out.println("������:");
        System.out.println("/broadcast <message>: Ⱥ����Ϣ");
        System.out.println("/private <recipient> <message>: ˽����Ϣ");
        System.out.println("/quit: �˳�����");

        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    String message = socketReader.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("���ӹر�.");
            }
        });
        receiveThread.start();

        while (true) {
            String command = consoleReader.readLine();
            if (command == null) {
                break;
            }
            if (command.startsWith("/broadcast ")) {
                String message = command.substring("/broadcast ".length());
                socketWriter.println("broadcast " + name + ": " + message);
            } else if (command.startsWith("/private ")) {
                String[] parts = command.substring("/private ".length()).split(" ", 2);
                if (parts.length == 2) {
                    String recipient = parts[0];
                    String message = parts[1];
                    socketWriter.println("private " + recipient + " " + name + ": " + message);
                }
            } else if (command.equals("/quit")) {
                break;
            } else {
                System.out.println("��Ч����.");
            }
        }

        socket.close();
    }


}
