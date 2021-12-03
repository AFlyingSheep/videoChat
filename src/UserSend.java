import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;
/*
 * Created by JFormDesigner on Wed Dec 01 22:27:12 CST 2021
 */



/**
 * @author Shuangjian
 */
public class UserSend extends JFrame{
    public static void main(String[] args) throws IOException {
        UserSend u = new UserSend();
        new pingTool().start();
        u.setVisible(true);
        u.getImage();
    }
    Graphics g;
    Graphics rece;
    static public BufferedImage image;
    static public BufferedImage newImg;
    Webcam webcam;
    int port;
    public static String ip;
    public static long ping;
    long lastSendTime = 0;


    public void initIP() throws IOException {
        Properties pro = new Properties();
        ClassLoader classLoader = UserSend.class.getClassLoader();
        URL res = classLoader.getResource("userSend.properties");
        String path = res.getPath();
        System.out.println(path);
        pro.load(new FileReader(path));
        port = Integer.parseInt(pro.getProperty("port"));
        ip = pro.getProperty("ip");
    }

    public UserSend() {
        initComponents();
        // 打开默认摄像头
        webcam = Webcam.getDefault();
        webcam.open();
        newImg = webcam.getImage();
        try{
            initIP();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label2 = new JLabel();
        label3 = new JLabel();
        label1 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label2 ----
        label2.setText("\u672a\u5f00\u542f\u89c6\u9891");
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(205, 145), label2.getPreferredSize()));

        //---- label3 ----
        label3.setText("\u672a\u53d1\u9001");
        contentPane.add(label3);
        label3.setBounds(595, 55, 165, 25);

        //---- label1 ----
        label1.setText(" ");
        contentPane.add(label1);
        label1.setBounds(665, 325, 95, label1.getPreferredSize().height);

        //---- label4 ----
        label4.setText("\u53d1\u9001\u901f\u7387\uff1a");
        contentPane.add(label4);
        label4.setBounds(new Rectangle(new Point(565, 260), label4.getPreferredSize()));

        //---- label5 ----
        label5.setText("NULL");
        contentPane.add(label5);
        label5.setBounds(630, 260, 125, label5.getPreferredSize().height);

        //---- label6 ----
        label6.setText("\u53d1\u9001\u5ef6\u8fdf\uff1a");
        contentPane.add(label6);
        label6.setBounds(new Rectangle(new Point(565, 285), label6.getPreferredSize()));

        //---- label7 ----
        label7.setText("4ms");
        contentPane.add(label7);
        label7.setBounds(630, 285, 125, label7.getPreferredSize().height);

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
    private JLabel label2;
    private JLabel label3;
    private JLabel label1;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void getImage() throws IOException {
        //持续获取图像
        while (true) {
            image = webcam.getImage();

            //传输有效图像
            if (image != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int width = image.getWidth();
                int height = image.getHeight();
                g = this.getGraphics();

                BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g2 = newImg.getGraphics();
                for (int i = 0; i < width; i += 1) {//把img的每个颜色转移到img2上
                    for (int j = 0; j < height; j += 1) {
                        int rgb = image.getRGB(i, j);
                        Color c = new Color(rgb);

                        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue()));
                        g2.drawLine(i, j, i, j);
                    }
                }
                g.drawImage(newImg,50,50,480,480*height/width,null);
                ImageIO.write(newImg,"jpg",bos);
                DatagramSocket ds = new DatagramSocket();
                byte[] bytes = bos.toByteArray();
                DatagramPacket dp = new DatagramPacket(bytes,bytes.length,InetAddress.getByName(ip),port);
                double tl = System.currentTimeMillis();
                ds.send(dp);
                this.label3.setText("发送正常.");
                double sendTime = System.currentTimeMillis() - tl;
                //System.out.println(sendTime);
                sendTime = sendTime / 1000; //转换为秒
                double sendRate = bytes.length / sendTime / 1000/1000;

                label5.setText(String.format("%.2f", sendRate).toString()+"MB/s");

                lastSendTime = System.currentTimeMillis();


            }
        }
    }
}


class pingTool extends Thread{
    @Override
    public void run() {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(8080);
            datagramSocket.setSoTimeout(1000);
            while(true)
            {
                String str="Ping "+" "+new Date().toString();
                byte[] data=str.getBytes("UTF-8");
                DatagramPacket datagramPacket= new DatagramPacket(data,data.length, InetAddress.getByName(UserSend.ip),80);
                long startTime = System.currentTimeMillis();
                datagramSocket.send(datagramPacket);
                byte[] data1=new byte[1024];
                DatagramPacket datagramPacket1=new DatagramPacket(data1,data1.length);
                try {
                    datagramSocket.receive(datagramPacket1);
                } catch (IOException e) {
                    System.out.print("超时");
                }
                long endTime = System.currentTimeMillis();
                UserSend.ping = endTime - startTime;

                Thread.sleep(2000);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}