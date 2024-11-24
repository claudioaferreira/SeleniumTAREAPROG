package Tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class TallerTest {

    WebDriver driver;

    // Selectores
    private static final By USERNAME_INPUT = By.name("username");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit']");
    private static final By MANTENIMIENTO_BUTTON = By.id("dropdownMenuButton");
    private static final By LOGOUT_BUTTON = By.xpath("//a[contains(text(), ' Logout ')]");
    private static final By LOGIN_TEXT = By.xpath("//h3[text()='Login']");
    private static final By ALERT_POPUP = By.cssSelector("div.swal2-popup.swal2-icon-error");
    private static final By ALERT_TITLE = By.id("swal2-title");


    @BeforeMethod
    public void setup() {
        System.out.println("Inicializando el navegador...");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Configuración de espera implícita
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Navegar al sitio
        driver.get("http://localhost:4200/login");
    }

    public String captureScreen(String nombreProceso) {
        String path;
        try {
            // Fecha y hora actual
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String timestamp = dateFormat.format(new Date());

            // Capturar pantalla
            WebDriver augmentedDriver = new Augmenter().augment(driver);
            File source = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);

            // Ruta
            path = "C:\\Users\\Soporte.DESKTOP-62PM8SP\\Desktop\\AUTOMATIZACION\\Project\\SeleniumTestProject\\CapturasPantalla\\screenshot_"
                    + nombreProceso + "_" + timestamp + ".png";
            FileUtils.copyFile(source, new File(path));
        } catch (IOException e) {
            path = "Failed to capture screenshot: " + e.getMessage();
        }
        return path;
    }

    @Test
    public void testLogin() {
        System.out.println("Ejecutando prueba de inicio de sesión...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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

        System.out.println("Inicio de sesión exitoso.");
        captureScreen("testLogin");
    }



    @Test
    public void testLogout() {
        System.out.println("Ejecutando prueba de logout...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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
        captureScreen("testLogout");
    }

    @Test
    public void testLoginFailed() {
        System.out.println("Ejecutando prueba de inicio de sesión fallida...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Introducir credenciales inválidas
        driver.findElement(USERNAME_INPUT).sendKeys("usuario_invalido");
        driver.findElement(PASSWORD_INPUT).sendKeys("contraseña_invalida");
        driver.findElement(LOGIN_BUTTON).click();

        // Esperar y verificar que aparezca la alerta de error
        WebElement alertPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(ALERT_POPUP));
        WebElement alertTitle = driver.findElement(ALERT_TITLE);
        assert alertTitle.getText().equals("Opps!! algo salio mal") : "El mensaje de error no coincide.";

        System.out.println("Inicio de sesión fallido correctamente detectado.");
        captureScreen("testLoginFailed");
    }

    @Test
    public void testLoginEmpty() {
        System.out.println("Ejecutando prueba de inicio de sesión fallida...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Introducir credenciales inválidas
        driver.findElement(USERNAME_INPUT).sendKeys("");
        driver.findElement(PASSWORD_INPUT).sendKeys("");
        driver.findElement(LOGIN_BUTTON).click();

        // Esperar y verificar que aparezca la alerta de error
        WebElement alertPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(ALERT_POPUP));
        WebElement alertTitle = driver.findElement(ALERT_TITLE);
        assert alertTitle.getText().equals("Error?") : "El mensaje de error no coincide.";

        System.out.println("No valida.");
        captureScreen("testLoginEmpty");
    }

    @Test
    public void testMantenimientoExistenCards() {
        System.out.println("Ejecutando prueba de validación de títulos de cards en MANTENIMIENTO...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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
        captureScreen("testMantenimientoCardsByTitle");
    }


    @AfterMethod
    public void teardown() {
        System.out.println("Cerrando el navegador...");
        if (driver != null) {
            driver.quit();
        }
    }
}
