package org.example.common.serializer.mySerializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.example.common.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * @Author cnwang
 * @Date created in 13:58 2025/3/26
 */
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        // 使用 ByteArrayOutputStream 和 HessianOutput 来实现对象的序列化
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);  // 将对象写入输出流
            return byteArrayOutputStream.toByteArray();  // 返回字节数组
        } catch (IOException e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        // 使用 ByteArrayInputStream 和 HessianInput 来实现反序列化
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();  // 读取并返回对象
        } catch (IOException e) {
            throw new SerializeException("Deserialization failed");
        }
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String toString() {
        return "Hessian";
    }
}
