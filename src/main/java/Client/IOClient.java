package Client;

import common.Message.RpcResponse;
import common.Message.RpcRequest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author cnwang
 * @Date created in 21:06 2025/3/12
 */
public class IOClient {
    public static RpcResponse sendRequest(String host, int port, RpcRequest request){
        try{
            Socket socket = new Socket(host,port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(request);
            oos.flush();

            RpcResponse response = (RpcResponse) ois.readObject();
            return response;

        }catch(IOException|ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}
