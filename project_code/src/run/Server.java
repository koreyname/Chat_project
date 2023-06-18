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
创建Server类，其中：

PORT: 服务器监听的端口号，默认为8080；
serverSocket: 服务器的socket；
clients: 保存已连接到服务器的客户端的名称和socket。
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
    //socket存储表---群发私发映射
    private Map<String, Socket> clients;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("服务器启动端口 " + PORT);
        clients = new HashMap<>();
    }
    /*
    Start()
    通过无限循环来等待客户端连接；
当客户端连接时，创建一个新的线程处理客户端连接；
将客户端连接信息输出到控制台。
     */

    public void start() throws IOException {
        while (true) {
            //循环等待新用户加入
            Socket socket = serverSocket.accept();
            System.out.println(form()+" 连接用户来自 " + socket.getRemoteSocketAddress());
            Thread thread = new Thread(() -> {
                // 来一个开启一个新线程
                try {
                    handleClient(socket);
                } catch (IOException e) {
                    System.out.println("连接关闭.");
                }
            });
            thread.start();
        }
    }
    /*
     *用户信息处理
     * 传入的sokect参数与服务器进行交互
     *
     */
    private void handleClient(Socket socket) throws IOException {
        //输入输出流转高级流方便操作
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        //有用户连接后客户端的名字记录
        String name = reader.readLine();
        String date=form();
        System.out.println(date+"\n"+"用户 " + name + " 连接.");
        //加入新的用户和它对应的socket
        clients.put(name, socket);
        broadcast(date+" "+name + " 加入聊天室");
        //用户加入后，直接进入聊天循环
        while (true) {
            String message = reader.readLine();
            if (message == null) {
                break;
            }
            System.out.println(form()+"\n"+name + ": " + message);
            //群发
            if (message.startsWith("broadcast ")) {
                String broadcastMessage = message.substring("broadcast ".length());
                broadcast(form()+"\n"+name + ": " + broadcastMessage);
            }
            //私发
            else if (message.startsWith("private ")) {
                String[] parts = message.substring("private ".length()).split(" ", 2);
                if (parts.length == 2) {
                    String recipient = parts[0];
                    String privateMessage = parts[1];
                    sendPrivateMessage(name, recipient, privateMessage);
                }
            } else {
                writer.println("命令无效.");
            }
        }

        clients.remove(name);
        broadcast(form()+"\n"+name + " 退出聊天室");

        socket.close();
    }
    //
    private void broadcast(String message) {
        //遍历整个map就将信息发送给所有人
        for (Socket client : clients.values()) {
            try {
                //打印流，发送给用户
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                System.out.println("发送给用户消息失败.");
            }
        }
    }

    private void sendPrivateMessage(String sender, String recipient, String message) {
        //获取对应用户的socket
        Socket socket = clients.get(recipient);
        if (socket != null) {
            try {
                //发送给对应socket的信息
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(form()+"\n"+sender + " (private): " + message);
            }
            catch (IOException e) {
                System.out.println("私发失败.");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if(args.length>0) {//修改参数
            PORT = Integer.parseInt(args[0]);
        }
        Server server = new Server();
        server.start();
    }
}
