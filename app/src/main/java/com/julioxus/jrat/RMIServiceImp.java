package com.julioxus.jrat;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by julioxus on 14/03/16.
 */
public class RMIServiceImp implements RMIService {

    Context context;

    RMIServiceImp(Context context){
        this.context = context;
    }

    public void sayHello(){
        Toast.makeText(context, "Hello world!", Toast.LENGTH_SHORT).show();
    }
}


