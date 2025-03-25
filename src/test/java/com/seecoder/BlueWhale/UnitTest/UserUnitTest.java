package com.seecoder.BlueWhale.UnitTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.serviceImpl.UserServiceImpl;
import com.seecoder.BlueWhale.util.RSAUtil;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.vo.UserVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

public class UserUnitTest {

				@Mock
				private UserRepository userRepository;

				@Mock
				private TokenUtil tokenUtil;

				@Mock
				private SecurityUtil securityUtil;

				@InjectMocks
				private UserServiceImpl userService;

				@Before
				public void setUp() {
								RSAUtil.initKey();
								MockitoAnnotations.initMocks(this);
				}

				@Test
				public void testRegister() {
								UserVO userVO = new UserVO();
								userVO.setPhone("1234567890");
								userVO.setName("Test User");
								userVO.setPassword("password");
        //mock 打桩，就是不管真实的方法如何执行，我们可以自行假设该方法执行的结果
								//后续的测试都是基于打桩结果来走
								User user = new User();
								user.setId(1);
								when(userRepository.findByPhone(userVO.getPhone())).thenReturn(null);
								when(userRepository.save(any(User.class))).thenReturn(user);

								boolean result = userService.register(userVO);

								assertTrue(result);
								verify(userRepository, times(1)).findByPhone(userVO.getPhone());
								verify(userRepository, times(1)).save(any(User.class));
				}

				@Test(expected = BlueWhaleException.class)
				public void testRegisterWithExistingPhone() {
								UserVO userVO = new UserVO();
								userVO.setPhone("1234567890");

								when(userRepository.findByPhone(userVO.getPhone())).thenReturn(new User());

								userService.register(userVO);
				}

				@Test
				public void testLogin() {
								String phone = "1234567890";
								String password = "password";
								User user = new User();

								when(userRepository.findByPhoneAndPassword(phone, password)).thenReturn(user);
								when(tokenUtil.getToken(user)).thenReturn("token");

								assertEquals("token", userService.login(phone, RSAUtil.encrypt(password,RSAUtil.getPublicKeyStr())));
								verify(userRepository, times(1)).findByPhoneAndPassword(phone, password);
								verify(tokenUtil, times(1)).getToken(user);
				}

				@Test(expected = BlueWhaleException.class)
				public void testLoginWithInvalidCredentials() {
								String phone = "1234567890";
								String password = "wrongpassword";

								when(userRepository.findByPhoneAndPassword(phone, password)).thenReturn(null);

								userService.login(phone, password);
				}

				@Test
				public void testGetInformation() {
								User user = new User();
								user.setId(1);
								user.setPhone("1234567890");
								user.setName("Test User");

								when(securityUtil.getCurrentUser()).thenReturn(user);

								UserVO userVO = userService.getInformation();

								assertEquals(user.getId(), userVO.getId());
								assertEquals(user.getPhone(), userVO.getPhone());
								assertEquals(user.getName(), userVO.getName());
								verify(securityUtil, times(1)).getCurrentUser();
				}

				@Test
				public void testUpdateInformation() {
								User user = new User();
								user.setId(1);
								user.setPhone("1234567890");
								user.setName("Test User");
								user.setPassword("password");

								UserVO userVO = new UserVO();
								userVO.setName("Updated Name");
								userVO.setAddress("Updated Address");

								when(securityUtil.getCurrentUser()).thenReturn(user);
								when(userRepository.save(any(User.class))).thenReturn(user);

								boolean result = userService.updateInformation(userVO);

								assertTrue(result);
								assertEquals("Updated Name", user.getName());
								assertEquals("Updated Address", user.getAddress());
								verify(securityUtil, times(1)).getCurrentUser();
								verify(userRepository, times(1)).save(user);
				}

				@Test
				public void testGetUserInformation() {
								User user = new User();
								user.setId(1);
								user.setPhone("1234567890");
								user.setName("Test User");

								when(userRepository.getOne(user.getId())).thenReturn(user);

								UserVO userVO = userService.getUserInformation(user.getId());

								assertEquals(user.getId(), userVO.getId());
								assertEquals(user.getPhone(), userVO.getPhone());
								assertEquals(user.getName(), userVO.getName());
								verify(userRepository, times(1)).getOne(user.getId());
				}

}
