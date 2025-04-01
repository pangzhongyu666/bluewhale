package com.seecoder.BlueWhale.aspect;

import com.seecoder.BlueWhale.anno.PreAuthorize;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.serviceImpl.StoreServiceImpl;
import com.seecoder.BlueWhale.util.SecurityUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class AuthorizeAspect {

				@Autowired
				private UserRepository userRepository;

				private static final Logger logger = LoggerFactory.getLogger(AuthorizeAspect.class);

				@Autowired
				private UserService userService;

				// 配置切入点表达式, @annotation 表示标注了指定注解的方法
				@Pointcut("@annotation(com.seecoder.BlueWhale.anno.PreAuthorize)")
				public void authorizePointcut() {
								// 切入点表达式
				}
				@Around("authorizePointcut()")
				public Object handle(ProceedingJoinPoint pjp) throws Throwable {
								// 处理鉴权逻辑

								//1.查询用户的所有资源权限列表
								List<String> privileges = userService.getCurrUserPrivileges();
								//2.查询当前请求的资源权限
								MethodSignature signature = (MethodSignature) pjp.getSignature();
								PreAuthorize preAuthorize = signature.getMethod().getAnnotation(PreAuthorize.class);
								String privilege = preAuthorize.value();
								//3.判断是否有权限
								if(!privileges.contains(privilege)){
												throw new BlueWhaleException("无权限");
								}

								logger.info("用户有权限, privilege={}", privilege);

								//4.如果有权限,则执行方法
								return pjp.proceed();
				}
}
