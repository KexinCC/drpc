package org.xiaoheshan.compress;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompressWrapper {
    private byte compressCode;
    private String name;
    private Compressor compressor;
}
