package com.digitaldream.winskool.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.winskool.adapters.PeersListAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.WifiDirectBroadcastReceiver;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.os.Looper.getMainLooper;

public class ShareDialog extends Dialog implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.ActionListener, PeersListAdapter.OnDeviceClickListener {
    private Context context;
    public static WifiP2pManager mManager;
    public static WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiManager wifi;
    IntentFilter intentFilter;
    private RecyclerView recyclerView;
    private List<WifiP2pDevice> deviceList;
    private PeersListAdapter adapter;
    private TextView emptyState;
    private static final int REQUEST_CHECK_SETTINGS = 4;
    Socket socket = new Socket();
    public String deviceAddress;
    private static final int CHOOSE_FILE_RESULT_CODE = 5;
    private Uri fileUri;
    private ProgressBar progressDialog = null;
    public static WifiP2pInfo info;
    public static String clientIp;
    public static Dao<CourseOutlineTable, Long> courseOutlineDao;
    private DatabaseHelper databaseHelper;


    public ShareDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_dialog);
        progressDialog = findViewById(R.id.progress);
        try {
            databaseHelper = new DatabaseHelper(getContext());
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        deviceList = new ArrayList<>();
      //  emptyState = findViewById(R.id.device_empty_state);


        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(context, "wifi not found", Toast.LENGTH_SHORT).show();

            }
        });

        recyclerView = findViewById(R.id.peers_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new PeersListAdapter(context, deviceList, this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int reason) {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.dismiss();

        this.info = info;


        if (info.groupFormed && !info.isGroupOwner) {
            new ClientAsyncTask().execute();

        }

        if (info.groupFormed && info.isGroupOwner) {
            new ClientAsynktask2().execute();

        }

        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(context)
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            new FileServerAsyncTask(context)
                    .execute();

        }
        // hide the connect button
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        progressDialog.setVisibility(View.GONE);
        deviceList.clear();

        deviceList.addAll(peers.getDeviceList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceClick(int position) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceList.get(position).deviceAddress;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                Toast.makeText(context, "connection failed", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        context.registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        context.unregisterReceiver(mReceiver);

    }

    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private TextView statusText;


        public FileServerAsyncTask(Context context) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        public void server(int port) throws IOException {
            ServerSocket serverSocket=null;

            boolean ascoltando=true;

            serverSocket = new ServerSocket(port);
            Socket s;
            BufferedReader br1=null;

            boolean r=true;
            BufferedInputStream bis=null;
            Scanner sc;
            FileOutputStream fout;

            while(true)
            {

                s=serverSocket.accept();// this socket

                String filename="";
                String json="";
                InputStream in = null;
                OutputStream out = null;

                DataInputStream inString;


                inString = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                filename = inString.readUTF();
                json = inString.readUTF();

                Log.i("Server Json Insert", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
                    queryBuilder.where().eq("id", jsonObject.getString("id"));
                    List<CourseOutlineTable> courseOutlineList = queryBuilder.query();
                    if (courseOutlineList.size() == 0) {
                        CourseOutlineTable cot = new CourseOutlineTable();
                        cot.setLevelId(jsonObject.getString("levelId"));
                        cot.setCourseId(jsonObject.getString("courseId"));
                        cot.setTitle(jsonObject.getString("title"));
                        cot.setObjective(jsonObject.getString("objective"));
                        cot.setWeek(jsonObject.getString("week"));
                        cot.setId(jsonObject.getString("id"));
                        cot.setCourseName(jsonObject.optString("courseName"));
                        cot.setLevelName(jsonObject.optString("levelName"));
                        cot.setNoteMaterialPath(jsonObject.getString("noteMaterialPath"));
                        cot.setOtherMatherialPath(jsonObject.getString("otherMaterialPath"));
                        cot.setType(jsonObject.getString("type"));
                        cot.setJson(jsonObject.optString("json"));

                        Log.i("Server Json Insert", jsonObject.optString(
                                "json"));
                        courseOutlineDao.create(cot);
                    }else{
                        UpdateBuilder<CourseOutlineTable,Long> updateBuilder = courseOutlineDao.updateBuilder();
                        updateBuilder.updateColumnValue("week",jsonObject.getString("week"));
                        updateBuilder.updateColumnValue("objective",jsonObject.getString("objective"));
                        updateBuilder.updateColumnValue("noteMaterialPath",jsonObject.getString("noteMaterialPath"));
                        updateBuilder.updateColumnValue("otherMatherialPath",jsonObject.getString("otherMaterialPath"));
                        updateBuilder.updateColumnValue("title",jsonObject.getString("title"));
                        updateBuilder.updateColumnValue("json",jsonObject.optString("json"));
                        updateBuilder.where().eq("id",jsonObject.getString("id"));

                        Log.i("Server Json Update", jsonObject.optString(
                                "json"));
                        updateBuilder.update();
                    }
                }catch (JSONException | SQLException e){
                    e.printStackTrace();
                }
                in = s.getInputStream();

                final File f = new File(context.getExternalFilesDir("received"),
                        filename);
                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                out = new FileOutputStream(f);

                byte[] b = new byte[20*1024];

                int i ;


                while((i = inString.read(b)) >0){
                    out.write(b, 0, i);
                }
                out.close();
                inString.close();
                s.close();

            }

        }

        void server2(int port) throws IOException {
            try {
                ServerSocket serverSocket=null;


                serverSocket = new ServerSocket(port);
                Socket socket =serverSocket.accept();

                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                //read the number of files from the client
                String json = dis.readUTF();
                String type = dis.readUTF();

                JSONObject jsonObject = new JSONObject(json);
                QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
                queryBuilder.where().eq("id", jsonObject.getString("id"));
                List<CourseOutlineTable> courseOutlineList = queryBuilder.query();
                if (courseOutlineList.size() == 0) {
                    CourseOutlineTable cot = new CourseOutlineTable();
                    cot.setLevelId(jsonObject.getString("levelId"));
                    cot.setCourseId(jsonObject.getString("courseId"));
                    cot.setTitle(jsonObject.getString("title"));
                    cot.setObjective(jsonObject.getString("objective"));
                    cot.setWeek(jsonObject.getString("week"));
                    cot.setId(jsonObject.getString("id"));
                    cot.setCourseName(jsonObject.optString("courseName"));
                    cot.setLevelName(jsonObject.optString("levelName"));
                    cot.setNoteMaterialPath(jsonObject.optString("noteMaterialPath"));
                    cot.setOtherMatherialPath(jsonObject.optString("otherMaterialPath"));
                    cot.setType(jsonObject.getString("type"));
                    cot.setJson(jsonObject.optString("json"));
                    courseOutlineDao.create(cot);
                }

                if(type.equals("file")){
                int number = dis.readInt();
                ArrayList<File> files = new ArrayList<File>(number);

                //read file names, add files to arraylist
                ArrayList<Long> fileSizeArr = new ArrayList<>();
                for(int i = 0; i< number;i++){
                    File file = new File(dis.readUTF());
                    files.add(file);
                    long fileSize = dis.readLong();
                    fileSizeArr.add(fileSize);
                }
                int n = 0;
                byte[]buf = new byte[4092];

                //outer loop, executes one for each file
                for(int i = 0; i < number;i++) {
                    final File f = new File(context.getExternalFilesDir("received"),
                            files.get(i).getName());


                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    //create a new fileoutputstream for each new file
                    FileOutputStream fos = new FileOutputStream(f);
                    //read file
                    long fileSize = fileSizeArr.get(i);
                    while (fileSize > 0 && (n = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                        fos.write(buf, 0, n);
                        fileSize -= n;
                    }

                    fos.close();
                }
                }

            } catch (IOException | JSONException | SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            }



        @Override
        protected String doInBackground(Void... params) {
            try {

                server2(8988);

            } catch (IOException e) {
                //Log.e(WiFiDirectActivity.TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context,"Received",Toast.LENGTH_SHORT).show();
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {

        }

        public static boolean copyFile(InputStream inputStream, OutputStream out) {
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
    }

    class ClientAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = new Socket();

            try {
                socket.setReuseAddress(true);


                socket.connect((new InetSocketAddress(info.groupOwnerAddress.getHostName(), 8000)), 50000);
                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(new String("BROFIST"));
                oos.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                new ClientAsyncTask().execute();
            }
            return null;
        }
    }

    class ClientAsynktask2 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8000);
                serverSocket.setReuseAddress(true);
                Socket client = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Object object = objectInputStream.readObject();
                if (object.getClass().equals(String.class) && ((String) object).equals("BROFIST")) {
                    //Log.d(TAG, "Client IP address: "+client.getInetAddress());
                    return client.getInetAddress().toString();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                clientIp = s;
                Toast.makeText(getContext(), "Client ip:" + s, Toast.LENGTH_SHORT).show();

            }
        }
    }


}
