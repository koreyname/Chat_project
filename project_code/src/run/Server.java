/*
//create by tyz@cuit
//create date is 2023.5.23
*/
package run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/*
����Server�࣬���У�

PORT: �����������Ķ˿ںţ�Ĭ��Ϊ8080��
serverSocket: ��������socket��
clients: ���������ӵ��������Ŀͻ��˵����ƺ�socket��
 */
public class Server {
    private static int PORT = 8080;
    public String form()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now=new Date();
        String nowStr=sdf.format(now);
        return nowStr;
    }


    private ServerSocket serverSocket;
    //socket�洢��---Ⱥ��˽��ӳ��
    private Map<String, Socket> clients;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("�����������˿� " + PORT);
        clients = new HashMap<>();
    }
    /*
    Start()
    ͨ������ѭ�����ȴ��ͻ������ӣ�
���ͻ�������ʱ������һ���µ��̴߳���ͻ������ӣ�
���ͻ���������Ϣ���������̨��
     */

    public void start() throws IOException {
        while (true) {
            //ѭ���ȴ����û�����
            Socket socket = serverSocket.accept();
            System.out.println(form()+" �����û����� " + socket.getRemoteSocketAddress());
            Thread thread = new Thread(() -> {
                // ��һ������һ�����߳�
                try {
                    handleClient(socket);
                } catch (IOException e) {
                    System.out.println("���ӹر�.");
                }
            });
            thread.start();
        }
    }
    /*
     *�û���Ϣ����
     * �����sokect��������������н���
     *
     */
    private void handleClient(Socket socket) throws IOException {
        //���������ת�߼����������
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        //���û����Ӻ�ͻ��˵����ּ�¼
        String name = reader.readLine();
        String date=form();
        System.out.println(date+"\n"+"�û� " + name + " ����.");
        //�����µ��û�������Ӧ��socket
        clients.put(name, socket);
        broadcast(date+" "+name + " ����������");
        //�û������ֱ�ӽ�������ѭ��
        while (true) {
            String message = reader.readLine();
            if (message == null) {
                break;
            }
            System.out.println(form()+"\n"+name + ": " + message);
            //Ⱥ��
            if (message.startsWith("broadcast ")) {
                String broadcastMessage = message.substring("broadcast ".length());
                broadcast(form()+"\n"+name + ": " + broadcastMessage);
            }
            //˽��
            else if (message.startsWith("private ")) {
                String[] parts = message.substring("private ".length()).split(" ", 2);
                if (parts.length == 2) {
                    String recipient = parts[0];
                    String privateMessage = parts[1];
                    sendPrivateMessage(name, recipient, privateMessage);
                }
            } else {
                writer.println("������Ч.");
            }
        }

        clients.remove(name);
        broadcast(form()+"\n"+name + " �˳�������");

        socket.close();
    }
    //
    private void broadcast(String message) {
        //��������map�ͽ���Ϣ���͸�������
        for (Socket client : clients.values()) {
            try {
                //��ӡ�������͸��û�
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                System.out.println("���͸��û���Ϣʧ��.");
            }
        }
    }

    private void sendPrivateMessage(String sender, String recipient, String message) {
        //��ȡ��Ӧ�û���socket
        Socket socket = clients.get(recipient);
        if (socket != null) {
            try {
                //���͸���Ӧsocket����Ϣ
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(form()+"\n"+sender + " (private): " + message);
            }
            catch (IOException e) {
                System.out.println("˽��ʧ��.");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length>0) {//�޸Ĳ���
            PORT = Integer.parseInt(args[0]);
        }
        Server server = new Server();
        server.start();
    }
}
