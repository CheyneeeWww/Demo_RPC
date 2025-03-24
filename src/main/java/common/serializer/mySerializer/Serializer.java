package common.serializer.mySerializer;

/**
 * @Author cnwang
 * @Date created in 0:25 2025/3/25
 */
public interface Serializer {
    byte[] serialize(Object obj);
    Object deserializer(byte[] bytes,int messageType);
    int getType();
    static Serializer getSerializerByCode(int code){
        switch(code){
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
