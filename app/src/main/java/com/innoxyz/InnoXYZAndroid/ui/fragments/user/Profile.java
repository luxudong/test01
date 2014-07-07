package com.innoxyz.InnoXYZAndroid.ui.fragments.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.Education;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.Experience;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.Paper;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.UserDetail;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.UserInfo;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.UserProfile;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.customviews.SimpleTable;
import com.innoxyz.InnoXYZAndroid.ui.customviews.SimpleTableHeaderFactory;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class Profile extends BaseFragment {

    SimpleObservedData<UserInfo> info = new SimpleObservedData<UserInfo>(new UserInfo());
    public NetworkImageView photo;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.

        //getActivity().getActionBar().setTitle(R.string.title_profile);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_profile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_task_actions,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] items = new String[] { "选择本地图片", "拍照上传" };
        if(item.getItemId() == R.id.action__newtask) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("设置头像")
                    .setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intentFromGallery = new Intent();
                                    intentFromGallery.setType("image/*"); // 设置文件类型
                                    intentFromGallery
                                            .setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intentFromGallery,
                                            IMAGE_REQUEST_CODE);
                                    break;
                                case 1:

                                    Intent intentFromCapture = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    // 判断存储卡是否可以用，可用进行存储
                                    if (Tools.hasSdcard()) {

                                        intentFromCapture.putExtra(
                                                MediaStore.EXTRA_OUTPUT,
                                                Uri.fromFile(new File(Environment
                                                        .getExternalStorageDirectory(),
                                                        "photo.jpg"
                                                )));
                                    }

                                    startActivityForResult(intentFromCapture,
                                            CAMERA_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ret = new LinearLayout(getActivity());
        ret.setOrientation(LinearLayout.VERTICAL);
        final ScrollView scrollView = new ScrollView(getActivity());
        ret.addView(scrollView);
        final SimpleTableHeaderFactory headerFactory = new SimpleTableHeaderFactory(inflater);
        info.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                final LinearLayout inner = new LinearLayout(getActivity());

                LinearLayout top = new LinearLayout(getActivity());
                top.setOrientation(LinearLayout.HORIZONTAL);
                if ( info.getData().detail != null ) {
                    UserDetail detail = info.getData().detail;
                    final HashMap<String, String> map = new HashMap<String, String>();
                    map.put("城市", detail.city);
                    map.put("单位", detail.institution);
                    map.put("部门", detail.department);
                    map.put("职位", detail.title);
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_top, null, false);
                    ((LinearLayout) view.findViewById(R.id.profile_detail)).addView(new SimpleTable(getActivity(), Arrays.asList("城市", "单位", "部门", "职位"), map));
                    //new ProfileImage((ImageView) view.findViewById(R.id.profile_avatar),detail.id).showProfileImage();
                    photo = ((NetworkImageView) view.findViewById(R.id.profile_avatar));
                    photo.setImageUrl(AddressURIs.getUserAvatarURL(detail.id), InnoXYZApp.getApplication().getImageLoader());

                    top.addView(view);
                }
                inner.addView(top);



                UserProfile userProfile = info.getData().profile;
                inner.setOrientation(LinearLayout.VERTICAL);

                if (userProfile.experiences != null) {
                    inner.addView(headerFactory.create("项目"));
                    for (Experience exp : userProfile.experiences) {
                        final HashMap<String, String> map = new HashMap<String, String>();
                        map.put("名称", exp.title);
                        map.put("类别", exp.classes);
                        map.put("简介", exp.summary);
                        map.put("项目周期", "从 " + DateFunctions.RewriteDate(exp.start) + " 到 " + DateFunctions.RewriteDate(exp.end));
                        inner.addView(new SimpleTable(getActivity(), Arrays.asList("名称", "类别", "项目周期", "简介"), map));
                    }
                }
                if (userProfile.educations != null) {
                    inner.addView(headerFactory.create("学历"));
                    for (Education edu : userProfile.educations) {
                        final HashMap<String, String> map = new HashMap<String, String>();
                        map.put("院校", edu.university);
                        map.put("专业", edu.major);
                        map.put("学位", edu.degree);
                        map.put("在校时间", "从 " + DateFunctions.RewriteDate(edu.start) + " 到 " + DateFunctions.RewriteDate(edu.end));
                        inner.addView(new SimpleTable(getActivity(), Arrays.asList("院校", "专业", "学位", "在校时间"), map));
                    }
                }
                if (userProfile.papers != null) {
                    inner.addView(headerFactory.create("论文"));
                    for (Paper paper : userProfile.papers) {
                        final HashMap<String, String> map = new HashMap<String, String>();
                        map.put("名称", paper.title);
                        map.put("关键字", paper.keywords);
                        map.put("发布于", paper.publisher);
                        map.put("发布日期", DateFunctions.RewriteDate(paper.date));
                        map.put("摘要", paper.abstracts);
                        inner.addView(new SimpleTable(getActivity(), Arrays.asList("名称", "关键字", "发布于", "发布日期", "摘要"), map));
                    }
                }
                InnoXYZApp.getApplication().getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.removeAllViews();
                        scrollView.addView(inner);
                    }
                });
            }
        });

        //photo.setOnClickListener(photoClicked);
        new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.USER_PROFILE)
                .setOnResponseListener(new JsonResponseHandler(info, UserInfo.class, Arrays.asList("data")))
                .request();
        return ret;
    }

    private View.OnClickListener photoClicked = new View.OnClickListener() {
        private String[] items = new String[] { "选择本地图片", "拍照" };
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("设置头像")
                    .setCancelable(false)
                    .setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intentFromGallery = new Intent();
                                    intentFromGallery.setType("image/*"); // 设置文件类型
                                    intentFromGallery
                                            .setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intentFromGallery,
                                            IMAGE_REQUEST_CODE);
                                    break;
                                case 1:

                                    Intent intentFromCapture = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    // 判断存储卡是否可以用，可用进行存储
                                    if (Tools.hasSdcard()) {
                                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));
                                    }

                                    startActivityForResult(intentFromCapture,
                                            CAMERA_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != -1){
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                startPhotoZoom(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                if (Tools.hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory()+"/innoxyz/"
                                    + "photo.jpg");
                    startPhotoZoom(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_LONG).show();
                }

                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo1 = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo1);
            photo = new NetworkImageView(getActivity());
            photo.setImageDrawable(drawable);
        }
    }
}

class Tools {
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}

