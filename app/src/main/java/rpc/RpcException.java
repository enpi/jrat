package rpc;

/**
 * An exception wrapper class. In case you need to be able to tell where 
 * it came from later or just hide the original exception.  
 * @author h.indzhov
 */
public class RpcException extends RuntimeException {

    private static final long serialVersionUID = -7053882704721668233L;

    public RpcException(Exception e) {
        super(e);
    }

    public RpcException(String string) {
        super(string);
    }

}
