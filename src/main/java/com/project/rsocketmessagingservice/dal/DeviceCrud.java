package com.project.rsocketmessagingservice.dal;

import com.project.rsocketmessagingservice.data.DeviceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Interface for accessing device data in the MongoDB repository in a reactive way.
 */
public interface DeviceCrud extends ReactiveMongoRepository<DeviceEntity, String> {
}
