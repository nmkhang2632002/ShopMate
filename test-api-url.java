// Test để verify API URL trong Android app

public class TestApiUrl {
    public static void main(String[] args) {
        // Simulate Constants values
        String BASE_URL_DEPLOYMENT = "https://saleapp-mspd.onrender.com/";
        String BASE_URL_LOCAL = "http://192.168.1.52:8080/";
        String API_VERSION = "v1";
        
        System.out.println("=== API URL Test ===");
        
        // Test deployment URL
        String deploymentUrl = BASE_URL_DEPLOYMENT + API_VERSION + "/";
        System.out.println("Deployment URL: " + deploymentUrl);
        System.out.println("Login endpoint: " + deploymentUrl + "auth/login");
        
        // Test local URL  
        String localUrl = BASE_URL_LOCAL + API_VERSION + "/";
        System.out.println("Local URL: " + localUrl);
        System.out.println("Login endpoint: " + localUrl + "auth/login");
        
        System.out.println("====================");
        
        // Expected results:
        // Deployment: https://saleapp-mspd.onrender.com/v1/auth/login
        // Local: http://192.168.1.52:8080/v1/auth/login
    }
}
