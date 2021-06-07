package com.ibatis.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author rkc
 * @date 2021/3/18 20:46
 */
public class IgnoreDTDEntityResolver implements EntityResolver {
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8' ?>".getBytes(StandardCharsets.UTF_8)));
    }
}
