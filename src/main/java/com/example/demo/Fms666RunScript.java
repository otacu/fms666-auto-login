package com.example.demo;

import com.example.demo.util.BinaryImageUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.Select;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Fms666RunScript {

    static final String ORIGIN_CODE_FILE_PATH = "E:\\originCode.png";

    static final String TRANSFERED_CODE_FILE_PATH = "E:\\transferedCode.png";

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    static final String USER = "admin";
    static final String PASS = "123456";

    private static WebDriver driver;

    public static void main(String[] args) throws Exception {
        // 注意chromedriver的版本要和chrome浏览器的版本一致
        System.setProperty("webdriver.chrome.driver", "E:\\yjs\\application\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("http://fms666.com/");
        // 页面最大化
        driver.manage().window().maximize();
        driver.findElement(By.id("query")).sendKeys("885522");
        driver.findElement(By.name("Submit")).click();
        Thread.sleep(1000);
        List<WebElement> itemElementList = driver.findElements(By.xpath("//td[@class='liWrap']"));
        itemElementList.get(0).findElement(By.xpath("a")).click();
        Thread.sleep(1000);
        // 坑点，加了下面这句才找到元素
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        driver.findElement(By.name("loginName")).sendKeys("jj180");
        driver.findElement(By.name("loginPwd")).sendKeys("dahxiaoh7262");

        WebElement codeImageElement = driver.findElement(By.id("pic_code"));
        File originCodeFile = new File(ORIGIN_CODE_FILE_PATH);
        FileUtils.copyFile(elementSnapshot(codeImageElement), originCodeFile);
        // 图片二值化
        BinaryImageUtil.binaryImage(ORIGIN_CODE_FILE_PATH, TRANSFERED_CODE_FILE_PATH);
        // 图片转字符串
        String code = transferImageToString(TRANSFERED_CODE_FILE_PATH);
        driver.findElement(By.name("ValidateCode")).sendKeys(code);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[@class='proPic3']/input[2]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[@data-action='Result.aspx']")).click();
        Thread.sleep(1000);
        WebElement iframe = driver.findElement(By.xpath("//iframe[@src='Result.aspx?id=0']"));
        driver.switchTo().frame(iframe);
        WebElement selectElement = driver.findElement(By.name("select"));
        String type = "幸運飛艇(3分鐘)";
        Select select = new Select(selectElement);
        select.selectByVisibleText(type);
        String lastPageStr = driver.findElement(By.id("pager")).findElement(By.xpath("//a[@title='尾頁']")).getAttribute("href");
        int totalPage = Integer.parseInt(lastPageStr.substring(lastPageStr.indexOf("page=") + 5, lastPageStr.indexOf("&id=")));

        // 指定开始页码
//        driver.findElement(By.id("txtPager")).clear();
//        driver.findElement(By.id("txtPager")).sendKeys("2074");
//        driver.findElement(By.id("btnPager")).click();
//        Thread.sleep(500);

        for (int i = 1; i <= totalPage; i++) {
            List<WebElement> rowElementList = driver.findElements(By.xpath("//tr[@align='center']"));
            for (WebElement rowElement : rowElementList) {
                List<WebElement> columnList = rowElement.findElements(By.tagName("td"));
                StringBuilder numberSb = new StringBuilder();
                StringBuilder sumSb = new StringBuilder();
                StringBuilder dragonTigerSb = new StringBuilder();
                FmsData fmsData = new FmsData();
                fmsData.setType(type);
                for (int j = 0; j < columnList.size(); j++) {
                    String value = columnList.get(j).getText();
                    if (j == 0) {
                        fmsData.setTermNo(value);
                    }
                    if (j == 1) {
                        fmsData.setDrawDate(value);
                    }
                    if (j >= 2 && j <= 11) {
                        value = columnList.get(j).findElement(By.tagName("span")).getAttribute("class").substring(3);
                        value = String.valueOf(Integer.parseInt(value));
                        numberSb.append(value);
                        if (j != 11) {
                            numberSb.append(",");
                        }
                    }
                    if (j >= 12 && j <= 14) {
                        sumSb.append(value);
                        if (j != 14) {
                            sumSb.append(",");
                        }
                    }
                    if (j >= 15 && j <= 19) {
                        dragonTigerSb.append(value);
                        if (j != 19) {
                            dragonTigerSb.append(",");
                        }
                    }
                }
                fmsData.setDrawNumber(numberSb.toString());
                fmsData.setSum(sumSb.toString());
                fmsData.setDragonTiger(dragonTigerSb.toString());
                save(fmsData);
            }
            driver.findElement(By.id("pager")).findElement(By.xpath("//a[@title='下一頁']")).click();
            Thread.sleep(500);
        }
        driver.quit();

    }

    public static void save(FmsData fmsData) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            String sql = String.format("insert into tb_fms_data (`type`,`term_no`,`draw_date`,`draw_number`,`sum`,`dragon_tiger`) values ('%s','%s','%s','%s','%s','%s')"
                    , fmsData.getType(), fmsData.getTermNo(), fmsData.getDrawDate(), fmsData.getDrawNumber(), fmsData.getSum(), fmsData.getDragonTiger());
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }
    }

    /**
     * 部分截图（元素截图）
     * 有时候需要元素的截图，不需要整个截图
     *
     * @throws Exception
     */
    public static File elementSnapshot(WebElement element) throws Exception {
        //创建全屏截图
        RemoteWebElement wrapsDriver = (RemoteWebElement) element;
        File screen = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
        BufferedImage image = ImageIO.read(screen);
        //获取元素的高度、宽度
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();

        //创建一个矩形使用上面的高度，和宽度
        java.awt.Rectangle rect = new java.awt.Rectangle(width, height);
        //元素坐标
        Point p = element.getLocation();
        BufferedImage img = image.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
        ImageIO.write(img, "png", screen);
        return screen;
    }

    /**
     * 图片转字符串
     *
     * @param transferedFilePath 经过二值化的图片地址
     * @return 字符串
     */
    public static String transferImageToString(String transferedFilePath) {
        String code = null;
        File transferedFile = new File(transferedFilePath);
        ITesseract instance = new Tesseract();//调用Tesseract
        URL url = ClassLoader.getSystemResource("tessdata");//获得Tesseract的文字库
        String tesspath = url.getPath().substring(1);
        instance.setDatapath(tesspath);//进行读取，默认是英文，如果要使用中文包，加上instance.setLanguage("chi_sim");
        instance.setLanguage("num");
        try {
            code = instance.doOCR(transferedFile);
            System.out.println("识别结果：" + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
}
