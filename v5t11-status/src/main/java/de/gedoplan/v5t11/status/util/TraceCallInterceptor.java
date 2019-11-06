package de.gedoplan.v5t11.status.util;

import de.gedoplan.baselibs.utils.util.ClassUtil;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interceptor for {@link TraceCall}.
 *
 * @author dw
 */
@TraceCall
@Interceptor
public class TraceCallInterceptor implements Serializable {

  @AroundInvoke
  private Object traceMethod(InvocationContext invocationContext) throws Exception {
    log(invocationContext.getTarget().getClass(), invocationContext.getMethod().getName());
    return invocationContext.proceed();
  }

  @AroundConstruct
  private Object traceCtor(InvocationContext invocationContext) throws Exception {
    log(invocationContext.getConstructor().getDeclaringClass(), "constructor");
    return invocationContext.proceed();
  }

  @PostConstruct
  private Object tracePostConstruct(InvocationContext invocationContext) throws Exception {
    log(invocationContext.getTarget().getClass(), "@PostConstruct method");
    return invocationContext.proceed();
  }

  @PreDestroy
  private Object tracePreDestroy(InvocationContext invocationContext) throws Exception {
    log(invocationContext.getTarget().getClass(), "@PreDestroy method");
    return invocationContext.proceed();
  }

  @AroundTimeout
  private Object traceTimeout(InvocationContext invocationContext) throws Exception {
    log(invocationContext.getTarget().getClass(), invocationContext.getMethod().getName());
    return invocationContext.proceed();
  }

  private static void log(Class<? extends Object> clazz, String text) {
    Log log = LogFactory.getLog(ClassUtil.getProxiedClass(clazz));
    if (log.isTraceEnabled()) {
      log.trace("Call " + text);
    }
  }
}
