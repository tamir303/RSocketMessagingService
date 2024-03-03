package com.project.rsocketmessagingservice.dal;

import com.project.rsocketmessagingservice.data.MessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WeatherCrud extends ReactiveMongoRepository<MessageEntity, String> {
}
