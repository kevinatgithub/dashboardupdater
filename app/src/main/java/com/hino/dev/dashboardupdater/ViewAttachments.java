package com.hino.dev.dashboardupdater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewAttachments extends AppCompatActivity {

    private ListView lv_attachments;
    private WebView wv_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachments);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        lv_attachments = findViewById(R.id.lv_attachments);
        wv_preview = findViewById(R.id.wv_preview);

        ArrayList<WipChassisNumber.Attachment> attachments = new ArrayList<WipChassisNumber.Attachment>();
        attachments.add(new WipChassisNumber.Attachment("File 1","http://www.africau.edu/images/default/sample.pdf"));
        attachments.add(new WipChassisNumber.Attachment("File 2","http://www.africau.edu/images/default/sample.pdf"));
        attachments.add(new WipChassisNumber.Attachment("File 3","http://www.africau.edu/images/default/sample.pdf"));
        attachments.add(new WipChassisNumber.Attachment("File 4","http://www.africau.edu/images/default/sample.pdf"));
        attachments.add(new WipChassisNumber.Attachment("File 5","http://www.africau.edu/images/default/sample.pdf"));

        AttachmentListAdapter adapter = new AttachmentListAdapter(this,attachments);
        lv_attachments.setAdapter(adapter);
        lv_attachments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WipChassisNumber.Attachment attachment = (WipChassisNumber.Attachment) lv_attachments.getItemAtPosition(i);
                wv_preview.getSettings().setJavaScriptEnabled(true);
                wv_preview.loadUrl("http://docs.google.com/gview?embedded=true&url=" +attachment.url);
//                wv_preview.loadUrl(attachment.url);
//                wv_preview.setVisibility(View.VISIBLE);
//                lv_attachments.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public class AttachmentListAdapter extends ArrayAdapter<WipChassisNumber.Attachment>{

        public AttachmentListAdapter(@NonNull Context context, ArrayList<WipChassisNumber.Attachment> attachments) {
            super(context, 0, attachments);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            WipChassisNumber.Attachment attachment = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.attachment_list_row, parent, false);
            }

            TextView txt_attachment_name = convertView.findViewById(R.id.txt_attachment_name);
            ImageView img_pdf= convertView.findViewById(R.id.img_pdf);

            txt_attachment_name.setText(attachment.name);
            img_pdf.setImageResource(R.drawable.ic_attachment);

            return convertView;
        }
    }

}
