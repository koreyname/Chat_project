/*
//create by tyz@cuit
//create date is 2023.5.23
*/
package run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
public class Client {
    //默认信息--地址---端口号
    private static String SERVER_ADDRESS = "localhost";
    private static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        if(args.length>1) {//修改对应参数
            SERVER_ADDRESS=args[0];
            SERVER_PORT= Integer.parseInt(args[1]);
        }
        //新建服务器，供客户端连接，进行监听
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        System.out.println("连接服务器 " + SERVER_ADDRESS + ":" + SERVER_PORT);
        //创建对应的I/O流
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));//待会儿传给客户端
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
        String name;
        System.out.print("输入您的信息: <用户名 密码>");
        String info = consoleReader.readLine();
        Check_info ci=new Check_info();
        try {
            ci.setName(info.split(" ")[0]);
            ci.setPwd(info.split(" ")[1]);
        }
        catch (Exception e)
        {
            System.out.println("输入格式错误，程序终止");
            System.exit(0);
        }
        while(!ci.SQL_Query(ci))
        {
            System.out.println("账号密码错误或不存在，是否要注册(T/F),退出输入exit,重新输入请输入Reset");
            Scanner sc=new Scanner(System.in);
            String flag=sc.next();
            if(flag.equals("T"))
            {
                    ci.add(ci);
            }
            else if(flag.equals("exit")) {
                    System.exit(0);
            }
            else if(flag.equals("Reset")){
                System.out.print("输入您的信息: ");
                info = consoleReader.readLine();
                ci.setName(info.split(" ")[0]);
                ci.setPwd(info.split(" ")[1]);

            }
            else {
                System.out.println("非服务用户，禁止使用");
            }
        }
        name=ci.getName();
        socketWriter.println(name);//发送给服务器
        ci.Close();
        System.out.println("命令行:");
        System.out.println("/broadcast <message>: 群发消息");
        System.out.println("/private <recipient> <message>: 私发消息");
        System.out.println("/quit: 退出聊天");

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
                System.out.println("连接关闭.");
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
                System.out.println("无效命令.");
            }
        }

        socket.close();
    }


}
