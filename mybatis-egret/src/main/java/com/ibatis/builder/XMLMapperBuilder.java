package com.ibatis.builder;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.mapping.ResultMap;
import com.ibatis.mapping.ResultMapping;
import com.ibatis.mapping.ResultMappingType;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.util.IgnoreDTDEntityResolver;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 解析具体的mapper.xml，比如AccountMapper.xml
 * @author：rkc
 * @date：Created in 2021/6/11 15:21
 * @description：
 */
@SuppressWarnings("all")
public class XMLMapperBuilder extends BaseBuilder {

    private final InputStream mapperInputStream;
    private final String resource;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) {
        super(configuration);
        this.mapperInputStream = inputStream;
        this.resource = resource;
    }

    public void parse() {
        try {
            SAXReader reader = new SAXReader();
            reader.setEntityResolver(new IgnoreDTDEntityResolver());
            Document document = reader.read(mapperInputStream);
            Element root = document.getRootElement();
            String namespace = root.attribute("namespace").getData().toString();
            List<Element> selectTags = root.elements("select");
            List<Element> insertTags = root.elements("insert");
            List<Element> deleteTags = root.elements("delete");
            List<Element> updateTags = root.elements("update");
            List<Element> resultMaps = root.elements("resultMap");

            for (Element resultMapEle : resultMaps) {
                String resultMapId = namespace + "." + resultMapEle.attribute("id").getData().toString();
                scanResultMap(configuration, resultMapEle, resultMapId);
            }

            handleSelect(namespace, selectTags);
            handleInsert(namespace, insertTags);
            handleDelete(namespace, deleteTags);
            handleUpdate(namespace, updateTags);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mapperInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void scanResultMap(Configuration configuration, Element resultMapTag, String resultMapId) throws ClassNotFoundException {
        System.out.println(resultMapTag.getName() + "->" + resultMapId);
        Attribute typeAttr = resultMapTag.attribute("type");
        if (typeAttr == null) {
            typeAttr = resultMapTag.attribute("javaType");
        }
        String type = typeAttr.getData().toString();

        List<ResultMapping> resultMappings = new ArrayList<>();

        ResultMap.Builder builder = new ResultMap.Builder(configuration, resultMapId, Class.forName(type), resultMappings);
        //遍历其所有子节点，如果遇到association和collection标签则视作一个resultMap标签，以特定的形式存储在configuration中
        List<Element> elements = resultMapTag.elements();
        for (Element element : elements) {
            String column = element.attribute("column").getData().toString();
            String property = element.attribute("property").getData().toString();

            ResultMappingType resultMappingType = ResultMappingType.RESULT_MAPPING;

            if ("association".equals(element.getName().toString()) || "collection".equals(element.getName().toString())) {
                String id = resultMapId;
                if ("association".equals(element.getName().toString())) {
                    id += "_association[" + property + "]";
                    resultMappingType = ResultMappingType.ASSOCIATION;
                } else if ("collection".equals(element.getName().toString())) {
                    id += "_collection[" + property + "]";
                    resultMappingType = ResultMappingType.COLLECTION;
                }
                //递归进行解析，每一个<association>标签或者<collection>标签都作为一个特殊的resultMap进行存储
                scanResultMap(configuration, element, id);
            }

            ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, property, column);
            resultMappingBuilder.resultMappingType(resultMappingType);
            Attribute javaType = element.attribute("javaType");
            if (javaType != null) {
                resultMappingBuilder.javaType(Class.forName(javaType.getData().toString()));
            }
            Attribute jdbcType = element.attribute("jdbcType");
            if (jdbcType != null) {
                resultMappingBuilder.jdbcType(jdbcType.getData().toString());
            }
            resultMappings.add(resultMappingBuilder.build());
        }
        //将构造好的resultMap添加到Configuration中
        configuration.addResultMap(builder.build());
    }

    private void handleUpdate(String namespace, List<Element> updateTags) {
        for (Element element : updateTags) {
            String id = element.attribute("id").getData().toString();
            String parameterType = element.attribute("parameterType").getData().toString();
            String sql = element.getData().toString();
            String sourceId = namespace + "." + id;

            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sql, SqlCommandType.UPDATE);
            MappedStatement ms = builder.parameterType(parameterType)
                    .namespace(namespace)
                    .build();
            configuration.getMappedStatements().put(sourceId, ms);
        }
    }

    private void handleDelete(String namespace, List<Element> deleteTags) {
        for (Element element : deleteTags) {
            String id = namespace + "." + element.attribute("id").getData().toString();
            String sql = element.getData().toString();

            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sql, SqlCommandType.DELETE);
            Attribute parameterType = element.attribute("parameterType");
            if (parameterType != null) {
                builder.parameterType(parameterType.getData().toString());
            }
            MappedStatement ms = builder.namespace(namespace).build();
            configuration.getMappedStatements().put(id, ms);
        }
    }

    private void handleInsert(String namespace, List<Element> insertTags) {
        //处理insert标签
        for (Element element : insertTags) {
            String id = element.attribute("id").getData().toString();
            String parameterType = element.attribute("parameterType").getData().toString();
            String sql = element.getData().toString();
            Attribute useGeneratedKeysAttribute = element.attribute("useGeneratedKeys");
            boolean useGeneratedKeys = false;
            if (useGeneratedKeysAttribute != null) {
                useGeneratedKeys = Boolean.parseBoolean(useGeneratedKeysAttribute.getData().toString());
            }
            String sourceId = namespace + "." + id;


            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sql, SqlCommandType.INSERT);
            MappedStatement mappedStatement = builder.parameterType(parameterType)
                    .useGeneratedKeys(useGeneratedKeys)
                    .namespace(namespace).build();
            configuration.getMappedStatements().put(sourceId, mappedStatement);
        }
    }

    private void handleSelect(String namespace, List<Element> selectTags) throws ClassNotFoundException {
        //处理select标签
        for (Element element : selectTags) {
            String id = element.attribute("id").getData().toString();
            Attribute resultTypeAttr = element.attribute("resultType");
            String sql = element.getData().toString();
            String sourceId = namespace + "." + id;

            MappedStatement.Builder mapperStatementBuilder = new MappedStatement.Builder(configuration, id, sql, SqlCommandType.SELECT);

            String resultType = null;
            //如果标注了resultType则将其转化为ResultMap
            if (resultTypeAttr != null) {
                resultType = resultTypeAttr.getData().toString();
                //需要将resultType指定的Java类转化为ResultMap存储
                List<ResultMapping> resultMappings = new ArrayList<>();
                Class<?> clazz = Class.forName(resultType);
                ResultMap.Builder builder = new ResultMap.Builder(configuration, sourceId, clazz, resultMappings);
                //遍历所有属性
                for (Field field : clazz.getDeclaredFields()) {
                    ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, field.getName(), field.getName());
                    resultMappingBuilder.javaType(field.getType());
                    resultMappings.add(resultMappingBuilder.build());
                }
                ResultMap resultMap = builder.build();
                configuration.addResultMap(resultMap);
                mapperStatementBuilder.resultMap(resultMap);
            }

            Attribute resultMapAttr = element.attribute("resultMap");
            String resultMap = namespace + ".";
            if (resultMapAttr != null) {
                resultMap += resultMapAttr.getData().toString();
                ResultMap map = configuration.getResultMap(resultMap);
                if (map == null) {
                    throw new RuntimeException("没有找到指定的resultMap");
                }
                mapperStatementBuilder.resultMap(map);
            }
            configuration.getMappedStatements().put(sourceId, mapperStatementBuilder.namespace(namespace).build());
        }
    }
}
