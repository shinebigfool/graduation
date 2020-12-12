package com.example.graduate.bean;


import org.springframework.core.io.AbstractResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class InMemoryResource extends AbstractResource {
    /**
     * 资源描述
     */
    private static final String DESCRIPTION = "InMemoryResource";

    /**
     * 脚本来源
     */
    private final byte[] source;

    /**
     * @param sourceString 构造函数，接收xml文本
     */
    public InMemoryResource(String sourceString) {
        this.source = sourceString.getBytes();
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(source);
    }



    /**
     * @see org.springframework.core.io.AbstractResource#hashCode()
     */
    public int hashCode() {
        return Arrays.hashCode(source);
    }

    /**
     * @see org.springframework.core.io.AbstractResource#equals(Object)
     */
    public boolean equals(Object res) {
        if (!(res instanceof InMemoryResource)) {
            return false;
        }

        return Arrays.equals(source, ((InMemoryResource) res).source);
    }
}
