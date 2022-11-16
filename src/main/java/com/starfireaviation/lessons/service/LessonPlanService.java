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

package com.starfireaviation.lessons.service;

import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.lessons.model.LessonPlanActivity;
import com.starfireaviation.lessons.model.LessonPlanActivityRepository;
import com.starfireaviation.lessons.model.LessonPlanEntity;
import com.starfireaviation.lessons.model.LessonPlanLesson;
import com.starfireaviation.lessons.model.LessonPlanLessonRepository;
import com.starfireaviation.lessons.model.LessonPlanRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LessonPlanService.
 */
@Slf4j
public class LessonPlanService {

    /**
     * LessonPlanRepository.
     */
    private final LessonPlanRepository lessonPlanRepository;

    /**
     * LessonPlanActivityRepository.
     */
    private final LessonPlanActivityRepository lessonPlanActivityRepository;

    /**
     * LessonPlanLessonRepository.
     */
    private final LessonPlanLessonRepository lessonPlanLessonRepository;

    /**
     * LessonPlanService.
     *
     * @param lpRepository LessonPlanRepository
     * @param lpaRepository LessonPlanActivityRepository
     * @param lplRepository LessonPlanLessonRepository
     */
    public LessonPlanService(final LessonPlanRepository lpRepository,
                             final LessonPlanActivityRepository lpaRepository,
                             final LessonPlanLessonRepository lplRepository) {
        lessonPlanRepository = lpRepository;
        lessonPlanActivityRepository = lpaRepository;
        lessonPlanLessonRepository = lplRepository;
    }

    /**
     * Creates a lessonPlan.
     *
     * @param lessonPlan LessonPlan
     * @return LessonPlan
     * @throws ResourceNotFoundException when resultant lesson plan is not found
     */
    public LessonPlanEntity store(final LessonPlanEntity lessonPlan) throws ResourceNotFoundException {
        if (lessonPlan == null) {
            return null;
        }
        return lessonPlanRepository.save(lessonPlan);
    }

    /**
     * Deletes a lessonPlan.
     *
     * @param lessonPlanId Long
     * @throws ResourceNotFoundException when lesson plan is not found
     */
    public void delete(final Long lessonPlanId) throws ResourceNotFoundException {
        lessonPlanActivityRepository
                .findByLessonPlanId(lessonPlanId)
                .orElse(new ArrayList<>())
                .forEach(lessonPlanActivityRepository::delete);
        lessonPlanRepository.delete(get(lessonPlanId));
    }

    /**
     * Gets all lessonPlan.
     *
     * @return list of LessonPlan
     */
    public List<LessonPlanEntity> getAll() {
        return lessonPlanRepository.findAll().orElseThrow();
    }

    /**
     * Gets a lessonPlan.
     *
     * @param lessonPlanId Long
     * @return LessonPlan
     * @throws ResourceNotFoundException when lesson plan is not found
     */
    public LessonPlanEntity get(final long lessonPlanId) throws ResourceNotFoundException {
        final LessonPlanEntity lessonPlan = lessonPlanRepository.findById(lessonPlanId).orElse(null);
        if (lessonPlan == null) {
            throw new ResourceNotFoundException(String.format("No lesson plan found for ID [%s]", lessonPlanId));
        }
        return lessonPlan;
    }

    /**
     * Gets list of Activity IDs for the given LessonPlan.
     *
     * @param lessonPlanId LessonPlan ID
     * @return list of Activity IDs
     */
    public List<Long> getActivityIdsForLessonPlan(final Long lessonPlanId) {
        return lessonPlanActivityRepository
                .findByLessonPlanId(lessonPlanId)
                .orElse(new ArrayList<>())
                .stream()
                .map(LessonPlanActivity::getActivityId)
                .collect(Collectors.toList());
    }

    /**
     * Gets list of Lesson IDs for the given LessonPlan.
     *
     * @param lessonPlanId LessonPlan ID
     * @return list of Lesson IDs
     */
    public List<Long> getLessonIdsForLessonPlan(final Long lessonPlanId) {
        return lessonPlanLessonRepository
                .findByLessonPlanId(lessonPlanId)
                .orElse(new ArrayList<>())
                .stream()
                .map(LessonPlanLesson::getLessonId)
                .collect(Collectors.toList());
    }

    /**
     * Links Activity to a LessonPlan.
     *
     * @param lessonPlanId LessonPlan ID
     * @param activityIds list of Activity IDs
     */
    public void linkActivities(final Long lessonPlanId, final List<Long> activityIds) {
        final List<Long> existingLinkedActivities = lessonPlanActivityRepository
                .findByLessonPlanId(lessonPlanId)
                .orElse(new ArrayList<>())
                .stream()
                .map(LessonPlanActivity::getActivityId)
                .collect(Collectors.toList());
        final List<Long> toBeLinked = new ArrayList<>(activityIds);
        toBeLinked.removeAll(existingLinkedActivities);
        for (final Long activityId : toBeLinked) {
            final Optional<LessonPlanActivity> lessonPlanActivityOpt =
                    lessonPlanActivityRepository.findByLessonPlanIdAndActivityId(lessonPlanId, activityId);
            if (lessonPlanActivityOpt.isEmpty()) {
                final LessonPlanActivity lessonPlanActivity = new LessonPlanActivity();
                lessonPlanActivity.setActivityId(activityId);
                lessonPlanActivity.setLessonPlanId(lessonPlanId);
                lessonPlanActivityRepository.save(lessonPlanActivity);
            }
        }
        final List<Long> toBeUnlinked = new ArrayList<>(existingLinkedActivities);
        toBeUnlinked.removeAll(activityIds);
        for (final Long activityId : toBeUnlinked) {
            lessonPlanActivityRepository
                    .findByLessonPlanIdAndActivityId(lessonPlanId, activityId)
                    .ifPresent(lessonPlanActivityRepository::delete);
        }
    }

    /**
     * (Un)Links Lesson to a LessonPlan.
     *
     * @param lessonPlanId LessonPlan ID
     * @param lessonIds list of Lesson IDs
     */
    public void linkLessons(final Long lessonPlanId, final List<Long> lessonIds) {
        final List<Long> existingLinkedLessons = lessonPlanLessonRepository
                .findByLessonPlanId(lessonPlanId)
                .orElse(new ArrayList<>())
                .stream()
                .map(LessonPlanLesson::getLessonId)
                .collect(Collectors.toList());
        final List<Long> toBeLinked = new ArrayList<>(lessonIds);
        toBeLinked.removeAll(existingLinkedLessons);
        for (final Long lessonId : toBeLinked) {
            final Optional<LessonPlanLesson> lessonPlanLessonOpt =
                    lessonPlanLessonRepository.findByLessonPlanIdAndLessonId(lessonPlanId, lessonId);
            if (lessonPlanLessonOpt.isEmpty()) {
                final LessonPlanLesson lessonPlanLesson = new LessonPlanLesson();
                lessonPlanLesson.setLessonId(lessonId);
                lessonPlanLesson.setLessonPlanId(lessonPlanId);
                lessonPlanLessonRepository.save(lessonPlanLesson);
            }
        }
        final List<Long> toBeUnlinked = new ArrayList<>(existingLinkedLessons);
        toBeUnlinked.removeAll(lessonIds);
        for (final Long lessonId : toBeUnlinked) {
            lessonPlanLessonRepository
                    .findByLessonPlanIdAndLessonId(lessonPlanId, lessonId)
                    .ifPresent(lessonPlanLessonRepository::delete);
        }
    }
}
