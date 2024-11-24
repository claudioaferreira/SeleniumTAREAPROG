package Tests;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class TallerAppTest {

    WebDriver driver;

    static ExtentReports extent;
    static ExtentTest test;

    // Selectores
    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit']");
    private static final By MANTENIMIENTO_BUTTON = By.id("dropdownMenuButton");
    private static final By LOGOUT_BUTTON = By.xpath("//a[contains(text(), ' Logout ')]");
    private static final By LOGIN_TEXT = By.xpath("//h3[text()='Login']");
    private static final By ALERT_POPUP = By.cssSelector("div.swal2-popup.swal2-icon-error");
    private static final By ALERT_TITLE = By.id("swal2-title");

    @BeforeTest
    public void setupExtentReports() {
        // Configuración del reporte ExtentReports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("C:\\Users\\Soporte.DESKTOP-62PM8SP\\Desktop\\AUTOMATIZACION\\Project\\SeleniumTestProject\\Reports\\report.html");
        sparkReporter.config().setDocumentTitle("Test Report - TallerAPP");
        sparkReporter.config().setReportName("TallerAPP Tests");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);  // Se adjunta el reporter de tipo Spark
    }

    @BeforeMethod
    public void setup() {
        System.out.println("Inicializando el navegador...");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://localhost:4200/login");
    }


    public String captureScreen(String nombreProceso) {
        String path;
        try {
            // Fecha y hora actual
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());

            // Capturar pantalla
            WebDriver augmentedDriver = new Augmenter().augment(driver);
            File source = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);

            // Ruta
            path = "C:\\Users\\Soporte.DESKTOP-62PM8SP\\Desktop\\AUTOMATIZACION\\Project\\SeleniumTestProject\\Reports\\screenshot_"
                    + nombreProceso + "_" + timestamp + ".png";
            FileUtils.copyFile(source, new File(path));
        } catch (IOException e) {
            path = "Failed to capture screenshot: " + e.getMessage();
        }
        return path;
    }


    @Test
    public void testLogin() {
        System.out.println("Ejecutando prueba de Login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Crear un test específico en el reporte para testLogin
        ExtentTest testLogin = extent.createTest("Login Test", "Prueba de inicio sesión");

        try {
            // Credenciales
            String username = "claudioa_ferreira";
            String password = "123456";

            // Introducir credenciales y enviar formulario
            driver.findElement(USERNAME_INPUT).sendKeys(username);
            driver.findElement(PASSWORD_INPUT).sendKeys(password);
            driver.findElement(LOGIN_BUTTON).click();

            // Verificar que el botón "MANTENIMIENTO" esté visible
            WebElement mantenimientoButton = wait.until(ExpectedConditions.visibilityOfElementLocated(MANTENIMIENTO_BUTTON));
            assert mantenimientoButton.isDisplayed() : "El login no fue exitoso.";

            System.out.println("LogIn realizado con éxito.");

            String screenshotPath = captureScreen("testLogin");
            testLogin.pass("Prueba de login Exitosa")
                    .addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            testLogin.fail("Error durante la prueba de login: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogout() {
        System.out.println("Ejecutando prueba de logout...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Crear un test específico en el reporte para testLogout
        ExtentTest testLogout = extent.createTest("Logout Test", "Prueba de cerrar sesión");

        try {
            // Iniciar sesión primero
            String username = "claudioa_ferreira";
            String password = "123456";
            driver.findElement(USERNAME_INPUT).sendKeys(username);
            driver.findElement(PASSWORD_INPUT).sendKeys(password);
            driver.findElement(LOGIN_BUTTON).click();

            // Verificar que el botón "Logout" esté presente y hacer clic en él
            WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_BUTTON));
            logoutButton.click();

            // Verificar que se redirija al login al hacer logout
            WebElement loginText = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_TEXT));
            assert loginText.isDisplayed() : "No se redirigió correctamente al login después del logout.";
            System.out.println("Logout realizado con éxito.");

            String screenshotPath = captureScreen("testLogout");
            testLogout.pass("Prueba de logout Exitosa")
                    .addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            testLogout.fail("Error durante la prueba de logout: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLoginFailed() {
        System.out.println("Ejecutando prueba de inicio de sesión fallida...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        ExtentTest testLoginFailed = extent.createTest("Login fail", "Prueba de loginFail");

        try{
            // Introducir credenciales inválidas
            driver.findElement(USERNAME_INPUT).sendKeys("usuario_invalido");
            driver.findElement(PASSWORD_INPUT).sendKeys("contraseña_invalida");
            driver.findElement(LOGIN_BUTTON).click();

            // Esperar y verificar que aparezca la alerta de error
            WebElement alertPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(ALERT_POPUP));
            WebElement alertTitle = driver.findElement(ALERT_TITLE);
            assert alertTitle.getText().equals("Opps!! algo salio mal") : "El mensaje de error no coincide.";
            System.out.println("Inicio de sesión fallido correctamente detectado.");


            String screenshotPath = captureScreen("testLoginFailed");
            testLoginFailed.pass("Prueba de loginFail Exitosa")
                    .addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            testLoginFailed.fail("Error durante la prueba de loginFail: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testLoginEmpty() {
        System.out.println("Ejecutando prueba de inicio de sesión fallida...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ExtentTest testLoginEmpty = extent.createTest("Login Empty", "Prueba de login Empty");
        try  {
            // Introducir credenciales inválidas
            driver.findElement(USERNAME_INPUT).sendKeys("");
            driver.findElement(PASSWORD_INPUT).sendKeys("");
            driver.findElement(LOGIN_BUTTON).click();

            // Esperar y verificar que aparezca la alerta de error
            WebElement alertPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(ALERT_POPUP));
            WebElement alertTitle = driver.findElement(ALERT_TITLE);
            assert alertTitle.getText().equals("Error?") : "El mensaje de error no coincide.";
            System.out.println("No valida.");


            String screenshotPath = captureScreen("testLoginEmpty");
            testLoginEmpty.pass("Prueba de testLoginEmpty Exitosa")
                    .addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            testLoginEmpty.fail("Error durante la prueba de testLoginEmpty: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

    @Test
    public void testMantenimientoExistenCards() {
        System.out.println("Ejecutando prueba de validación de títulos de cards en MANTENIMIENTO...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ExtentTest testMantenimientoExistenCards = extent.createTest("Card en Mantenimiento", "Prueba de validacion card mantenimiento");
        try{

            //Iniciar sesión
            String username = "claudioa_ferreira";
            String password = "123456";
            driver.findElement(USERNAME_INPUT).sendKeys(username);
            driver.findElement(PASSWORD_INPUT).sendKeys(password);
            driver.findElement(LOGIN_BUTTON).click();

            //Navegar al botón "MANTENIMIENTO"
            WebElement mantenimientoButton = wait.until(ExpectedConditions.visibilityOfElementLocated(MANTENIMIENTO_BUTTON));
            mantenimientoButton.click();

            //Títulos esperados
            String[] expectedTitles = {
                    "EQUIPOS",
                    "CATEGORÍA",
                    "MARCA",
                    "MODELO",
                    "MODELO",
                    "USUARIO",
                    "Empleados"
            };

            // Validar que cada título esperado exista en la página
            for (String expectedTitle : expectedTitles) {
                try {
                    WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//span[contains(@class, 'card-title') and contains(text(), '" + expectedTitle + "')]")));

                    // Valida que el card visible
                    assert card.isDisplayed() : "No se encontró el card con el título: " + expectedTitle;
                    System.out.println("Validado: " + expectedTitle);

                } catch (Exception e) {
                    System.err.println("Error al validar el card con el título: " + expectedTitle);
                    e.printStackTrace();
                }
            }

            System.out.println("Todos los títulos de los cards fueron validados exitosamente.");

            String screenshotPath = captureScreen("testMantenimientoExistenCards");
            testMantenimientoExistenCards.pass("Prueba de testMantenimientoExistenCards Exitosa")
                    .addScreenCaptureFromPath(screenshotPath);

        } catch (Exception e) {
            testMantenimientoExistenCards.fail("Error durante la prueba de testMantenimientoExistenCards: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @AfterMethod
    public void teardown() {
        System.out.println("Cerrando el navegador...");
        if (driver != null) {
            driver.quit();

        }

    }

    @AfterTest
    public void end(){
        extent.flush();
    }

}
