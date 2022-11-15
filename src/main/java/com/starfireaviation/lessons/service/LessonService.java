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
import com.starfireaviation.lessons.model.LessonEntity;
import com.starfireaviation.lessons.model.LessonRepository;

import java.util.List;

/**
 * LessonService.
 */
public class LessonService {

    /**
     * LessonRepository.
     */
    private final LessonRepository lessonRepository;

    /**
     * LessonService.
     *
     * @param lRepository LessonRepository
     */
    public LessonService(final LessonRepository lRepository) {
        lessonRepository = lRepository;
    }

    /**
     * Creates a lesson.
     *
     * @param lesson Lesson
     * @return Lesson
     */
    public LessonEntity store(final LessonEntity lesson) {
        return lessonRepository.save(lesson);
    }

    /**
     * Deletes a lesson.
     *
     * @param id Long
     * @return Lesson
     */
    public LessonEntity delete(final Long id) {
        final LessonEntity lesson = get(id);
        if (lesson != null) {
            lessonRepository.delete(lesson);
        }
        return lesson;
    }

    /**
     * Gets all lesson.
     *
     * @return list of Lesson
     */
    public List<LessonEntity> getAll() {
        return lessonRepository.findAll();
    }

    /**
     * Gets a lesson.
     *
     * @param id Long
     * @return Lesson
     */
    public LessonEntity get(final Long id) {
        return lessonRepository.findById(id).orElseThrow();
    }

    /**
     * Gets all lessons for the given course.
     *
     * @param group name
     * @return list of Lesson
     * @throws ResourceNotFoundException when course is not found
     */
    public List<LessonEntity> getLessonsByGroup(final String group) throws ResourceNotFoundException {
        if (group == null) {
            throw new ResourceNotFoundException(String.format("No group found for [%s]", group));
        }
        return lessonRepository.findByGroup(group).orElseThrow();
    }

}
