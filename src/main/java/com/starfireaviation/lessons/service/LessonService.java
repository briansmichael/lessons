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

import com.starfireaviation.groundschool.exception.ResourceNotFoundException;
import com.starfireaviation.groundschool.model.Lesson;
import com.starfireaviation.groundschool.model.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public Lesson store(final Lesson lesson) {
        if (lesson == null) {
            return lesson;
        }
        return lessonRepository.save(lesson);
    }

    /**
     * Deletes a lesson.
     *
     * @param id Long
     * @return Lesson
     */
    public Lesson delete(final long id) {
        final Lesson lesson = get(id);
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
    public List<Lesson> getAll() {
        final List<Lesson> lessons = new ArrayList<>();
        final List<Lesson> lessonEntities = lessonRepository.findAll();
        for (final Lesson lessonEntity : lessonEntities) {
            lessons.add(get(lessonEntity.getId()));
        }
        return lessons;
    }

    /**
     * Gets all lessons attended by a participant.
     *
     * @param userId User ID
     * @return list of Lesson
     */
    public List<Lesson> getAttendedLessons(final Long userId) {
        return new ArrayList<>();
    }

    /**
     * Gets a lesson.
     *
     * @param id Long
     * @return Lesson
     */
    public Lesson get(final long id) {
        return lessonRepository.findById(id);
    }

    /**
     * Gets all courses.
     *
     * @return list of course names
     */
    public List<String> getAllCourses() {
        return getAll()
                .stream()
                .map(Lesson::getCourse)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Gets all lessons for the given course.
     *
     * @param course name
     * @return list of Lesson
     * @throws ResourceNotFoundException when course is not found
     */
    public List<Lesson> getLessonsByCourse(final String course) throws ResourceNotFoundException {
        if (course == null) {
            throw new ResourceNotFoundException(String.format("No course found for [%s]", course));
        }
        return getAll()
                .stream()
                .filter(lesson -> course.equals(lesson.getCourse()))
                .collect(Collectors.toList());
    }

}
