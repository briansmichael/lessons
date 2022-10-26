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

/**
 * LessonRepository.
 */
public interface LessonRepository extends Repository<Lesson, Long> {

    /**
     * Deletes a lesson.
     *
     * @param lesson LessonEntity
     */
    void delete(Lesson lesson);

    /**
     * Gets all lesson.
     *
     * @return list of LessonEntity
     */
    List<Lesson> findAll();

    /**
     * Gets a lesson.
     *
     * @param id Long
     * @return LessonEntity
     */
    Lesson findById(long id);

    /**
     * Saves a lesson.
     *
     * @param lesson LessonEntity
     * @return LessonEntity
     */
    Lesson save(Lesson lesson);
}
