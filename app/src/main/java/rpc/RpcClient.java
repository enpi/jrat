package rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RcpClient
 * @author h.indzhov
 */
public class RpcClient implements InvocationHandler {

    private String serviceName = "";
    private Class<?> interfaceClass = null;
    private String host = "";
    private int port = 0;

    private RpcClient() {
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        try {
            RpcRequest rpcRequest = new RpcRequest();

            rpcRequest.setServiceName(serviceName);
            String interfaceCName = interfaceClass.getCanonicalName();
            rpcRequest.setInterfaceCName(interfaceCName);

            String methodName = method.getName();
            rpcRequest.setMethodName(methodName);
            Class<?>[] argTypes = method.getParameterTypes();
            rpcRequest.setArgTypes(argTypes);
            rpcRequest.setArgs(args);

            Socket clientSocket = new Socket(host, port);
            writeRequestObject(rpcRequest, clientSocket);
            RpcResponse rpcResponse = readResponseObject(clientSocket);

            if (!rpcResponse.isSuccessfull()) {
                throw new RpcException(rpcResponse.getException());
            }

            Object returnValue = rpcResponse.getReturnValue();
            return returnValue;
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    private RpcResponse readResponseObject(Socket clientSocket)
            throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(
                clientSocket.getInputStream());
        RpcResponse rpcResponse = (RpcResponse) ois.readObject();
        clientSocket.shutdownInput();
        clientSocket.close();
        return rpcResponse;
    }

    private void writeRequestObject(RpcRequest rpcRequest, Socket clientSocket)
            throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                clientSocket.getOutputStream());
        oos.writeObject(rpcRequest);
        clientSocket.shutdownOutput();
    }

    public static <T> T lookupService(String rpcAddress, Class<T> interfaceClass) {
        // "rpc://192.168.1.1:8080/service"

        Pattern pattern = Pattern
                .compile("^rpc://(\\d{1,3}(?:\\.\\d{1,3}){3}):(\\d{1,5})/(\\w+)$");
        Matcher matcher = pattern.matcher(rpcAddress);
        if (!matcher.matches()) {
            throw new RpcException("Cannot parse rpc address " + rpcAddress
                    + " (Format: rpc://ip:port/serviceName)");
        }

        String host = matcher.group(1);
        int port = Integer.parseInt(matcher.group(2));
        String serviceName = matcher.group(3);

        return lookupService(host, port, serviceName, interfaceClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookupService(String host,
                                      int port, String serviceName, Class<T> interfaceClass) {
        RpcClient remoteClient = new RpcClient();
        remoteClient.interfaceClass = interfaceClass;
        remoteClient.host = host;
        remoteClient.port = port;
        remoteClient.serviceName = serviceName;
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass }, remoteClient);
    }

}
