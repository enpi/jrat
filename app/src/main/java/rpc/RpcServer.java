package rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RpcServer {

    private Map<String, Object> serviceNameToImpl = Collections.synchronizedMap(new HashMap<String, Object>());

    private int serverPort = 0;
    private ServerSocket serverSocket = null;
    private Thread serverThread = null;
    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    public void start(int port) {
        if (isRunning()) {
            throw new RpcException("Server already started on port " + serverPort);
        }

        isRunning = true;
        serverPort = port;
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(RpcServer.this.serverPort);
                } catch (Exception e) {
                    throw new RpcException(e);
                }

                while (!Thread.interrupted()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        Thread clientThread = new Thread(new RpcHandler(clientSocket));
                        clientThread.start();
                    } catch (Exception e) {
                        throw new RpcException(e);
                    }
                }
                isRunning = false;
            }
        });
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
    }

    public void registerService(String serviceName, Object serviceImpl) {
        serviceNameToImpl.put(serviceName, serviceImpl);
    }

    public boolean isServiceRegistered(String serviceName) {
        return serviceNameToImpl.containsKey(serviceName);
    }

    public void unregisterService(String serviceName) {
        serviceNameToImpl.remove(serviceName);
    }

    private class RpcHandler implements Runnable {

        private Socket clientSocket = null;

        public RpcHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            tryReadWriteObjects(clientSocket);
        }
    }

    private void tryReadWriteObjects(Socket clientSocket) {
        try {
            RpcRequest remoteRequest = readRequestObject(clientSocket);
            RpcResponse remoteResponse = handleMethodCall(remoteRequest);
            writeResponseObject(clientSocket, remoteResponse);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    private void writeResponseObject(Socket clientSocket, RpcResponse remoteResponse) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        oos.writeObject(remoteResponse);
        oos.flush();
    }

    private RpcRequest readRequestObject(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        RpcRequest remoteRequest = (RpcRequest) ois.readObject();
        return remoteRequest;
    }

    private RpcResponse handleMethodCall(RpcRequest remoteRequest) {

        String serviceName = checkServiceName(remoteRequest);

        Object serviceImpl = serviceNameToImpl.get(serviceName);
        Class<?> serviceImplClass = serviceImpl.getClass();

        checkServiceInterface(remoteRequest, serviceImplClass);

        String methodName = remoteRequest.getMethodName();
        Class<?>[] argTypes = remoteRequest.getArgTypes();

        Method method = null;
        try {
            method = serviceImplClass.getMethod(methodName, argTypes);
        } catch (Exception e) {
            throw new RpcException(e);
        }

        Object[] args = remoteRequest.getArgs();
        Object returnValue = null;
        Exception exception = null;
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            returnValue = method.invoke(serviceImpl, args);
        } catch (Exception e) {
            exception = e;
        }

        RpcResponse rpcResponse = new RpcResponse();

        if (exception != null) {
            rpcResponse.setException(exception);
        } else {
            rpcResponse.setReturnValue(returnValue);
        }
        return rpcResponse;
    }

    private void checkServiceInterface(RpcRequest remoteRequest, Class<?> serviceImplClass) {
        String interfaceCName = remoteRequest.getInterfaceCName();
        Class<?> interfaceClass = null;
        try {
            interfaceClass = Class.forName(interfaceCName);
        } catch (ClassNotFoundException e) {
            throw new RpcException(e);
        }

        Class<?>[] interfaces = serviceImplClass.getInterfaces();
        int idx = Arrays.binarySearch(interfaces, interfaceClass, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.equals(o2) ? 0 : -1;
            }
        });
        if (idx < 0) {
            throw new RpcException(interfaceCName + " not implemeted by service with name "
                    + remoteRequest.getServiceName());
        }
    }

    private String checkServiceName(RpcRequest remoteRequest) {
        String serviceName = remoteRequest.getServiceName();
        if (!serviceNameToImpl.containsKey(serviceName)) {
            throw new RpcException("Cannot find service with name " + serviceName);
        }
        return serviceName;
    }
}
