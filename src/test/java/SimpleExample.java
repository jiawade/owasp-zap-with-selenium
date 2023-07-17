
import java.time.Duration;
import io.smart.browser.configuration.Configuration;
import io.smart.browser.configuration.impls.ChromeConfiguration;
import io.smart.browser.factory.impls.SeleniumBrowser;
import io.smart.element.impls.ElementByXpath;
import io.smart.enums.BrowserType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.zaproxy.clientapi.core.*;


public class SimpleExample {

    private static final String ZAP_ADDRESS = "127.0.0.1";
    private static final int ZAP_PORT = 8090;
    private static final String ZAP_API_KEY = "kls5afe30ekk45tc37sj9lio0h"; // Change this if you have set the apikey in ZAP via Options / API

    private static final String TARGET = "https://www.google.com/";

    public static void passiveScan(ClientApi zapTest) {
        long progress;
        try {
            do
            {
                progress = Integer.parseInt(((ApiResponseElement) zapTest.pscan.recordsToScan()).getValue());
                System.out.println("Passive Scan progress: " + progress + " records left ");
                Thread.sleep(1000);
            } while (progress >= 1);
            System.out.println("Passive Scan complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spiderScan(ClientApi zapTest) {
        try {
            System.out.println("Spider : " + TARGET);
            ApiResponse resp = zapTest.spider.scan(TARGET, null, null, null, null);
            String scanid;
            int progress;
            scanid = ((ApiResponseElement) resp).getValue();
            do
            {
                Thread.sleep(1000);
                progress = Integer.parseInt(((ApiResponseElement) zapTest.spider.status(scanid)).getValue());
                System.out.println("Spider progress : " + progress + "%");
            } while (progress < 100);
            System.out.println("Spider complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void activeScan(ClientApi zapTest) {
        try {
            System.out.println("Active scan : " + TARGET);
            String scanid;
            int progress;
            zapTest.ascan.clearExcludedFromScan();
            ApiResponse resp = zapTest.ascan.scan(TARGET, "True", "False", null, null, null);
            scanid = ((ApiResponseElement) resp).getValue();
            do
            {
                Thread.sleep(5000);
                progress = Integer.parseInt(((ApiResponseElement) zapTest.ascan.status(scanid)).getValue());
                System.out.println("Active Scan progress : " + progress + "%");
            } while (progress < 100);
            System.out.println("Active Scan complete");

        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void generateReport(ClientApi zapTest) {
        System.out.println("generate report");
        String title = "zap test";
        String template = "traditional-html";
        String description = "zap test";
        String reportFileName = "zap.html";
        String targetFolder = "C:\\Users\\abc\\Desktop";
        System.out.println(targetFolder);

        try {
            ApiResponse response = zapTest.reports.generate(title, template, null, description, null, null, null, null,
                    null, reportFileName, null, targetFolder, null);
            System.out.println("Zap Report : " + response.toString());
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(ZAP_ADDRESS + ":" + ZAP_PORT).setSslProxy(ZAP_ADDRESS + ":" + ZAP_PORT);
        proxy.setSslProxy(ZAP_ADDRESS + ":" + ZAP_PORT).setSslProxy(ZAP_ADDRESS + ":" + ZAP_PORT);

        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);

        Configuration c = ChromeConfiguration.builder()
                .chromeOptions(options)
                .width(1920)
                .height(1080)
                .proxy(proxy)
                .duration(Duration.ofSeconds(60))
                .maximizeWindow(true)
                .headless(false)
                .build();
        SeleniumBrowser vv = new SeleniumBrowser().setUp(BrowserType.CHROME, c);
        ClientApi zapTest = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);

        ElementByXpath browser = new ElementByXpath(vv.getDriver());
        browser.get(TARGET);
        spiderScan(zapTest);
        passiveScan(zapTest);
        activeScan(zapTest);
        generateReport(zapTest);
        browser.closeBrowser();

    }
}