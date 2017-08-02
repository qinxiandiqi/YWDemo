package com.qinxiandiqi.ywdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.mobileim.IYWP2PPushListener;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;

import java.util.List;

/**
 * Created by Jianan on 2017/8/2.
 */
public class MessageActivity extends AppCompatActivity {

    private EditText etToUser, etMessage;
    private Button btnStart, btnSent;
    private TextView tvLog;

    private StringBuilder sb;
    private String talkTo;
    private YWConversation conversation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        etToUser = (EditText)findViewById(R.id.et_to_user);
        etMessage = (EditText) findViewById(R.id.et_message);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnSent = (Button) findViewById(R.id.btn_sent);
        tvLog = (TextView) findViewById(R.id.tv_log);

        sb = new StringBuilder();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkTo = etToUser.getText().toString();
                conversation = IM.getIM().getConversation(talkTo);
            }
        });
        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String toUser = etToUser.getText().toString();
                final String msg = etMessage.getText().toString();
                if (!TextUtils.isEmpty(toUser) && !TextUtils.isEmpty(msg)) {
                    IM.getIM().sentText(msg, toUser, new IWxCallback() {
                        @Override
                        public void onSuccess(Object... objects) {
                            sb.append(msg).append("\nsent success\n");
                            tvLog.setText(sb.toString());
                        }

                        @Override
                        public void onError(int i, String s) {
                            sb.append(msg).append("\nsent fail:").append(i).append(";").append(s).append("\n");
                            tvLog.setText(sb.toString());
                        }

                        @Override
                        public void onProgress(int i) {

                        }
                    });
                }
            }
        });

        IM.getIM().getIMCore().getConversationService().addP2PPushListener(new IYWP2PPushListener() {
            @Override
            public void onPushMessage(IYWContact iywContact, List<YWMessage> list) {
                sb.append("receive msg:").append(iywContact.getShowName()).append(":");
                for (YWMessage message : list) {
                    sb.append(message.getContent());
                }
                tvLog.setText(sb.toString());
            }
        });
    }
}
