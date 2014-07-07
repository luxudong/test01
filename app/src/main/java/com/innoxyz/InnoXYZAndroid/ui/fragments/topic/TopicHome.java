package com.innoxyz.InnoXYZAndroid.ui.fragments.topic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.ParcelableUser;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Attachment;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.project.MembersOfProj;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.topic.TopicReply;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.topic.TopicSpec;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.commands.AddInvolvedUserDialog;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.customviews.InnerListView;
import com.innoxyz.InnoXYZAndroid.ui.customviews.PullToRefreshListView;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.fragments.project.ProjectHome;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;
import com.innoxyz.InnoXYZAndroid.ui.utils.OpenFileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.view.View.OnClickListener;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午5:45
 * To change this template use File | Settings | File Templates.
 */
public class TopicHome extends BaseFragment {

    SimpleObservedData<TopicSpec> topic, loadedTopic;
    ImageView sendButton;
    EditText editText;
    View bottomButton,input;
    private int lastHeight = 0;
    AutoloadListView autolv;

    private int projectId;
    //String[] attachmentName;
    HashMap<Integer,Attachment[]> attachAtPosition = new HashMap<Integer, Attachment[]>();
    static HashMap<String,Integer> attachImage = new HashMap<String, Integer>();
    int innerItemPostion;
    //ProgressBar progressBar;
    int fileLength;
    //private List<Attachment> attachmentList= new ArrayList<Attachment>();

    URLConnection connection;
    InputStream inputStream;
    OutputStream outputStream;

    SimpleObservedData<MembersOfProj> membersOfProj;
    List<ParcelableUser> memberList = new ArrayList<ParcelableUser>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        projectId = getArguments().getInt("projectId");
        topic = new SimpleObservedData<TopicSpec>(new TopicSpec());
        loadedTopic = new SimpleObservedData<TopicSpec>(new TopicSpec());

        //getActivity().getActionBar().setTitle(R.string.title_topic_home);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        attachImage = new FileWithImage().init();


        ArrayList<ParcelableUser> parcelableUser = getArguments().getParcelableArrayList("memberList");
        if(parcelableUser != null){
            memberList = (List<ParcelableUser>)parcelableUser.get(0);
        }
        else{
            membersOfProj = new SimpleObservedData<MembersOfProj>(new MembersOfProj());

            membersOfProj.registerObserver(new IDataObserver() {
                @Override
                public void update(Object o) {
                    //memberList = Arrays.asList(membersOfProj.getData().members);
                    for(int i=0;i<membersOfProj.getData().members.length;i++){
                        ParcelableUser parcelableUser = new ParcelableUser();
                        parcelableUser.setId(membersOfProj.getData().members[i].user.id);
                        parcelableUser.setName(membersOfProj.getData().members[i].user.realname);
                        memberList.add(i,parcelableUser);
                    }
//                for(int i=0;i<memberList.size();i++){
//                    Log.e("aaaaaa",memberList.get(i).user.id+" "+memberList.get(i).user.realname);
//                }
                }
            });

            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.GET_MEMBER_OF_PROJECT)
                    .addParameter("thingId", "" + projectId)
                    .addParameter("pageSize", "100")
                    .setOnResponseListener(new JsonResponseHandler(membersOfProj, MembersOfProj.class, null))
                    .request();
        }


    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_topic_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_topic_home, container, false);

        setHasOptionsMenu(true);
        if(attachImage.isEmpty()) {                             //如果为空，初始化。静态数据
            attachImage = new FileWithImage().init();
        }


        ((TextView)ret.findViewById(R.id.topichome_projectname)).setText(getResources().getString(R.string.template_topichome_projectname).replace("%name%", getArguments().getString("projectName")));

        sendButton = (ImageView)ret.findViewById(R.id.topichome_bottom_send);
        bottomButton = ret.findViewById(R.id.topichome_bottom_button);
        input = ret.findViewById(R.id.topichome_bottom_input);
        editText = (EditText)ret.findViewById(R.id.topichome_bottom_edittext);

        ret.findViewById(R.id.topichome_reply).setOnClickListener(onReplyClicked);
        ret.findViewById(R.id.topichome_goto_project).setOnClickListener(onGoToProjectClicked);
        sendButton.setOnClickListener(onSendClicked);

        ((EditText)ret.findViewById(R.id.topichome_bottom_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0)
                    TopicHome.this.sendButton.setImageResource(R.drawable.send);
                else
                    TopicHome.this.sendButton.setImageResource(R.drawable.send_black);
            }
        });

        //handle the software keyboard hide event
        final View rootView = getActivity().findViewById(R.id.rootView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = rootView.getHeight();
                //int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                if(lastHeight != 0 && height - lastHeight > 100){
                    bottomButton.setVisibility(View.VISIBLE);
                    input.setVisibility(View.GONE);
                }
                lastHeight = height;
            }
        });

        final PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) ret.findViewById(R.id.topichome_itemlist);
        autolv = new AutoloadListView(pullToRefreshListView);
        loadedTopic.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                topic.getData().topic = loadedTopic.getData().topic;
                topic.getData().pager.append(loadedTopic.getData().pager);
                topic.notifyObservers();
                autolv.endLoad(topic.getData().pager.hasMore());
            }

        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {
            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.TOPIC_DETAIL)
                        .addParameter("topicId", getArguments().getString("topicId"))
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedTopic, TopicSpec.class, null))
                        .request();
            }
        });
        topic.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
            }
        });
        pullToRefreshListView.setLockScrollWhileRefreshing(false);
        pullToRefreshListView.setShowLastUpdatedText(true);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //clean cache before refresh
                topic.setData(new TopicSpec(), false);
                autolv.reLoading();
            }
        });
        pullToRefreshListView.setAdapter(new TopicRepliesListViewAdapter(getActivity()));
        autolv.TriggerLoading();

        //Log.e("","topic home oncreatView called............");

        return ret;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.topichome_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_topichome_adduser){

            if(topic.getData().topic.creatorId == InnoXYZApp.getApplication().getCurrentUserId()){
                //alertdialog
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString(AddInvolvedUserDialog.TYPE_NAME, AddInvolvedUserDialog.TYPE_TOPIC);
                new AddInvolvedUserDialog(getActivity(), bundle)
                        .setItemsAndIds(memberList)
                        .show();
            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.topic_modify_involvers))
                        .setPositiveButton(getString(R.string.OK), null)
                        .show();
            }


        }

        return false;
    }

    private OnClickListener onReplyClicked = new OnClickListener() {
        @Override
        public void onClick(View view) {
            bottomButton.setVisibility(View.GONE);
            input.setVisibility(View.VISIBLE);
            editText.requestFocus();
            editText.setText("");
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText,0);
        }
    };

    private OnClickListener onSendClicked = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //Log.e("editText", "" + editText.getText().toString());

            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.TOPIC_REPLY)
                    .addParameter("topicId", getArguments().getString("topicId"))
                    .addParameter("content", editText.getText().toString())
                    .addParameter("attachments", "[]")
                    .setOnResponseListener(new PostResponseHandler(new TopicHomeSendReplyListener()))
                    .request();
        }
    };



    protected OnClickListener onGoToProjectClicked = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            if(memberList != null){
                ArrayList list = new ArrayList();
                list.add(memberList);
                bundle.putParcelableArrayList("memberList",list);
            }
            new FragmentCommand(TopicHome.class, ProjectHome.class, TopicHome.this.getActivity(), bundle, null).Execute();
        }
    };

    private class TopicHomeSendReplyListener implements OnPostListener {

        public void onPostSuccess(){

            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            //clean cache before refresh
            topic.setData(new TopicSpec(), false);
            TopicHome.this.autolv.reLoading();
        }

        public void onPostFail(){

        }
    }



//    /* to be finished */
//    private class TopicHomeSendReplyHandler extends TextResponseHandler {
//
//        final static String RESULT = "action_returns";
//        final static String ERROR = "action_errors";
//        final static String SUCCESS = "success";
//
//        @Override
//        public boolean OnResponseContent(int responseCode, String content) throws Exception{
//            Log.e("Request", content);
//            JSONObject json = new JSONObject(content);
//            if ( json.getString(RESULT).compareTo(SUCCESS)!=0 ) {
//                String err_msg = "";
//                if ( json.has(ERROR) ) {
//                    JSONArray err_arr = json.getJSONArray(ERROR);
//                    for (int i=0; i< err_arr.length(); i++) {
//                        err_msg += err_arr.getString(i) + "\n";
//                    }
//                }
//                throw new ActionFailedException(err_msg);
//            }
//
///*            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    bottomButton.setVisibility(View.VISIBLE);
//                    input.setVisibility(View.GONE);
//                }
//            });*/
//            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//
//            TopicHome.this.autolv.reLoading();
//
//
//            return true;
//        }
//
//    }




    static class ViewHolder{
        TextView topichome_item_username,topichome_item_content,topichome_item_date;
        ImageButton topichome_item_downloadButton;
        Spinner topichome_attach_spinner;
        NetworkImageView topichome_item_avatar;
        ProgressBar download_progressbar;
        TextView download_progress_text;
        LinearLayout attachment_view;
        InnerListView attachListView;


    }

    private class TopicRepliesListViewAdapter extends CommonListViewAdapter {

        public TopicRepliesListViewAdapter(Context context) {
            super(context);
            topic.registerObserver(this);
        }

        @Override
        public int getCount() {
            if(topic == null || topic.getData() == null || topic.getData().pager == null || topic.getData().pager.replies == null)
                return 0;
            else
                return 1 + topic.getData().pager.replies.length;  //To change body of implemented methods use File | Settings | File Templates.
        }


        class AttachListViewAdapter extends BaseAdapter {
            private Context context;
            private List<Attachment> data;
            private LayoutInflater layoutInflater;
            private int count;


            final class Zujian {
                TextView textView;
                Button downloadbutton;
                TextView fileName;
                ImageView fileImage;
                ProgressBar progressBar;
            }

            private AttachListViewAdapter(int count,Context context,List<Attachment> data) {
                this.count = count;
                this.context = context;
                this.data = data;
                this.layoutInflater = LayoutInflater.from(context);
            }

            @Override
            public int getCount() {
                //return spinnerAtPosition.get(count).length;
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            public void showPdf(String path)
            {
                File file = new File(path);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/pdf");
                startActivity(intent);

            }


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                //Log.e("","inner listview getview called!!");

                Zujian zujian = null;
                if(convertView == null) {
                    zujian = new Zujian();
                    convertView = layoutInflater.inflate(R.layout.listitem_attachments_download,null);
                    zujian.textView = (TextView)convertView.findViewById(R.id.attach_progress_text);
                    zujian.downloadbutton = (Button)convertView.findViewById(R.id.attachment_down_imagebtn);
                    zujian.fileImage = (ImageView)convertView.findViewById(R.id.attach_file_image);
                    zujian.fileName = (TextView)convertView.findViewById(R.id.attachment_listitem_filename);
                    zujian.progressBar = (ProgressBar)convertView.findViewById(R.id.attach_progressbar);
                    convertView.setTag(zujian);
                }else {
                    zujian = (Zujian)convertView.getTag();
                }
                zujian.textView.setText("");
                zujian.fileName.setText(data.get(position).name);

                String filePath= Environment.getExternalStorageDirectory()+"/innoxyzDownload/"+data.get(position).name;
                //Log.e("",filePath);
                File file =new File(filePath);
                if(file.exists()) {
                    zujian.downloadbutton.setText("打开文件");

                }

                String fileSort = data.get(position).name.substring(data.get(position).name.lastIndexOf("."));
                if(!attachImage.containsKey(fileSort)) {
                    zujian.fileImage.setImageDrawable(getResources().getDrawable(R.drawable.other));
                }else {
                    zujian.fileImage.setImageDrawable(getResources().getDrawable(attachImage.get(fileSort)));
                }
                final Zujian finalZujian = zujian;

                zujian.downloadbutton.setOnClickListener(new OnClickListener() {
                    @Override
                    public  void onClick(View view) {
                        String fileURI= Environment.getExternalStorageDirectory()+"/innoxyzDownload/"+data.get(position).name;

                        File file =new File(fileURI);

                        if(!file.exists()) {

                            finalZujian.progressBar.setVisibility(View.VISIBLE);
                            //holder.topichome_item_downloadButton.setText("下载完成！");
                            String attachId;
                            attachId = data.get(position).id;


                            final String attachName;
                            attachName = data.get(position).name;


                            final String attachPath = "http://www.innoxyz.com/file/file.action?"+ "type=document&download=true&id=" + attachId;
                            Log.e("", attachPath);
                            Log.e("", attachName);






                            final Handler handler=new Handler() {
                                public void handleMessage(Message msg)
                                {

                                    switch (msg.what) {
                                        case 0:

                                            finalZujian.progressBar.setMax((Integer) msg.obj);
                                            //mypDialog.setMessage("");
                                            Log.i("文件长度----------->", finalZujian.progressBar.getMax() + "");
                                            break;
                                        case 1:
                                            finalZujian.progressBar.setProgress((Integer)msg.obj);
                                            int percent = (Integer)msg.obj*100/fileLength;
                                           finalZujian.textView.setText("已下载：" + (Integer) msg.obj + "  共：" + fileLength + "  已完成:" + percent + "%");
                                            //textView.setText(x+"%");
                                            break;
                                        case 2:
                                            Toast.makeText(InnoXYZApp.getApplication().getApplicationContext(), "已下载到" + (String) msg.obj, Toast.LENGTH_LONG).show();
                                            finalZujian.textView.setVisibility(View.INVISIBLE);
                                            finalZujian.progressBar.setVisibility(View.INVISIBLE);

                                            finalZujian.downloadbutton.setText("打开文件");

                                            break;

                                        default:
                                            break;
                                    }
                                }


                            };




                            Thread thread=new Thread(){
                                public void run(){

                                    try {
                                        URL url=new URL(attachPath);
                                        connection=url.openConnection();
                                        if(connection != null) {
                                            Log.e("", "建立网络连接");
                                        }
                                        if (connection.getReadTimeout()==10) {
                                            Log.i("---------->", "当前网络有问题");
                                            Toast.makeText(InnoXYZApp.getApplication().getApplicationContext(), "请检查您的网络", Toast.LENGTH_LONG);

                                        }
                                        inputStream=connection.getInputStream();

                                    } catch (MalformedURLException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

             /*
       * 文件的保存路径和和文件名其中Nobody.mp3是在手机SD卡上要保存的路径，如果不存在则新建
       */
                                    String savePAth= Environment.getExternalStorageDirectory()+"/innoxyzDownload";
                                    Log.e("", savePAth);
                                    File file1=new File(savePAth);
                                    if (!file1.exists()) {
                                        file1.mkdir();
                                    }else {
                                        Log.e("", "文件夹存在!!");
                                    }
                                    String savePathString= Environment.getExternalStorageDirectory()+"/innoxyzDownload/"+attachName;
                                    Log.e("", savePathString);
                                    File file =new File(savePathString);

                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }


                                    Message message=new Message();
                                    try {
                                        outputStream=new FileOutputStream(file);
                                        byte [] buffer=new byte[1024*4];
                                        fileLength = connection.getContentLength();
                                        Log.e("FileLength", "length" + fileLength);
                                        message.what=0;
                                        message.obj = connection.getContentLength();
                                        handler.sendMessage(message);
                                        int downedFileLength = 0;
                                        int len = 0;
                                        while ((len = inputStream.read(buffer)) != -1) {

                                            outputStream.write(buffer,0,len);





                                            downedFileLength  = downedFileLength + len;
                                            Log.e("-------->", downedFileLength + "");
                                            Message message1=new Message();
                                            message1.what=1;
                                            message1.obj = downedFileLength;
                                            handler.sendMessage(message1);
                                        }

                                        outputStream.close();
                                        inputStream.close();

                                        Message message2=new Message();
                                        message2.what=2;
                                        message2.obj = savePathString;
                                        handler.sendMessage(message2);
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }


                                }

                            };
                            thread.start();
                        }else {
                            String sort = data.get(position).name.substring(data.get(position).name.lastIndexOf("."));
                            Log.e("sort", sort + ".....................................");
                            Intent intent = null;
                            if(sort.equals(".pdf")) {
                                intent = OpenFileUtils.getPdfFileIntent(fileURI);
                            }else if(".doc".equals(sort) ||  ".docx".equals(sort)) {
                                intent =OpenFileUtils.getWordFileIntent(fileURI);
                            }else if(".ppt".equals(sort) || ".pptx".equals(sort)) {
                                intent =OpenFileUtils.getPptFileIntent(fileURI);
                            }else if(".xls".equals(sort) || ".xlsx".equals(sort)) {
                                intent =OpenFileUtils.getExcelFileIntent(fileURI);
                            }else if(".txt".equals(sort)) {
                                intent =OpenFileUtils.getTextFileIntent(fileURI, true);
                            }else if(".mp3".equals(sort)) {
                                intent =OpenFileUtils.getAudioFileIntent(fileURI);
                            }else if(".mp4".equals(sort) || ".avi".equals(sort)) {
                                intent =OpenFileUtils.getVideoFileIntent(fileURI);
                            }else if(".jpg".equals(sort) || ".png".equals(sort) || ".jpeg".equals(sort) || ".gif".equals(sort) || ".bmp".equals(sort)) {
                                intent =OpenFileUtils.getImageFileIntent(fileURI);
                            }else if(".chm".equals(sort)) {
                                intent =OpenFileUtils.getChmFileIntent(fileURI);
                            }else if(".apk".equals(sort)) {
                                intent =OpenFileUtils.getApkFileIntent(fileURI);
                            }else {
                                Toast.makeText(getActivity(), "不支持的文件格式", Toast.LENGTH_LONG);
                            }
                            if(intent != null) {
                                startActivity(intent);
                            }
                        }
                    }
                });
                return convertView;
            }
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Log.e("", "getViewCalled!!!!!!!!!!!!!!!!!!!!!!");

            final ViewHolder holder;
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_topichome_topic, null);
                holder = new ViewHolder();
                holder.topichome_item_username = (TextView)view.findViewById(R.id.topichome_item_username);
                holder.topichome_item_content = (TextView)view.findViewById(R.id.topichome_item_content);
                //holder.topichome_item_attach = (TextView)view.findViewById(R.id.topichome_item_attach);
                holder.topichome_item_date = (TextView)view.findViewById(R.id.topichome_item_date);
                holder.topichome_item_avatar = (NetworkImageView)view.findViewById(R.id.topichome_item_avatar);
                holder.topichome_item_downloadButton = (ImageButton)view.findViewById(R.id.topichome_download_button);
                holder.topichome_attach_spinner = (Spinner)view.findViewById(R.id.topichome_spinner);
                holder.download_progressbar = (ProgressBar)view.findViewById(R.id.download_progress);
                holder.download_progress_text = (TextView)view.findViewById(R.id.download_progress_text);
                holder.attachment_view = (LinearLayout)view.findViewById(R.id.attachment_view);
                holder.attachListView = (InnerListView)holder.attachment_view.findViewById(R.id.attachment_listview);
                view.setTag(holder);
            }
            else{
                holder = (ViewHolder) view.getTag();

            }


            String name, content, updateDate;

            int creatorId;
            if ( i==0 ) {

                creatorId = topic.getData().topic.creatorId;
                name = topic.getData().topic.creatorName;
                content = topic.getData().topic.content;
                updateDate = topic.getData().topic.createTime;
                if ( topic.getData().topic.attachments != null ) {       //如果有附件，则显示附件。如果没有则不显示。
                    Attachment[] attach_arr = topic.getData().topic.attachments;

                    attachAtPosition.put(i,attach_arr);
                    //attachmentName = new String[attach_arr.length];
                    for(int j = 0;j < attach_arr.length;j ++) {
                        //attachment += attach.name + " ";
                        attachAtPosition.get(0)[j] = attach_arr[j];


                    }
                    //holder.topichome_item_attach.setText(getResources().getString(R.string.template_topichome_attachments).replace("%attach%", attachment));
                    //holder.topichome_attach_spinner.setVisibility(View.VISIBLE);
                    //holder.topichome_item_downloadButton.setVisibility(View.VISIBLE);
                    //holder.download_progress_text.setVisibility(View.VISIBLE);

                    //holder.topichome_item_downloadButton.setOnClickListener(onDownloadButtonClicked);
                    //holder.topichome_attach_spinner.setAdapter(new SpinnerAdapter(i));

                    holder.attachListView.setAdapter(new AttachListViewAdapter(0,getActivity(), Arrays.asList(attachAtPosition.get(i))));
                    holder.attachment_view.setVisibility(View.VISIBLE);


                }else {
                    holder.topichome_attach_spinner.setVisibility(View.GONE);
                    holder.topichome_item_downloadButton.setVisibility(View.GONE);
                    holder.attachment_view.setVisibility(View.GONE);
                    //holder.download_progress_text.setVisibility(View.GONE);
                    //holder.download_progressbar.setVisibility(View.GONE);
                    //holder.topichome_item_attach.setVisibility(View.GONE);
                }
            } else {
                TopicReply reply = topic.getData().pager.replies[i-1];
                creatorId = reply.creatorId;
                name = reply.creatorName;
                content = reply.content;
                updateDate = reply.createTime;

                if ( topic.getData().pager.replies[i-1].attachments != null ) {
                    Attachment[] attach_arr = topic.getData().pager.replies[i-1].attachments;
                    attachAtPosition.put(i,attach_arr);
                    //attachmentName = new String[attach_arr.length];
                    for(int j = 0;j < attach_arr.length;j ++) {
                        //attachment += attach.name + " ";
                        attachAtPosition.get(i)[j] = attach_arr[j];


                    }
                    //holder.topichome_item_attach.setText(getResources().getString(R.string.template_topichome_attachments).replace("%attach%", attachment));
                    //holder.topichome_attach_spinner.setVisibility(View.VISIBLE);
                    //holder.topichome_item_downloadButton.setVisibility(View.VISIBLE);
                    //holder.download_progress_text.setVisibility(View.VISIBLE);

                    //holder.topichome_item_downloadButton.setOnClickListener(onDownloadButtonClicked);
                    //holder.topichome_attach_spinner.setAdapter(new SpinnerAdapter(i));

                    holder.attachListView.setAdapter(new AttachListViewAdapter(i,getActivity(), Arrays.asList(attachAtPosition.get(i))));
                    holder.attachment_view.setVisibility(View.VISIBLE);

                }else {
                    holder.topichome_attach_spinner.setVisibility(View.GONE);
                    //holder.topichome_item_attach.setVisibility(View.GONE);
                    holder.topichome_item_downloadButton.setVisibility(View.GONE);
                    holder.attachment_view.setVisibility(View.GONE);
                    //holder.download_progress_text.setVisibility(View.GONE);
                    //holder.download_progressbar.setVisibility(View.GONE);
                }
            }
            updateDate = DateFunctions.RewriteDate(updateDate, "yyyy-M-d", "unknown");

            holder.topichome_item_username.setText(getResources().getString(R.string.template_topichome_username).replace("%name%", name));
            holder.topichome_item_content.setText(content);

            holder.topichome_item_date.setText(updateDate);
            holder.topichome_item_avatar.setImageUrl(AddressURIs.getUserAvatarURL(creatorId), InnoXYZApp.getApplication().getImageLoader());

            return view;
        }

        @Override
        protected void updateData() {
//            views = new View[ 1 + topic.getData().pager.replies.length ];
//            for(int i=0; i<views.length; i++) {
//                views[i] = mInflator.inflate(R.layout.listitem_topichome_topic, null);
//                String name, content, updateDate, attachment = "";
//                int creatorId;
//                if ( i==0 ) {
//                    creatorId = topic.getData().topic.creatorId;
//                    name = topic.getData().topic.creatorName;
//                    content = topic.getData().topic.content;
//                    updateDate = topic.getData().topic.createTime;
//                    if ( topic.getData().topic.attachments != null ) {
//                        for(Attachment attach : topic.getData().topic.attachments) {
//                            attachment += attach.name + " ";
//                        }
//                    }
//                } else {
//                    TopicReply reply = topic.getData().pager.replies[i-1];
//                    creatorId = reply.creatorId;
//                    name = reply.creatorName;
//                    content = reply.content;
//                    updateDate = reply.createTime;
//                    if ( topic.getData().pager.replies[i-1].attachments != null ) {
//                        for(Attachment attach : topic.getData().pager.replies[i-1].attachments) {
//                            attachment += attach.name + " ";
//                        }
//                    }
//                }
//                updateDate = DateFunctions.RewriteDate(updateDate, "yyyy-M-d", "unknown");
//                ((TextView)views[i].findViewById(R.id.topichome_item_username)).setText(getResources().getString(R.string.template_topichome_username).replace("%name%", name));
//                ((TextView)views[i].findViewById(R.id.topichome_item_content)).setText(content);
//                ((TextView)views[i].findViewById(R.id.topichome_item_attach)).setText(getResources().getString(R.string.template_topichome_attachments).replace("%attach%", attachment));
//                ((TextView)views[i].findViewById(R.id.topichome_item_date)).setText(updateDate);
//
//                //new ProfileImage((ImageView) views[i].findViewById(R.id.topichome_item_avatar),creatorId).showProfileImage();
//                ((NetworkImageView) views[i].findViewById(R.id.topichome_item_avatar)).setImageUrl(AddressURIs.getUserAvatarURL(creatorId), InnoXYZApp.getApplication().getImageLoader());
//
//            }
        }


    }

}
