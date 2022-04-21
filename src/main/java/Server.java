import com.sun.net.httpserver.*;
// import org.apache.commons.text.StringEscapeUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import endpoint.Endpoint1;
import endpoint.Endpoint2;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(48888), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
        HttpContext context = server.createContext("/", new Endpoint1());
        context = server.createContext("/endpoint2", new Endpoint2());
        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println("Server started");
    }
}