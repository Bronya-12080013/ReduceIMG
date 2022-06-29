import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class ReduceIMG {
    /**
     * 指定图片宽度和高度和压缩比例对图片进行压缩
     */
    public static void reduceImg(String imgSrc, String imgDist) {
        try {
            File srcfile = new File(imgSrc);
            int[] results = getImgWidthHeight(srcfile);

            int widthdist = results[0];
            int heightdist = results[1];
            Image src = ImageIO.read(srcfile);

            BufferedImage tag = new BufferedImage( widthdist,  heightdist, BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null); //缩放平滑

            FileOutputStream out = new FileOutputStream(imgDist);
            //将图片按JPEG压缩，保存到out中
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            //关闭文件输出流
            out.close();
        } catch (Exception ef) {
            ef.printStackTrace();
        }
    }

    /**
     * 获取图片宽度和高度
     */
    public static int[] getImgWidthHeight(File file) {
        InputStream is ;
        BufferedImage src ;
        int result[] = { 0, 0 };
        try {
            is = new FileInputStream(file);

            src = ImageIO.read(is);

            result[0] =src.getWidth(null);

            result[1] =src.getHeight(null);
            is.close();
        } catch (Exception ef) {
            ef.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        new Try().main(args);
    }

    //压缩图片
    public static void execute(String src,String dist,int code) {
        if (src==null||dist==null) return;
        if(new File(src).isFile())
        {
            File srcfile = new File(src);
            System.out.println("===================================");
            System.out.println("压缩前--"+srcfile.getName()+" :"+ srcfile.length());
            File disFile;
            if(code==1) disFile = new File(namedFileByTime(dist,0));
            else disFile = new File(namedFileByOrig(dist,srcfile.getName()));
            reduceImg(srcfile.getAbsolutePath(),disFile.getAbsolutePath());
            System.out.println("压缩后--"+disFile.getName()+" :"+ disFile.length());
            System.out.println("===================================");
            System.out.println("\n压缩结束，压缩文件数量: "+1+" 个");
        }else {
            File[] fs = new File(src).listFiles();	//遍历path下的文件和目录，放在File数组中
            int count = 0;
            for(File f:fs){					//遍历File[]数组
                Image image = null;
                try {
                    image = ImageIO.read(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(image==null)
                    {
                        System.out.println("dame");
                        continue;
                    }
                    if(!f.isDirectory())
                    {
                        System.out.println("压缩前--"+f.getName()+" :"+ f.length());
                        File disFile;
                        if(code==1) disFile = new File(namedFileByTime(dist,count));
                        else disFile = new File(namedFileByOrig(dist,f.getName()));
                        reduceImg(f.getAbsolutePath(),disFile.getAbsolutePath());
                        System.out.println("压缩后--"+disFile.getName()+" :"+ disFile.length());
                        count++;
                    }
                }
            }
            System.out.println("\n压缩结束，压缩文件数量: "+count+" 个");
            ArrayList list = new ArrayList();
        }
        return;
    }

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    /*
    未修改 当new的时候时间就确定了
    之后date.getTime() 只是把new时的时间拿出来
    不是新开时间
    所以想要实时的直接new就好了
     */
    static Date date = new Date();
    /**
    文件命名
     */
    public static String namedFileByTime(String dist,int count)
    {
            return dist+"\\"+formatter.format(date.getTime())+"["+count +"].jpg";
    }

    public static String namedFileByOrig(String dist,String OriginalName)
    {
        return dist+"\\"+OriginalName;
    }


    public static class Try extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {


            Pane pane = new Pane();
            pane.setMinSize(900,600);


            Text textSrc= new Text("压缩文件路径:");
            textSrc.setLayoutX(10);
            textSrc.setLayoutY(120);

            Text textSave= new Text("文件保存路径:");
            textSave.setLayoutX(10);
            textSave.setLayoutY(290);

            Label labelSrc = new Label();
            labelSrc.setLayoutX(10);
            labelSrc.setLayoutY(150);
            labelSrc.setFont(Font.font(20));

            Label labelSave = new Label();
            labelSave.setLayoutX(10);
            labelSave.setLayoutY(320);
            labelSave.setFont(Font.font(20));


            Button bt1 = new Button("单图压缩");
            bt1.setOnAction(e->{
                labelSrc.setText(findFile("选择压缩文件"));
            });
            bt1.setLayoutX(10);
            bt1.setLayoutY(30);
            bt1.setFont(Font.font(30));

            Button bt2 = new Button("多图压缩");
            bt2.setOnAction(e->{
                labelSrc.setText(findDirectory("选择压缩图片所在文件夹"));
            });
            bt2.setLayoutX(200);
            bt2.setLayoutY(30);
            bt2.setFont(Font.font(30));

            Button bt3 = new Button("选择保存路径");
            bt3.setOnAction(e->{
               labelSave.setText(findDirectory("选择保存路径"));
            });
            bt3.setLayoutX(10);
            bt3.setLayoutY(200);
            bt3.setFont(Font.font(30));



          ComboBox comboBox = new ComboBox();
          comboBox.getItems().addAll("文件按时间命名","文件按原名命名");
          comboBox.setValue("文件按时间命名");
          comboBox.setLayoutX(10);
          comboBox.setLayoutY(380);
          comboBox.setPrefSize(300,70);

            Button bt6  = new Button("压缩");
            bt6.setOnAction(e->{
                    if (comboBox.getValue().equals("文件按时间命名")) execute(labelSrc.getText(), labelSave.getText(), 1);
                    else execute(labelSrc.getText(), labelSave.getText(), 2);
            });
            bt6.setLayoutX(0);
            bt6.setLayoutY(500);
            bt6.setPrefSize(900,200);
            bt6.setFont(Font.font(30));


            //不同的pane不能添加同一个bt
            pane.getChildren().addAll(bt1,bt2,labelSrc,bt3,labelSave,comboBox,bt6,textSrc,textSave);






            Scene scene = new Scene(pane);
            primaryStage.setTitle("图片压缩");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.show();

        }

        //找文件
        static String findFile(String txt){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(txt);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("图片类型",  "*.PNG","*.JPG", "*.GIF","*.BMP","*.JPEG")
            );
            File file =fileChooser.showOpenDialog(new Stage());
            String filePath = file.getPath();
            return filePath;
        }

        //找目录
        static String findDirectory(String txt){
            DirectoryChooser directoryChooser=new DirectoryChooser();
            directoryChooser.setTitle(txt);
            File directory = directoryChooser.showDialog(new Stage());
            String directoryPath = directory.getPath();
            return directoryPath;
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}