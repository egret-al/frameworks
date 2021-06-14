package com.ibatis.binding;

import com.ibatis.config.Configuration;
import com.ibatis.config.MappedStatement;
import com.ibatis.mapping.SqlCommandType;
import com.ibatis.reflection.ParamNameResolver;
import com.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author：rkc
 * @date：Created in 2021/6/8 15:18
 * @description：
 */
public class MapperMethod {

    private final SqlCommandType sqlCommandType;
    private final MethodSignature methodSignature;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.methodSignature = new MethodSignature(configuration, mapperInterface, method);
        this.sqlCommandType = configuration.getMappedStatements().get(methodSignature.getName()).getSqlCommandType();
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        //根据不同的sql语句类型进行不同的特殊处理
        switch (sqlCommandType) {
            case INSERT: {
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                //通过SqlSession调用插入方法，而SqlSession最终会通过执行器Executor执行SQL语句；返回影响行数通过rowCountResult方法与接口返回类型进行适配
                result = rowCountResult(sqlSession.insert(methodSignature.getName(), param));
                break;
            }
            case UPDATE: {
                //update语句同insert
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.update(methodSignature.getName(), param));
                break;
            }
            case DELETE: {
                Object param = methodSignature.convertArgsToSqlCommandParam(args);
                result = rowCountResult(sqlSession.delete(methodSignature.getName(), param));
                break;
            }
            case SELECT: {
                //查询需要对返回类型的不同进行不同的特殊处理
                if (methodSignature.returnsVoid()) {
                    //返回的是void
                    return null;
                } else if (methodSignature.returnsMany()) {
                    //查询结果是集合，也就是查询多个
                    Object param = methodSignature.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectList(methodSignature.getName(), param);
                } else {
                    Object param = methodSignature.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectOne(methodSignature.getName(), param);
                }
                break;
            }
            default:
                throw new RuntimeException("未知的执行方法");
        }
        return result;
    }

    /**
     * 在得到返回的影响行数时进行对Method的返回类型的匹配处理
     * @param rowCount SQL执行后的影响行数
     * @return 如果方法返回类型是void，则返回空；如果方法返回类型是int，则直接返回；如果返回类型是long，则进行类型转换；
     * 如果方法类型是Boolean，则返回是否指向成功
     */
    private Object rowCountResult(int rowCount) {
        Object result;
        if (methodSignature.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(methodSignature.getReturnType()) || Integer.TYPE.equals(methodSignature.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(methodSignature.getReturnType()) || Long.TYPE.equals(methodSignature.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(methodSignature.getReturnType()) || Boolean.TYPE.equals(methodSignature.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new RuntimeException("方法返回类型不匹配！");
        }
        return result;
    }

    //包含了method的信息
    public static class MethodSignature {
        //返回是否是多个
        private final boolean returnsMany;
        //返回是否是void
        private final boolean returnsVoid;
        //返回类型
        private final Class<?> returnType;
        private final ParamNameResolver paramNameResolver;
        private final String name;

        public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
            this.returnType = method.getReturnType();
            this.returnsVoid = void.class.equals(returnType);
            this.returnsMany = Collection.class.isAssignableFrom(this.returnType) || this.returnType.isArray();
            this.paramNameResolver = new ParamNameResolver(configuration, method);
            this.name = method.getDeclaringClass().getName() + "." + method.getName();
        }

        public Object convertArgsToSqlCommandParam(Object[] args) {
            return paramNameResolver.getNamedParams(args);
        }

        public boolean returnsVoid() {
            return this.returnsVoid;
        }

        public boolean returnsMany() {
            return this.returnsMany;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public String getName() {
            return name;
        }
    }
}
