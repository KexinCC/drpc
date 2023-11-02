package org.xiaoheshan.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SerializerWrapper {
    private byte serializeType;
    private String name;
    private Serializer serializer;
}
