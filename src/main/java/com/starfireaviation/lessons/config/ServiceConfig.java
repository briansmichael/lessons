/*
 *  Copyright (C) 2022 Starfire Aviation, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starfireaviation.lessons.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.lessons.model.LessonPlanActivityRepository;
import com.starfireaviation.lessons.model.LessonPlanLessonRepository;
import com.starfireaviation.lessons.model.LessonPlanRepository;
import com.starfireaviation.lessons.model.LessonRepository;
import com.starfireaviation.lessons.service.DataService;
import com.starfireaviation.lessons.service.LessonPlanService;
import com.starfireaviation.lessons.service.LessonService;
import com.starfireaviation.lessons.validation.LessonValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * ServiceConfig.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ ApplicationProperties.class })
public class ServiceConfig {

    /**
     * LessonService.
     *
     * @param lRepository LessonRepository
     * @return LessonService
     */
    @Bean
    public LessonService lessonService(final LessonRepository lRepository) {
        return new LessonService(lRepository);
    }

    /**
     * LessonPlanService.
     *
     * @param lpRepository LessonPlanRepository
     * @param lpaRepository LessonPlanActivityRepository
     * @param lplRepository LessonPlanLessonRepository
     * @return LessonPlanService
     */
    @Bean
    public LessonPlanService lessonPlanService(final LessonPlanRepository lpRepository,
                                               final LessonPlanActivityRepository lpaRepository,
                                               final LessonPlanLessonRepository lplRepository) {
        return new LessonPlanService(lpRepository, lpaRepository, lplRepository);
    }

    /**
     * DataService.
     *
     * @return DataService
     */
    @Bean
    public DataService dataService() {
        return new DataService();
    }

    /**
     * HttpClient.
     *
     * @return HttpClient
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * ObjectMapper.
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Creates a rest template with default timeout settings. The bean definition
     * will be updated to accept timeout
     * parameters once those are part of the Customer settings.
     *
     * @param restTemplateBuilder RestTemplateBuilder
     * @param props   ApplicationProperties
     *
     * @return Rest Template with request, read, and connection timeouts set
     */
    @Bean
    public RestTemplate restTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            final ApplicationProperties props) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(props.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(props.getReadTimeout()))
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    /**
     * Hazelcast Questions Instance.
     *
     * @return HazelcastInstance
     */
    @Bean("lessons")
    public HazelcastInstance hazelcastLessonsInstance() {
        return Hazelcast.newHazelcastInstance(
                new Config().addMapConfig(
                        new MapConfig("lessons")
                                .setTimeToLiveSeconds(CommonConstants.THREE_HUNDRED)
                                .setMaxIdleSeconds(CommonConstants.THREE_HUNDRED)));
    }

    /**
     * Hazelcast LessonPlans Instance.
     *
     * @return HazelcastInstance
     */
    @Bean("lessonplans")
    public HazelcastInstance hazelcastLessonPlansInstance() {
        return Hazelcast.newHazelcastInstance(
                new Config().addMapConfig(
                        new MapConfig("lessonplans")
                                .setTimeToLiveSeconds(CommonConstants.THREE_HUNDRED)
                                .setMaxIdleSeconds(CommonConstants.THREE_HUNDRED)));
    }

    /**
     * Hazelcast LessonPlans Instance.
     *
     * @return HazelcastInstance
     */
    @Bean("activities")
    public HazelcastInstance hazelcastActivitiesInstance() {
        return Hazelcast.newHazelcastInstance(
                new Config().addMapConfig(
                        new MapConfig("activities")
                                .setTimeToLiveSeconds(CommonConstants.THREE_HUNDRED)
                                .setMaxIdleSeconds(CommonConstants.THREE_HUNDRED)));
    }

    /**
     * LessonValidator.
     *
     * @param dService DataService
     * @return LessonValidator
     */
    @Bean
    public LessonValidator lessonValidator(final DataService dService) {
        return new LessonValidator(dService);
    }

}
