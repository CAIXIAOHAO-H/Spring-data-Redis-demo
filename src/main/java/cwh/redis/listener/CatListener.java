package cwh.redis.listener;


import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Date;

public class CatListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        Date date = new Date();
        System.out.println(date.toString()+":this is cat listener,msg:"+message.toString());
    }

}
