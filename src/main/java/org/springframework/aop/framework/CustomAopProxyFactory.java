
package org.springframework.aop.framework;

import com.google.common.collect.Maps;
import org.springframework.aop.SpringProxy;
import org.springframework.cglib.proxy.Enhancer;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Map;


public class CustomAopProxyFactory implements AopProxyFactory, Serializable {

   // cache Class with CGLib Proxied Class
   private Map<Class, Class> classProxiesCache = Maps.newConcurrentMap();

   @Override
   public AopProxy createAopProxy(AdvisedSupport config) {
      if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
         Class<?> targetClass = config.getTargetClass();
         if (targetClass == null) {
            throw new AopConfigException("TargetSource cannot determine target class: " +
                  "Either an interface or a target is required for proxy creation.");
         }
         if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
            return new JdkDynamicAopProxy(config);
         }
         return new ObjenesisCglibAopProxy(config) {
            @Override
            protected Enhancer createEnhancer() {
               return new CustomEnhancer();
            }
         };
      }
      else {
         return new JdkDynamicAopProxy(config);
      }
   }

   private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
      Class<?>[] ifcs = config.getProxiedInterfaces();
      return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
   }

   private class CustomEnhancer extends Enhancer {

      private Class targetClass;

      @Override
      public void setSuperclass(Class superclass) {
         if (superclass != null) {
            targetClass = superclass;
         }
         super.setSuperclass(superclass);
      }

      @Override
      protected Class generate(ClassLoaderData data) {

         //in-case no className (for interfaces etc..) just delegate to parent
         if (targetClass == null) {
            return super.generate(data);
         }

         //if proxy exist in cache - return it instead of generate new one
         Class proxyClass = classProxiesCache.get(targetClass);
         if (proxyClass == null) {
            proxyClass = super.generate(data);
            //insert to cache after generation
            classProxiesCache.put(targetClass, proxyClass);
         }

         return proxyClass;
      }

   }

}
