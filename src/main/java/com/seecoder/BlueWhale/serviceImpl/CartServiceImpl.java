package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Cart;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.repository.CartRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.service.CartService;
import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.vo.CartVO;
import com.seecoder.BlueWhale.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderService orderService;


    //创建购物车并初始化
    @Override
    public Boolean create(CartVO cartVO) {
        Cart newCart = cartVO.toPO();
        newCart.setTotalPrice(0);
        newCart.setTimes(0);
        cartRepository.save(newCart);
        return true;
    }

    @Override
    public CartVO getCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if(cart == null){
            throw BlueWhaleException.cartNotExists();
        }
        List<Integer> chooseList = new ArrayList<>(); //在每次重新进入购物车界面刷新选择内容
        cart.setChooseList(chooseList);
        cart.setOrderIdList(new ArrayList<>()); //若上次使用后有未支付的订单则抛出，不在本次使用时同时支付
        cartRepository.save(cart);
        return cart.toVO();
    }

    //添加商品
    @Override
    public Integer addProduct(Integer userId, Integer productId, Integer count) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> productIdList = cart.getProductIdList();
        List<Integer> productCountList = cart.getProductCountList();
        if(productIdList.contains(productId)) { //若是相同商品加入购物车，则与之前的合并
            int oldCount = productCountList.get(productIdList.indexOf(productId));
            changeCount(userId, productId, oldCount + count);
        } else {
            productIdList.add(productId);
            cart.setProductIdList(productIdList);
            productCountList.add(count);
            cart.setProductCountList(productCountList);
            Product product = productRepository.findByProductId(productId);
            int price = cart.getTotalPrice();
            cart.setTotalPrice(price + product.getPrice() * count);
            cartRepository.save(cart);
        }
        return productId;
    }

    //改变商品数量
    @Override
    public Integer changeCount(Integer userId, Integer productId, Integer count) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> list = cart.getProductIdList();
        List<Integer> pclist = cart.getProductCountList();
        Integer[] productList = list.toArray(list.toArray(new Integer[0]));
        Integer[] productCountList = pclist.toArray(new Integer[0]);
        for(int i = 0; i < productList.length; i++){
            if(Objects.equals(productList[i], productId)) {
                Integer price = cart.getTotalPrice();
                Integer productPrice = productRepository.findByProductId(productId).getPrice();
                cart.setTotalPrice(price + productPrice * (count - productCountList[i])); //修改购物车中总价
                productList[i] = productId;
                productCountList[i] = count; //修改对应数量
                break;
            }
        }
        cart.setProductIdList(new ArrayList<>(Arrays.asList(productList)));
        cart.setProductCountList(new ArrayList<>(Arrays.asList(productCountList)));
        cartRepository.save(cart);
        return count;
    }

    //移除商品
    @Override
    public Integer removeProduct(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> productList = cart.getProductIdList();
        List<Integer> productCountList = cart.getProductCountList();
        List<Integer> choosetList = cart.getChooseList();
        int i = productList.indexOf(productId);
        int price = cart.getTotalPrice();
        int productPrice = productRepository.findByProductId(productList.get(i)).getPrice();
        cart.setTotalPrice(price - productPrice * productCountList.get(i));
        productList.remove(i);
        productCountList.remove(i);
        cart.setProductIdList(productList);
        cart.setProductCountList(productCountList);
        if(choosetList.contains(productId)) {
            choosetList.remove(productId);
            cart.setChooseList(choosetList);
        }
        cartRepository.save(cart);
        return productId;
    }

    //选中商品
    @Override
    public Integer chooseProduct(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> chooseList = cart.getChooseList();
        chooseList.add(productId);
        cart.setChooseList(chooseList);
        cartRepository.save(cart);
        return productId;
    }

    //取消选中商品
    @Override
    public Integer cancelChooseProduct(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> chooseList = cart.getChooseList();
        chooseList.remove(productId);
        cart.setChooseList(chooseList);
        cartRepository.save(cart);
        return productId;
    }

    //根据已选中商品创建订单
    @Override
    public Boolean createOrders(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Long> orderIdList = cart.getOrderIdList();
        List<Integer> productList = cart.getProductIdList();
        List<Integer> productCountList = cart.getProductCountList();
        List<Integer> chooseList = cart.getChooseList();
        int totalPrice = cart.getTotalPrice();
        int times = cart.getTimes();
        for(int i = 0; i < chooseList.size(); i++){
            int productId = chooseList.get(i);
            if(productId != 0) {
                int index = productList.indexOf(productId);
                int quantity = productCountList.get(index);
                Product product = productRepository.findByProductId(productId);
                OrderVO orderVO = new OrderVO();
                orderVO.setStoreId(product.getStoreId());
                orderVO.setProductId(productId);
                orderVO.setQuantity(quantity);
                orderVO.setDeliveryOption(DeliveryEnum.DELIVERY);
                orderVO.setUserId(userId);
                orderVO.setPaid(product.getPrice() * quantity * 1.0);
                orderVO.setState(OrderStateEnum.UNPAID);
                productList.remove(index);
                productCountList.remove(index);
                totalPrice -= product.getPrice() * quantity;
            }
        }
        chooseList = new ArrayList<>();
        times++;
        cart.setTimes(times);
        cart.setOrderIdList(orderIdList);
        cart.setProductIdList(productList);
        cart.setProductCountList(productCountList);
        cart.setChooseList(chooseList);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return true;
    }

    //检查支付结果
    @Override
    public Boolean checkPayResult(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if(cart == null){
            throw BlueWhaleException.cartNotExists();
        }
        return cart.getOrderIdList().isEmpty(); //在支付异步回调成功后会清空订单列表，则可知支付结果
    }

    //清空购物车
    @Override
    public Boolean clear(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<Integer> newList = new ArrayList<>();
        cart.setProductIdList(newList);
        cart.setProductCountList(newList);
        cart.setOrderIdList(new ArrayList<>());
        cart.setTotalPrice(0);
        cartRepository.save(cart);
        return true;
    }
}
