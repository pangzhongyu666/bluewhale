package com.seecoder.BlueWhale.IntegrationTest;
import static org.junit.jupiter.api.Assertions.*;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.UserService;
import com.seecoder.BlueWhale.util.RSAUtil;
import com.seecoder.BlueWhale.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserIntegrationTest {

				@Autowired
				UserRepository userRepository;
				@Autowired
				UserService userService;
				private UserVO userVO1;
				private UserVO userVO2;

				@Before
				public void setUp() {
								// 清理现存数据
								userRepository.deleteAll();
								// 初始化用户数据
								userVO1 = new UserVO();
								userVO1.setPhone("12345678900");
								userVO1.setPassword("password1");
								userVO1.setName("Test User1");
								userVO1.setAddress("中国");
								userVO1.setRole(RoleEnum.CUSTOMER);

								userVO2 = new UserVO();
								userVO2.setPhone("12345678901");
								userVO2.setPassword("password2");
								userVO2.setName("Test User2");
								userVO2.setAddress("中国");
								userVO2.setRole(RoleEnum.STAFF);
				}


				@After
				public void tearDown() {
								// 清理测试数据
								userRepository.deleteAll();
				}



				@Test
				public void testUser() {
								RSAUtil.initKey();

								User existingUser = userRepository.findByPhone(userVO1.getPhone());
								assertNull(existingUser);

								//注册用户1
								Boolean result1 = userService.register(userVO1);
								assertTrue(result1);
								//注册用户2
								Boolean result2 = userService.register(userVO2);
								assertTrue(result2);

								User newUser1 = userRepository.findByPhone(userVO1.getPhone());
								assertNotNull(newUser1);
								assertEquals(userVO1.getPhone(), newUser1.getPhone());
								User newUser2 = userRepository.findByPhone(userVO2.getPhone());
								assertNotNull(newUser2);
								assertEquals(userVO2.getPhone(), newUser2.getPhone());

								//已存在账号报错
								assertThrows(BlueWhaleException.class, () -> userService.register(userVO1));

								//登陆时密码错误
								assertThrows(BlueWhaleException.class, () -> userService.login("12345678900", RSAUtil.encrypt("wrongpassword",RSAUtil.getPublicKeyStr())));

								//登录
								String token = userService.login(userVO1.getPhone(), RSAUtil.encrypt(userVO1.getPassword(),RSAUtil.getPublicKeyStr()));
								assertNotNull(token);

								//获取某个用户信息
								UserVO userVO = userService.getUserInformation(newUser1.getId());
								assertEquals(userVO1.getPhone(), userVO.getPhone());
								assertEquals(userVO1.getName(), userVO.getName());
								assertEquals(userVO1.getAddress(), userVO.getAddress());
								assertEquals(userVO1.getRole(), userVO.getRole());

								userVO = userService.getUserInformation(newUser2.getId());
								assertEquals(userVO2.getPhone(), userVO.getPhone());
								assertEquals(userVO2.getName(), userVO.getName());
								assertEquals(userVO2.getAddress(), userVO.getAddress());
								assertEquals(userVO2.getRole(), userVO.getRole());
				}




}
