package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.po.Order;
import com.seecoder.BlueWhale.repository.OrderRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.ExcelService;
import com.seecoder.BlueWhale.util.OssUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OssUtil ossUtil;
    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

    public String createOrderSheet(int storeId) throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        Sheet orderSheet = workbook.createSheet("订单报表");
        List<Order> orderList = new ArrayList<>();
        String excelName = "所有订单总报表";
        if (storeId == 0) {
            orderList = orderRepository.findAll();
        } else {
            orderList = orderRepository.findByStoreId(storeId);
            excelName = storeRepository.findByStoreId(storeId).getName() + "门店订单报表";
        }
        System.out.println(productRepository.findByStoreId(23));
        Row header = orderSheet.createRow(0);
        Cell orderId = header.createCell(0);
        orderId.setCellValue("订单ID");
        orderSheet.setColumnWidth(0, 2000);
        Cell createTime = header.createCell(1);
        createTime.setCellValue("创建时间");
        orderSheet.setColumnWidth(1, 7000);
        Cell finishTime = header.createCell(2);
        finishTime.setCellValue("完成时间");
        orderSheet.setColumnWidth(2, 7000);
        Cell storeName = header.createCell(3);
        storeName.setCellValue("商店名");
        orderSheet.setColumnWidth(3, 4000);
        Cell productName = header.createCell(4);
        productName.setCellValue("商品名");
        orderSheet.setColumnWidth(4, 4000);
        Cell deliveryOption = header.createCell(5);
        deliveryOption.setCellValue("提货方式");
        orderSheet.setColumnWidth(5, 2500);
        Cell state = header.createCell(6);
        state.setCellValue("商品状态");
        orderSheet.setColumnWidth(6, 2500);
        Cell paid = header.createCell(7);
        paid.setCellValue("总价");
        orderSheet.setColumnWidth(7, 2500);
        Cell userName = header.createCell(8);
        userName.setCellValue("用户名");
        orderSheet.setColumnWidth(8, 2500);
        for (int i = 0; i < orderList.size(); i++) {
            String stateName = "";
            Row orderInfo = orderSheet.createRow(i + 1);
            Order order = orderList.get(i);
            switch (order.getState().toString()) {
                case "UNPAID":
                    stateName = "待支付";
                    break;
                case "UNSEND":
                    stateName = "待发货";
                    break;
                case "UNGET":
                    stateName = "待收货";
                    break;
                case "UNCOMMENT":
                    stateName = "待评论";
                    break;
                case "DONE":
                    stateName = "已完成";
                    break;
                case "REFUND":
                    stateName = "已退款";
                    break;
                case "CANCELLED" :
                    stateName = "已取消";
                    break;

            }
            orderInfo.createCell(0).setCellValue(order.getOrderId());
            orderInfo.createCell(1).setCellValue(order.getCreateTime().toString());
            if(order.getFinishTime() != null) {
                orderInfo.createCell(2).setCellValue(order.getFinishTime().toString());
            } else {
                orderInfo.createCell(2).setCellValue("订单暂未完成");
            }
            orderInfo.createCell(3).setCellValue(storeRepository.findByStoreId(order.getStoreId()).getName());
            orderInfo.createCell(4).setCellValue(productRepository.findByProductId(order.getProductId()).getName());
            orderInfo.createCell(5).setCellValue(order.getDeliveryOption().toString().matches("DELIVERY") ? "快递送达" : "到店自提");
            orderInfo.createCell(6).setCellValue(stateName);
            orderInfo.createCell(7).setCellValue("￥" + order.getPaid());
            orderInfo.createCell(8).setCellValue(userRepository.findById(order.getUserId()).get().getName());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        logger.info("完成订单报表创建");
        return ossUtil.upload(excelName + ".xlsx", inputStream);
    }

}
