package com.zero.capsule.meds.listeners;

import java.util.List;

public interface UploadImageListener {
    void addPhoto(List<String> uri, String orderID);
}
