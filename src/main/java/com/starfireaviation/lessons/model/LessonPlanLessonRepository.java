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

package com.starfireaviation.lessons.model;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * LessonPlanLessonRepository.
 */
public interface LessonPlanLessonRepository extends Repository<LessonPlanLesson, Long> {

    /**
     * Deletes a LessonPlanLesson.
     *
     * @param lessonPlanLesson LessonPlanLesson
     */
    void delete(LessonPlanLesson lessonPlanLesson);

    /**
     * Gets all LessonPlanLesson for a lesson plan.
     *
     * @param lessonPlanId LessonPlan ID
     * @return list of LessonPlanLesson
     */
    Optional<List<LessonPlanLesson>> findByLessonPlanId(Long lessonPlanId);

    /**
     * Gets all LessonPlanLesson for an lesson.
     *
     * @param lessonId Lesson ID
     * @return list of LessonPlanLesson
     */
    Optional<List<LessonPlanLesson>> findByLessonId(Long lessonId);

    /**
     * Gets all LessonPlanLesson for an lesson.
     *
     * @param lessonPlanId LessonPlan ID
     * @param lessonId Lesson ID
     * @return list of LessonPlanLesson
     */
    Optional<LessonPlanLesson> findByLessonPlanIdAndLessonId(Long lessonPlanId, Long lessonId);

    /**
     * Gets a LessonPlanLesson.
     *
     * @param id Long
     * @return LessonPlanLesson
     */
    Optional<LessonPlanLesson> findById(Long id);

    /**
     * Saves a LessonPlanLesson.
     *
     * @param lessonPlanLesson LessonPlanLesson
     * @return LessonPlanLesson
     */
    LessonPlanLesson save(LessonPlanLesson lessonPlanLesson);
}
