package rpc;

import java.io.Serializable;

/**
 * An object wrapper for an actual method return value.
 * @author h.indzhov
 */
class RpcResponse implements Serializable {

    private static final long serialVersionUID = 5162234232160485880L;

    private Object returnValue = null;

    private Exception exception = null;

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object value) {
        this.returnValue = value;
    }

    public boolean isSuccessfull() {
        return exception == null;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
