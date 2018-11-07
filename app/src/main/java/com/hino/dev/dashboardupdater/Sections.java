package com.hino.dev.dashboardupdater;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class Sections extends AppCompatActivity {

    private Session session;
    private User user;
    private ListView lv_sections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        session = new Session(this);
        session.removeSection();
        user = session.getUser();
        lv_sections = findViewById(R.id.lv_sections);

        ArrayList<User.Section> sections = new ArrayList<User.Section>(Arrays.asList(user.sections));

        SectionListAddapter adapter = new SectionListAddapter(this,sections);
        lv_sections.setAdapter(adapter);

        lv_sections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User.Section section = (User.Section) lv_sections.getItemAtPosition(i);
                session.setSection(section);
                Intent intent = new Intent(getApplicationContext(),MOList.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class SectionListAddapter extends ArrayAdapter<User.Section>{

        public SectionListAddapter(@NonNull Context context, ArrayList<User.Section> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User.Section section = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sections_list_row, parent, false);
            }

            TextView lbl_section = convertView.findViewById(R.id.lbl_section);
            lbl_section.setText(section.name);
            return convertView;
        }
    }
}
