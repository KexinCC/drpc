package org.xiaoheshan.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerializerWrapper {
    private byte serializeCode;
    private String name;
    private Serializer serializer;
}