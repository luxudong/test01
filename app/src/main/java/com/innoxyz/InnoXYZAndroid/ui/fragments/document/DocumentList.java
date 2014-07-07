package com.innoxyz.InnoXYZAndroid.ui.fragments.document;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.document.Document;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.document.Documents;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;
import com.innoxyz.InnoXYZAndroid.ui.utils.FilesizeFunctions;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午12:26
 * To change this template use File | Settings | File Templates.
 */
public class DocumentList extends BaseFragment {

    protected SimpleObservedData<Documents> documentList, load;
    AutoloadListView autolv;
    String entryId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        documentList = new SimpleObservedData<Documents>(new Documents());
        load = new SimpleObservedData<Documents>(new Documents());
        entryId= getArguments().getString("entryId");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle("文档列表");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        ListView ret = new ListView(getActivity());

        ret.setDivider(null);
        autolv = new AutoloadListView(ret);
        load.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                documentList.getData().append(load.getData());
                documentList.notifyObservers();
                autolv.endLoad(documentList.getData().hasMore());
            }
        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {

//            @Override
//            public void LoadData(int page) {
//                autolv.beginLoad();
//                final String sPage = "" + page;
//                final Runnable runnable = new Runnable() {
//
//                    @Override
//                    public void run() {
//                        new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_DOCUMENT_IN_PROJECT)
//                                .addParameter("entryId", "" + getArguments().getString("entryId"))
//                                .addParameter("page", sPage)
//                                .setOnResponseListener(new JsonResponseHandler(load, Documents.class, Arrays.asList("data")))
//                                .request();
//                    }
//                };
//                if ( getArguments().getString("entryId")==null ) {
//                    new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.GET_BASEDIR_OF_PROJECT)
//                            .addParameter("thingId", "" + getArguments().getInt("projectId"))
//                            .setOnResponseListener(new TextResponseHandler() {
//                                @Override
//                                public boolean OnResponseContent(int responseCode, String content) throws Exception {
//                                    String entryId = new JSONObject(content).getString("data");
//                                    getArguments().putString("entryId", entryId);
//                                    Thread t = new Thread(runnable);
//                                    t.start();
//                                    return true;
//                                }
//                            }).request();
//                } else {
//                    runnable.run();
//                }
//            }



            public void LoadData(int page) {

                        new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_DOCUMENT_IN_PROJECT)
                                .addParameter("entryId", "" + entryId)
                                .addParameter("page", page+"")
                                .addParameter("pageSize","10")
                                .addParameter("type","LPROJECT")
                                .setOnResponseListener(new JsonResponseHandler(load, Documents.class, Arrays.asList("data")))
                                .request();

            }
        });
        ret.setAdapter(new DocumentListViewAdapter(getActivity()));
        autolv.TriggerLoading();
        return ret;
    }

    private class DocumentListViewAdapter extends CommonListViewAdapter {

        public DocumentListViewAdapter(Context context) {
            super(context);
            documentList.registerObserver(this);
        }

        protected View.OnClickListener dirItemClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document document = (Document)v.getTag(R.id.item_object);
//                Bundle bundle = new Bundle();
//                bundle.putString("type",document.type);
//                bundle.putString("i", document.id);
//                bundle.putString("disName",document.dispName);
//                bundle.putInt("creatorId",document.creatorId);
                entryId = document.id;
                documentList.setData(new Documents(),false);
                autolv.reLoading();
            }
        };

        @Override
        protected void updateData() {
            Document[] documents = documentList.getData().documents;
            views = new View[documents.length];
            for(int i=0; i<views.length; i++) {
                if ( documents[i].type.compareToIgnoreCase("file")==0 ) {
                    views[i] = mInflator.inflate(R.layout.listitem_document_file, null);
                    ((TextView)views[i].findViewById(R.id.document_item_name)).setText(documents[i].dispName);
                    ((TextView)views[i].findViewById(R.id.document_item_size)).setText(FilesizeFunctions.dispSize(documents[i].length));
                    ((TextView)views[i].findViewById(R.id.document_item_creator)).setText(
                            getResources().getString(R.string.template_documentitem_create)
                            .replace("%time%", DateFunctions.RewriteDate(documents[i].createTime, "yyyy-M-d", "unknown"))
                            .replace("%user%", documents[i].creatorName)
                    );
                } else {
                    views[i] = mInflator.inflate(R.layout.listitem_document_dir, null);
                    ((TextView)views[i].findViewById(R.id.document_item_dirname)).setText(documents[i].dispName);
                    Log.e("filetype", documents[i].type);
                    views[i].setOnClickListener(dirItemClicked);
                }
                views[i].setTag(R.id.item_object, documents[i]);
            }
        }
    }
}
