package common.serializer.mySerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @Author cnwang
 * @Date created in 0:33 2025/3/25
 */
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        //创建一个内存中的输出流，用于存储序列化后的字节数据
        //ByteArrayOutputStream是一个可变大小的字节数据缓冲区，数据都会写入这个缓冲区中
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            //将对象转换为二进制数据，把对象数据写入字节缓冲区bos
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            //把对象写入输出流中，触发序列化
            oos.writeObject(obj);
            //强制将缓冲区的数据刷新到底层流bos中
            oos.flush();
            //将字节缓冲区的内容转换为字节数组
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public Object deserializer(byte[] bytes, int messageType) {
        Object obj = null;
        //把字节数组包装成一个字节输入流
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            //使用 ObjectInputStream 包装一个 ByteArrayInputStream 对象
            ObjectInputStream ois = new ObjectInputStream(bis);
            //从 ois 中读取序列化的对象，并将其反序列化为 Java 对象
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
