package com.project.rsocketmessagingservice.dal;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeviceCrud extends ReactiveMongoRepository<DeviceEntity, String> {
}
