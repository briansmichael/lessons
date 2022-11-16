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

package com.starfireaviation.lessons.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.starfireaviation.common.exception.AccessDeniedException;
import com.starfireaviation.common.exception.InvalidPayloadException;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.model.LessonPlan;
import com.starfireaviation.lessons.model.LessonPlanEntity;
import com.starfireaviation.lessons.service.LessonPlanService;
import com.starfireaviation.lessons.validation.LessonPlanValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LessonPlanController.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({ "/lessonplans" })
public class LessonPlanController {

    /**
     * LessonPlanService.
     */
    private final LessonPlanService lessonPlanService;

    /**
     * LessonPlanValidator.
     */
    private final LessonPlanValidator lessonPlanValidator;

    /**
     * Lesson Plan Cache.
     */
    private final IMap<Long, LessonPlan> cache;

    /**
     * LessonPlanController.
     *
     * @param lpService   LessonPlanService
     * @param lpValidator LessonPlanValidator
     * @param hazelcastInstance HazelcastInstance
     */
    public LessonPlanController(final LessonPlanService lpService,
                                final LessonPlanValidator lpValidator,
                                @Qualifier("lessonplans") final HazelcastInstance hazelcastInstance) {
        lessonPlanService = lpService;
        lessonPlanValidator = lpValidator;
        cache = hazelcastInstance.getMap("lessonplans");
    }

    /**
     * Creates a lessonPlan.
     *
     * @param lessonPlan LessonPlan
     * @param principal  Principal
     * @return LessonPlan
     * @throws ResourceNotFoundException when user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     */
    @PostMapping
    public LessonPlan post(@RequestBody final LessonPlan lessonPlan, final Principal principal)
            throws InvalidPayloadException,
            ResourceNotFoundException, AccessDeniedException {
        lessonPlanValidator.validate(lessonPlan);
        lessonPlanValidator.accessAdminOrInstructor(principal);
        return map(lessonPlanService.store(map(lessonPlan)));
    }

    /**
     * Gets a lessonPlan.
     *
     * @param lessonPlanId Long
     * @param principal    Principal
     * @return LessonPlan
     * @throws ResourceNotFoundException when lesson plan is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = { "/{lessonPlanId}" })
    public LessonPlan get(@PathVariable("lessonPlanId") final Long lessonPlanId, final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        lessonPlanValidator.accessAdminOrInstructor(principal);
        if (cache.containsKey(lessonPlanId)) {
            return cache.get(lessonPlanId);
        }
        final LessonPlan lessonPlan = map(lessonPlanService.get(lessonPlanId));
        cache.put(lessonPlanId, lessonPlan);
        return lessonPlan;
    }

    /**
     * Updates a lessonPlan.
     *
     * @param lessonPlan LessonPlan
     * @param principal  Principal
     * @return LessonPlan
     * @throws ResourceNotFoundException when user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     */
    @PutMapping
    public LessonPlan put(@RequestBody final LessonPlan lessonPlan, final Principal principal)
            throws InvalidPayloadException, ResourceNotFoundException, AccessDeniedException {
        lessonPlanValidator.validate(lessonPlan);
        lessonPlanValidator.accessAdminOrInstructor(principal);
        final LessonPlan updatedLessonPlan = map(lessonPlanService.store(map(lessonPlan)));
        lessonPlanService.linkLessons(updatedLessonPlan.getId(), lessonPlan.getLessonIds());
        lessonPlanService.linkActivities(updatedLessonPlan.getId(), lessonPlan.getActivityIds());
        cache.put(updatedLessonPlan.getId(), updatedLessonPlan);
        return updatedLessonPlan;
    }

    /**
     * Deletes a lessonPlan.
     *
     * @param lessonPlanId Long
     * @param principal    Principal
     * @throws ResourceNotFoundException when lesson plan is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @DeleteMapping(path = { "/{lessonPlanId}" })
    public void delete(@PathVariable("lessonPlanId") final Long lessonPlanId, final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        lessonPlanValidator.accessAdminOrInstructor(principal);
        lessonPlanService.delete(lessonPlanId);
        cache.remove(lessonPlanId);
    }

    /**
     * Get all lessonPlans.
     *
     * @param principal Principal
     * @return list of LessonPlans
     * @throws ResourceNotFoundException when lesson plan is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<LessonPlan> list(final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        lessonPlanValidator.accessAdminOrInstructor(principal);
        return lessonPlanService.getAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    /**
     * Maps LessonPlanEntity to LessonPlan.
     *
     * @param lessonPlanEntity LessonPlanEntity
     * @return LessonPlan
     */
    private LessonPlan map(final LessonPlanEntity lessonPlanEntity) {
        final LessonPlan lessonPlan = new LessonPlan();
        lessonPlan.setId(lessonPlanEntity.getId());
        lessonPlan.setActivityIds(lessonPlanService.getActivityIdsForLessonPlan(lessonPlanEntity.getId()));
        lessonPlan.setLessonIds(lessonPlanService.getLessonIdsForLessonPlan(lessonPlanEntity.getId()));
        lessonPlan.setContent(lessonPlanEntity.getContent());
        lessonPlan.setEquipment(lessonPlanEntity.getEquipment());
        lessonPlan.setCompletionStandards(lessonPlanEntity.getCompletionStandards());
        lessonPlan.setInstructorActions(lessonPlanEntity.getInstructorActions());
        lessonPlan.setObjective(lessonPlanEntity.getObjective());
        lessonPlan.setSchedule(lessonPlanEntity.getSchedule());
        lessonPlan.setStudentActions(lessonPlanEntity.getStudentActions());
        lessonPlan.setSummary(lessonPlanEntity.getSummary());
        lessonPlan.setTitle(lessonPlanEntity.getTitle());
        return lessonPlan;
    }

    /**
     * Maps LessonPlan to LessonPlanEntity.
     *
     * @param lessonPlan LessonPlan
     * @return LessonPlanEntity
     */
    private LessonPlanEntity map(final LessonPlan lessonPlan) {
        final LessonPlanEntity lessonPlanEntity = new LessonPlanEntity();
        lessonPlanEntity.setId(lessonPlan.getId());
        lessonPlanEntity.setContent(lessonPlan.getContent());
        lessonPlanEntity.setEquipment(lessonPlan.getEquipment());
        lessonPlanEntity.setCompletionStandards(lessonPlan.getCompletionStandards());
        lessonPlanEntity.setInstructorActions(lessonPlan.getInstructorActions());
        lessonPlanEntity.setObjective(lessonPlan.getObjective());
        lessonPlanEntity.setSchedule(lessonPlan.getSchedule());
        lessonPlanEntity.setStudentActions(lessonPlan.getStudentActions());
        lessonPlanEntity.setSummary(lessonPlan.getSummary());
        lessonPlanEntity.setTitle(lessonPlan.getTitle());
        return lessonPlanEntity;
    }

}
