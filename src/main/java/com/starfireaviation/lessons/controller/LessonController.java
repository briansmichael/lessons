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

import com.starfireaviation.common.exception.AccessDeniedException;
import com.starfireaviation.common.exception.InvalidPayloadException;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.lessons.model.LessonEntity;
import com.starfireaviation.lessons.service.LessonService;
import com.starfireaviation.lessons.validation.LessonValidator;
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

/**
 * LessonController.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({ "/api/lessons" })
public class LessonController {

    /**
     * LessonService.
     */
    private final LessonService lessonService;

    /**
     * LessonValidator.
     */
    private final LessonValidator lessonValidator;

    /**
     * LessonController.
     *
     * @param lService   LessonService
     * @param lValidator LessonValidator
     */
    public LessonController(final LessonService lService,
                            final LessonValidator lValidator) {
        lessonService = lService;
        lessonValidator = lValidator;
    }

    /**
     * Creates a lesson.
     *
     * @param lesson    Lesson
     * @param principal Principal
     * @return Lesson
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     */
    @PostMapping
    public LessonEntity post(@RequestBody final LessonEntity lesson, final Principal principal) throws
            AccessDeniedException, InvalidPayloadException {
        lessonValidator.validate(lesson);
        lessonValidator.accessAdminOrInstructor(principal);
        return lessonService.store(lesson);
    }

    /**
     * Gets a lesson.
     *
     * @param lessonId  Long
     * @param principal Principal
     * @return Lesson
     * @throws ResourceNotFoundException when lesson is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{lessonId}"
    })
    public LessonEntity get(@PathVariable("lessonId") final long lessonId, final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        lessonValidator.accessAnyAuthenticated(principal);
        return lessonService.get(lessonId);
    }

    /**
     * Updates a lesson.
     *
     * @param lesson    Lesson
     * @param principal Principal
     * @return Lesson
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     */
    @PutMapping
    public LessonEntity put(@RequestBody final LessonEntity lesson, final Principal principal)
            throws InvalidPayloadException, AccessDeniedException {
        lessonValidator.validate(lesson);
        lessonValidator.accessAdminOrInstructor(principal);
        return lessonService.store(lesson);
    }

    /**
     * Deletes a lesson.
     *
     * @param lessonId  Long
     * @param principal Principal
     * @return Lesson
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @DeleteMapping(path = {
            "/{lessonId}"
    })
    public LessonEntity delete(@PathVariable("lessonId") final long lessonId, final Principal principal)
            throws AccessDeniedException {
        lessonValidator.accessAdminOrInstructor(principal);
        return lessonService.delete(lessonId);
    }

    /**
     * Gets all attended lessons.
     *
     * @param userId    Long
     * @param principal Principal
     * @return list of Lesson
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{userId}/attended"
    })
    public List<LessonEntity> getAllAttendedLessons(@PathVariable("userId") final long userId,
                                                    final Principal principal)
            throws AccessDeniedException {
        lessonValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        return lessonService.getAttendedLessons(userId);
    }

    /**
     * Gets all lessons for a given course.
     *
     * @param course    course name
     * @param principal Principal
     * @return list of Lesson
     * @throws ResourceNotFoundException when lesson information is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/all/{course}"
    })
    public List<LessonEntity> getAllLessonsByCourse(@PathVariable("course") final String course,
                                                    final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        lessonValidator.accessAnyAuthenticated(principal);
        return lessonService.getLessonsByCourse(course);
    }

    /**
     * Gets a list of all courses.
     *
     * @param principal Principal
     * @return list of courses
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/courses"
    })
    public List<String> getAllCourses(final Principal principal)
            throws AccessDeniedException {
        lessonValidator.accessAnyAuthenticated(principal);
        return lessonService.getAllCourses();
    }

    /**
     * Get all lessons.
     *
     * @param principal Principal
     * @return list of Lessons
     * @throws ResourceNotFoundException when lesson is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<LessonEntity> list(final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        lessonValidator.accessAdminOrInstructor(principal);
        return lessonService.getAll();
    }
}
