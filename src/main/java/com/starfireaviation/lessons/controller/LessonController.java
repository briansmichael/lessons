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
import com.starfireaviation.common.model.Lesson;
import com.starfireaviation.lessons.model.LessonEntity;
import com.starfireaviation.lessons.service.LessonService;
import com.starfireaviation.lessons.validation.LessonValidator;
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
     * Lessons Cache.
     */
    private final IMap<Long, Lesson> cache;

    /**
     * LessonController.
     *
     * @param lService   LessonService
     * @param lValidator LessonValidator
     * @param hazelcastInstance HazelcastInstance
     */
    public LessonController(final LessonService lService,
                            final LessonValidator lValidator,
                            @Qualifier("lessons") final HazelcastInstance hazelcastInstance) {
        lessonService = lService;
        lessonValidator = lValidator;
        cache = hazelcastInstance.getMap("lessons");
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
    public Lesson post(@RequestBody final Lesson lesson, final Principal principal)
            throws AccessDeniedException, InvalidPayloadException {
        lessonValidator.validate(lesson);
        lessonValidator.accessAdminOrInstructor(principal);
        final Lesson newLesson = map(lessonService.store(map(lesson)));
        cache.put(newLesson.getId(), newLesson);
        return newLesson;
    }

    /**
     * Gets a lesson.
     *
     * @param lessonId  Long
     * @param principal Principal
     * @return Lesson
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = { "/{lessonId}" })
    public Lesson get(@PathVariable("lessonId") final Long lessonId, final Principal principal)
            throws AccessDeniedException {
        lessonValidator.accessAnyAuthenticated(principal);
        if (cache.containsKey(lessonId)) {
            return cache.get(lessonId);
        }
        final Lesson lesson = map(lessonService.get(lessonId));
        cache.put(lesson.getId(), lesson);
        return lesson;
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
    public Lesson put(@RequestBody final Lesson lesson, final Principal principal)
            throws InvalidPayloadException, AccessDeniedException {
        lessonValidator.validate(lesson);
        lessonValidator.accessAdminOrInstructor(principal);
        final Lesson updatedLesson = map(lessonService.store(map(lesson)));
        cache.put(updatedLesson.getId(), updatedLesson);
        return updatedLesson;
    }

    /**
     * Deletes a lesson.
     *
     * @param lessonId  Long
     * @param principal Principal
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @DeleteMapping(path = { "/{lessonId}" })
    public void delete(@PathVariable("lessonId") final Long lessonId, final Principal principal)
            throws AccessDeniedException {
        lessonValidator.accessAdminOrInstructor(principal);
        lessonService.delete(lessonId);
        cache.delete(lessonId);
    }

    /**
     * Gets all lessons for a given course.
     *
     * @param group    group name (ex. PVT or IFR)
     * @param principal Principal
     * @return list of Lesson
     * @throws ResourceNotFoundException when lesson information is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = { "/all/{group}" })
    public List<Lesson> getAllLessonsByGroup(@PathVariable("group") final String group,
                                             final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        lessonValidator.accessAnyAuthenticated(principal);
        return lessonService.getLessonsByGroup(group).stream().map(this::map).collect(Collectors.toList());
    }

    /**
     * Get all lessons.
     *
     * @param principal Principal
     * @return list of Lessons
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<Lesson> list(final Principal principal) throws AccessDeniedException {
        lessonValidator.accessAdminOrInstructor(principal);
        return lessonService.getAll().stream().map(this::map).collect(Collectors.toList());
    }

    /**
     * Maps a LessonEntity to a Lesson.
     *
     * @param lessonEntity LessonEntity
     * @return Lesson
     */
    private Lesson map(final LessonEntity lessonEntity) {
        final Lesson lesson = new Lesson();
        lesson.setChapter(lessonEntity.getChapter());
        lesson.setGroup(lessonEntity.getGroup());
        lesson.setId(lessonEntity.getId());
        lesson.setTitle(lessonEntity.getTitle());
        lesson.setText(lessonEntity.getText());
        lesson.setRequired(lessonEntity.isRequired());
        lesson.setCreatedAt(lessonEntity.getCreatedAt());
        lesson.setUpdatedAt(lessonEntity.getUpdatedAt());
        return lesson;
    }

    /**
     * Maps a Lesson to a LessonEntity.
     *
     * @param lesson Lesson
     * @return LessonEntity
     */
    private LessonEntity map(final Lesson lesson) {
        final LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setChapter(lesson.getChapter());
        lessonEntity.setGroup(lesson.getGroup());
        lessonEntity.setId(lesson.getId());
        lessonEntity.setText(lesson.getText());
        lessonEntity.setTitle(lesson.getTitle());
        lessonEntity.setRequired(lesson.isRequired());
        lessonEntity.setCreatedAt(lesson.getCreatedAt());
        lessonEntity.setUpdatedAt(lesson.getUpdatedAt());
        return lessonEntity;
    }

}
