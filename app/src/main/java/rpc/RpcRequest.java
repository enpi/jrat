package rpc;
import java.io.Serializable;

/**
 * An object wrapper for an actual method call.
 * @author h.indzhov
 *
 */
class RpcRequest implements Serializable {

    private static final long serialVersionUID = -5778642767500477993L;

    private String serviceName = "";
    private String interfaceCName = "";
    private String methodName = "";
    private Class<?>[] argTypes = null;
    private Object[] args = null;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInterfaceCName() {
        return interfaceCName;
    }

    public void setInterfaceCName(String interfaceCName) {
        this.interfaceCName = interfaceCName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}

