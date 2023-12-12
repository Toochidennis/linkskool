package com.digitaldream.winskool.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FileTransferService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final int SOCKET_TIMEOUT = 50000;
    public static final String ACTION_SEND_FILE = "com.digitaldream.linkskool.Utils.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String JSON = "json";
    public static final String EXTRAS_FILE_PATH2 = "file_url2";
    public static final String EXTRAS_FILE_ARRAY = "file_arr";
    public static final String TYPE = "type";


    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            String json = intent.getExtras().getString(JSON);
            String type = intent.getExtras().getString(TYPE);
            ArrayList<File> files = new ArrayList();
            files = (ArrayList<File>) intent.getSerializableExtra(EXTRAS_FILE_ARRAY);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            try {
                Log.d("response", "Opening client socket - ");
                client2(socket,host,port,json,files,type);
            } catch (IOException e) {
                Log.e("response", e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static boolean copyFile (InputStream inputStream, OutputStream out){
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            //Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }


    private void client(Socket socket,String host,int port,String filepath,String json) throws IOException {
        File file;
        file = new File(filepath);

            socket.bind(null);
        socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
        DataOutputStream outString = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

          outString.writeUTF(file.getName());
          outString.writeUTF(json);
           outString.flush();

        byte[] b = new byte[20*1024];



        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int i ;

        while ((i = in.read(b)) >0    ) {
            out.write( b , 0 , i);
        }

        out.close();
        in.close();

        socket.close();


    }

    private void client2(Socket socket, String host, int port, String json, ArrayList<File> files,String type) throws IOException {
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            //write the number of files to the server
            dos.writeUTF(json);
            dos.writeUTF(type);
            dos.writeInt(files.size());
            dos.flush();
            if(type.equals("file")) {
                //write file names
                for (int i = 0; i < files.size(); i++) {
                    dos.writeUTF(files.get(i).getName());
                    dos.writeLong(files.get(i).length());
                    dos.flush();
                }

                //buffer for file writing, to declare inside or outside loop?
                int n = 0;
                byte[] buf = new byte[4092];
                //outer loop, executes one for each file
                for (int i = 0; i < files.size(); i++) {

                    //create new fileinputstream for each file
                    FileInputStream fis = new FileInputStream(files.get(i));

                    //write file to dos
                    while ((n = fis.read(buf)) != -1) {
                        dos.write(buf, 0, n);
                        dos.flush();

                    }
                }
            }
            dos.close();
            Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
