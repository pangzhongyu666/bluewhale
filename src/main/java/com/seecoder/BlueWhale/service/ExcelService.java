package com.seecoder.BlueWhale.service;

import java.io.IOException;

public interface ExcelService {
    String createOrderSheet(int storeId) throws IOException;
}
