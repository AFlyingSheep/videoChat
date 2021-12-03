import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;
/*
 * Created by JFormDesigner on Wed Dec 01 23:41:21 CST 2021
 */



/**
 * @author Shuangjian
 */
public class UserRec extends JFrame implements Runnable{
    Graphics g;
    Graphics rece;
    BufferedImage image;
    String ip;
    int port;
    public void initIP() throws IOException {
        /*Properties pro = new Properties();
        ClassLoader classLoader = UserRec.class.getClassLoader();
        URL res = classLoader.getResource("userRec.properties");
        String path = res.getPath();
        System.out.println(path);
        pro.load(new FileReader(path));*/

        Properties properties = new Properties();
        FileInputStream in= new FileInputStream("userRec.properties");
        properties.load(in);
        port = Integer.parseInt(properties.getProperty("port"));
        ip = properties.getProperty("ip");

    }
    public UserRec() {
        initComponents();
        try{
            initIP();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        UserRec user2 = new UserRec();
        new pingToolServer().start();
        user2.setVisible(true);
        user2.run();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 ----
        label1.setText("\u670d\u52a1\u5668\u7aef");
        contentPane.add(label1);
        label1.setBounds(655, 355, 80, label1.getPreferredSize().height);

        //---- label2 ----
        label2.setText("\u63a5\u6536\u7684IP\u5730\u5740\uff1a");
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(15, 350), label2.getPreferredSize()));

        //---- label3 ----
        label3.setText("null");
        contentPane.add(label3);
        label3.setBounds(105, 350, 160, label3.getPreferredSize().height);

        //---- label4 ----
        label4.setText("\u7b49\u5f85\u8fde\u63a5");
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(185, 115), label4.getPreferredSize()));

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void DrawCapture(byte[] buffer) {

        //字节数组输入流
        ByteArrayInputStream bis=new ByteArrayInputStream(buffer);

        try {
            //直接将字节数组输入流转换为依照图片
            BufferedImage recvImg=ImageIO.read(bis);
            rece = this.getGraphics();
            rece.drawImage(recvImg,50,50,480,270,null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        SocketAddress localaddress=new InetSocketAddress(ip,port);


        try {
            DatagramSocket socket=new DatagramSocket(localaddress);

            while(true) {
                System.out.println("视频waiting……");
                System.out.println("socket.getSendBufferSize()="+socket.getSendBufferSize());
                byte[] buffer=new byte[socket.getSendBufferSize()];
                DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
                socket.receive(packet);
                DrawCapture(buffer);//把传过来的数据变成图像显示成视频
                label3.setText(packet.getAddress().getHostAddress());
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class pingToolServer extends Thread{

    @Override
    public void run() {

        DatagramSocket datagramSocket;
        try{
            datagramSocket =new DatagramSocket(80);
            while (true) {
                byte[] data = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                datagramSocket.receive(datagramPacket);
                String str = new String(data, 0, datagramPacket.getLength(), "UTF-8").toUpperCase();
                byte[] data1 = str.getBytes("UTF-8");
                //System.out.println(new String(data,0,datagramPacket.getLength(),"UTF-8"));
                DatagramPacket datagramPacket1 = new DatagramPacket(data1, data1.length, datagramPacket.getAddress(), datagramPacket.getPort());
                datagramSocket.send(datagramPacket1);
            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}