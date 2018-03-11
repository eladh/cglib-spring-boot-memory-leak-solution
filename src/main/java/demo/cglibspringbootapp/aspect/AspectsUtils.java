package demo.cglibspringbootapp.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.CustomAopProxyFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

public class AspectsUtils {

   private static CustomAopProxyFactory customAopProxyFactory = new CustomAopProxyFactory();

   public static <T> T getProxyWithInterceptor(Class<?> type, MethodInterceptor interceptor) {
      return (T) getProxy(null, type, interceptor);
   }

   public static <T> T recreateInstanceWithInterceptor(Object instance, Class<?> type, MethodInterceptor interceptor) {
      return (T) getProxy(instance, type, interceptor);
   }

   private static <T> T getProxy(Object instance, Class<?> type, MethodInterceptor interceptor) {
      ProxyFactory factory = null;
      if (AopUtils.isAopProxy(instance)) {
         return (T) instance;
      }
      try {
         factory = new ProxyFactory(instance != null ? instance : type.newInstance());
      } catch (Exception e) {
         e.printStackTrace();
      }

      factory.setAopProxyFactory(customAopProxyFactory);

      if (type.isInterface()) {
         factory.addInterface(type);
      } else {
         factory.setProxyTargetClass(true);
      }
      factory.addAdvice(interceptor);
      return (T) factory.getProxy();
   }


   public static Object getRealObject(Object proxy) {
      if (!AopUtils.isAopProxy((proxy))) {
         return proxy;
      }
      try {
         return ((Advised) proxy).getTargetSource().getTarget();
      } catch (Exception e) {
         return proxy;
      }
   }
}

