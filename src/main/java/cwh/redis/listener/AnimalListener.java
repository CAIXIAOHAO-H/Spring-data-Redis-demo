package cwh.redis.listener;


import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.Date;

public class AnimalListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        Date date = new Date();
        System.out.println(date.toString()+":this is animal listener,msg:"+message.toString());
    }

}
