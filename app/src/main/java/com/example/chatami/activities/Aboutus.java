package com.example.chatami.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.chatami.R;

public class Aboutus extends AppCompatActivity {
ImageView wh,fb,web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

wh=findViewById(R.id.wh);
fb=findViewById(R.id.fa);
web=findViewById(R.id.si);

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkpp="";
                String Package="";
                String weblink="https://github.com/Chaimaebencheikhe";
                openLink(linkpp,Package,weblink);


            }

            private void openLink(String linkpp, String aPackage, String weblink) {
                try {
                    Uri uri = Uri.parse(linkpp);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setPackage(aPackage);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException activityNotFoundException) {
                    Uri uri = Uri.parse(weblink);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    fb.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  String linkpp = "";
                                                  String Package = "";
                                                  String weblink = "https://www.linkedin.com/in/chaimae-ben-cheikhe/";
                                                  openLink(linkpp, Package, weblink);


                                              }

                                              private void openLink(String linkpp, String aPackage, String weblink) {
                                                  try {
                                                      Uri uri = Uri.parse(linkpp);
                                                      Intent intent = new Intent(Intent.ACTION_VIEW);
                                                      intent.setData(uri);
                                                      intent.setPackage(weblink);
                                                      intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                                      startActivity(intent);
                                                  } catch (ActivityNotFoundException activityNotFoundException) {
                                                      Uri uri = Uri.parse(weblink);
                                                      Intent intent = new Intent(Intent.ACTION_VIEW);
                                                      intent.setData(uri);
                                                      intent.setData(uri);
                                                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                      startActivity(intent);
                                                  }
                                              }
                                          });

                    wh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String linkpp = "";
                            String Package = "";
                            String weblink = "https://classroom.google.com/c/NzIzNjUyMDExOTg5/m/NzIzNjU1MDM4OTY0/details";
                            openLink(linkpp, Package, weblink);


                        }

                        private void openLink(String linkpp, String aPackage, String weblink) {
                            try {
                                Uri uri = Uri.parse(linkpp);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                intent.setPackage(weblink);
                                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException activityNotFoundException) {
                                Uri uri = Uri.parse(weblink);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        };
                    });
                }
            }});
    }
}
