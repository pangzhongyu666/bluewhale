package com.seecoder.BlueWhale.configure;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Cart;
import com.seecoder.BlueWhale.po.Order;
import com.seecoder.BlueWhale.repository.CartRepository;
import com.seecoder.BlueWhale.repository.OrderRepository;
import com.seecoder.BlueWhale.service.OrderService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "alipay")
@Component
@Getter
@Setter
public class AlipayTools {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderService orderService;

    @Value("${alipay.appId}")
    private String appId;
    @Value("${alipay.appPrivateKey}")
    private String privateKey;
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${alipay.server-url}")
    private String serverUrl;
    @Value("${alipay.charset}")
    private String charset;
    @Value("${alipay.sign-type}")
    private String signType;
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
    @Value("${alipay.returnUrl}")
    private String returnUrl;
    private static String format="json";

    private static final Logger logger = LoggerFactory.getLogger(AlipayTools.class);

    /**
     * @Date: 11:25 2024/1/31
     * 使用支付宝沙箱
     * 使用时可以根据自己的需要做修改，包括参数名、返回值、具体实现
     * 在bizContent中放入关键的信息：tradeName、price、name
     * 返回的form是一个String类型的html页面
     */
    public String pay(String tradeName, String name , Double price, String path,Integer couponId){
        Order order = orderRepository.findById(Long.parseLong(tradeName)).orElse(null);
        if(order == null){//查看订单是否存在
            throw BlueWhaleException.orderNotExists();
        }
        //检测订单状态,防止超时取消后还能支付
        if(order.getState() != OrderStateEnum.UNPAID){
            throw BlueWhaleException.orderStatusError();
        }
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,appId,privateKey,format,charset,alipayPublicKey,signType);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl + "/"  + couponId);
        request.setReturnUrl("http://localhost:3000/#" + path);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", tradeName);
        bizContent.put("total_amount", price);
        bizContent.put("subject", name);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String form;
        try  {
            form = alipayClient.pageExecute(request).getBody();
        }  catch  (Exception e) {
            throw BlueWhaleException.payError();
        }
        return form;
    }
    public Boolean refund(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            throw BlueWhaleException.orderNotExists();
        }
        String tradeName = order.getTradeName();
        if(order.getState() != OrderStateEnum.UNSEND
        && order.getState() != OrderStateEnum.UNGET
        && order.getState() != OrderStateEnum.DONE
        && order.getState() != OrderStateEnum.UNCOMMENT){
            throw BlueWhaleException.refundRefuse();//以上状态才可退款
        }
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,appId,privateKey,format,charset,alipayPublicKey,signType);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", tradeName);
        Double price = orderRepository.findById(orderId).get().getPaid();
        bizContent.put("refund_amount", price);
        //标识一次退款请求，同一笔交易多次退款需要保证唯一。如需部分退款，则此参数必传；不传该参数则代表全额退款
        bizContent.put("out_request_no", "HZ01RF001");
        request.setBizContent(bizContent.toString());
        try  {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                orderService.refundSuccess(orderId);
                logger.info("退款成功");
                return true;
            } else {
                logger.info("退款失败");
                return false;
            }
        }  catch  (Exception e) {
            throw BlueWhaleException.refundError();
        }
				}

    public void notify(HttpServletRequest httpServletRequest,Integer couponId) {
        if (httpServletRequest.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            logger.info("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, httpServletRequest.getParameter(name));
            }

            String tradeName = params.get("out_trade_no");
            Double paid = Double.parseDouble(params.get("total_amount"));
            orderService.paySuccess(Long.parseLong(tradeName),paid,couponId);
            Order order = orderRepository.findById(Long.parseLong(tradeName)).get();
            order.setTradeName(tradeName);
            orderRepository.save(order);
        }
    }








    public String payCart(String tradeName, String name , Double price, String path, Integer cartId){
        Cart cart = cartRepository.findByCartId(cartId);
        if(cart.getOrderIdList().isEmpty()){
            throw BlueWhaleException.orderNotExists();
        }
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,appId,privateKey,format,charset,alipayPublicKey,signType);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl + "Cart" + "/"  + cartId);
        request.setReturnUrl("http://localhost:3000/#" + path);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", tradeName);
        bizContent.put("total_amount", price);
        bizContent.put("subject", name);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String form;
        try  {
            form = alipayClient.pageExecute(request).getBody();
        }  catch  (Exception e) {
            throw BlueWhaleException.payError();
        }
        return form;
    }
    public void notifyCart(HttpServletRequest httpServletRequest,Integer cartId) {
        if (httpServletRequest.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            logger.info("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, httpServletRequest.getParameter(name));
            }

            String tradeName = params.get("out_trade_no");
            Double paid = Double.parseDouble(params.get("total_amount"));
            Cart cart = cartRepository.findByCartId(cartId);
            List<Long> orderIdList = cart.getOrderIdList();
            for(int i = 0; i < orderIdList.size(); i++){
                Order order = orderRepository.findById(orderIdList.get(i)).get();
                orderService.paySuccess(order.getOrderId(),order.getPaid(),0);
                order.setTradeName(tradeName);
                orderRepository.save(order);
            }
            cart.setOrderIdList(new ArrayList<>());
            cartRepository.save(cart);
        }
    }
}