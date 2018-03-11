package demo.cglibspringbootapp.aspect;

import com.google.common.eventbus.EventBus;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;

public class ObjectAudited implements MethodInterceptor {


   private final EventBus eventBus;
   private final String prefixOgnl;


   public ObjectAudited(EventBus theEventBus, String thePrefixOgnl) {
      eventBus = theEventBus;
      prefixOgnl = thePrefixOgnl;
   }

   @Override
   public Object invoke(MethodInvocation methodInvocation) throws Throwable {
      Method method = methodInvocation.getMethod();
      String methodName = StringUtils.uncapitalize(method.getName().substring(3));

      if (method.getName().startsWith("get")) {
         String ognl = prefixOgnl + '.' + methodName;

         Object invocationResult = methodInvocation.proceed();

         if (invocationResult == null) {
            return null;
         }

         Class<?> returnType = methodInvocation.getMethod().getReturnType();

         if (invocationResult.getClass().equals(Object.class)) {
            return invocationResult;
         }

         if (List.class.isAssignableFrom(invocationResult.getClass())) {
            return invocationResult;
         }

         if (Modifier.isFinal(invocationResult.getClass().getModifiers()) || Date.class.isAssignableFrom(invocationResult.getClass())) {
            return invocationResult;
         }

         return AspectsUtils.recreateInstanceWithInterceptor(invocationResult, returnType,
                  new ObjectAudited(eventBus, ognl));
      }

      if (method.getName().startsWith("set")) {
         String fieldName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
         String ognl = prefixOgnl + '.' + fieldName;
         Object prevObj = getMethodValue(methodInvocation, fieldName);
         Object methodResult = methodInvocation.proceed();
         Object newObj = getMethodValue(methodInvocation, fieldName);

         eventBus.post(new OgnlChangeEvent(ognl, prevObj, newObj));

         return methodResult;
      }

      return methodInvocation.proceed();
   }

   private static Object getMethodValue(MethodInvocation methodInvocation, String fieldName) {
      try {
         Field field = ReflectionUtils.findField(methodInvocation.getThis().getClass(), fieldName);
         if (field != null) {
            field.setAccessible(true);
            return field.get(methodInvocation.getThis());
         }
         return null;

      } catch (Exception ignored) {
         return null;
      }
   }
}
