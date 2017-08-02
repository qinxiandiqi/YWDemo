package com.qinxiandiqi.ywdemo;

import android.app.Application;
import android.util.Log;

import com.alibaba.mobileim.IYWP2PPushListener;
import com.alibaba.mobileim.IYWTribePushListener;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMCore;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationListener;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationCreater;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.wxlib.util.SysUtil;

import java.util.List;

import static com.alibaba.mobileim.channel.constant.WXConstant.TIMEOUT;

/**
 * Created by Jianan on 2017/8/2.
 */
public class IM {

    private static IM sIM;
    private YWIMCore ywimCore;

    public static IM getIM() {
        if (sIM == null) {
            synchronized (IM.class) {
                if (sIM == null) {
                    sIM = new IM();
                }
            }
        }
        return sIM;
    }

    private IM() {
    }

    public void init(Application application) {
        SysUtil.setApplication(application);
        if (SysUtil.isTCMSServiceProcess(application)) {
            return;
        }
        if (SysUtil.isMainProcess()) {
            YWAPI.init(application, "23015524");
        }
    }

    public YWIMCore getIMCore() {
        return ywimCore;
    }

    public void login(String userID, String password, IWxCallback callback) {
        if (ywimCore == null) {
            ywimCore = YWAPI.createIMCore(userID, "23015524");
        }
        ywimCore.addConnectionListener(new IYWConnectionListener() {
            @Override
            public void onDisconnect(int i, String s) {
                log("onDisconnect:" + i + ";" + s);
            }

            @Override
            public void onReConnecting() {
                log("onReConnecting");
            }

            @Override
            public void onReConnected() {
                log("onReConnected");
            }
        });

        ywimCore.getConversationService().addP2PPushListener(new IYWP2PPushListener() {
            @Override
            public void onPushMessage(IYWContact iywContact, List<YWMessage> list) {
                for (YWMessage message : list) {
                    log("receive p2p msg:" + iywContact.toString() + ":" + message.toString());
                }
            }
        });

        ywimCore.getConversationService().addTribePushListener(new IYWTribePushListener() {
            @Override
            public void onPushMessage(YWTribe ywTribe, List<YWMessage> list) {
                for (YWMessage message : list) {
                    log("receive tribe msg:" + ywTribe.toString() + ":" + message.toString());
                }
            }
        });

        ywimCore.getConversationService().addConversationListener(new IYWConversationListener() {
            @Override
            public void onItemUpdated() {
                log("conversation updated");
            }
        });

        ywimCore.getConversationService().addTotalUnreadChangeListener(new IYWConversationUnreadChangeListener() {
            @Override
            public void onUnreadChange() {
                log("onUnreadChange");
            }
        });

        ywimCore.getLoginService().login(YWLoginParam.createLoginParam(userID, password), callback);
    }

    public void logout(IWxCallback callback) {
        if (ywimCore != null) {
            ywimCore.getLoginService().logout(callback);
        }
    }

    public void sentText(String msg, String toUser, IWxCallback callback) {
        //创建一条文本或者表情消息
        YWMessage message = YWMessageChannel.createTextMessage(msg);
        YWConversation conversation = getConversation(toUser);
        //将消息发送给对方
        conversation.getMessageSender().sendMessage(message, TIMEOUT, callback);
    }

    public YWConversation getConversation(String userID) {
        //创建一个与消息接收者的聊天会话，userId：表示聊天对象id
        final YWConversationCreater conversationCreater = ywimCore.getConversationService().getConversationCreater();
        final IYWContact contact = ywimCore.getContactService().getWXIMContact(userID);
        return conversationCreater.createConversationIfNotExist(contact);
    }

    public void log(String msg) {
        Log.e("YW_DEMO:IM", msg);
    }
}
