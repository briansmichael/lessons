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

package com.starfireaviation.lessons;

import com.starfireaviation.lessons.model.Lesson;
import com.starfireaviation.model.CommonConstants;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LessonsSteps {

    /**
     * URL.
     */
    protected static final String URL = "http://localhost:8080";

    /**
     * ORGANIZATION.
     */
    protected static final String ORGANIZATION = "TEST_ORG";

    /**
     * RestTemplate.
     */
    protected RestTemplate restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler()).build();

    @Autowired
    protected TestContext testContext;

    @Before
    public void init() {
        testContext.reset();
    }

    @Given("^I have a lesson$")
    public void iHaveALesson() throws Throwable {
        testContext.setLesson(new Lesson());
    }

    @Given("^A lesson exists$")
    public void aLessonExists() throws Throwable {
        // TODO
    }

    @And("^The lesson has course with (.*) characters$")
    public void theLessonHasCourseWithXCharacters(final int characterCount) throws Throwable {
        // TODO
    }

    @And("^The lesson has unit with (.*) characters$")
    public void theLessonHasUnitWithXCharacters(final int characterCount) throws Throwable {
        // TODO
    }

    @When("^I submit the lesson$")
    public void iAddTheLesson() throws Throwable {
        log.info("I submit the lesson");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (testContext.getOrganization() != null) {
            headers.add(CommonConstants.ORGANIZATION_HEADER_KEY, testContext.getOrganization());
        }
        if (testContext.getCorrelationId() != null) {
            headers.add(CommonConstants.CORRELATION_ID_HEADER_KEY, testContext.getCorrelationId());
        }
        //final HttpEntity<Question> httpEntity = new HttpEntity<>(testContext.getQuestion(), headers);
        //testContext.setResponse(restTemplate.postForEntity(URL, httpEntity, Void.class));
    }

    @When("^I get the lesson$")
    public void iGetTheLesson() throws Throwable {
        // TODO
    }

    @When("^I submit the lesson for update$")
    public void iSubmitTheLessonForUpdate() throws Throwable {
        // TODO
    }

    @When("^I delete the lesson$")
    public void iDeleteTheLesson() throws Throwable {
        // TODO
    }

    @When("^I get all attended lessons$")
    public void iGetAllAttendedLessons() throws Throwable {
        // TODO
    }

    @When("^I get all lessons for a given course$")
    public void iGetAllLessonsForAGivenCourse() throws Throwable {
        // TODO
    }

    @When("^I get a list of all courses$")
    public void iGetAListOfAllCourses() throws Throwable {
        // TODO
    }

    @When("^I get all lessons$")
    public void iGetAllLessons() throws Throwable {
        // TODO
    }

    @And("^A lesson should be received$")
    public void aLessonShouldBeReceived() throws Throwable {
        // TODO
    }

    @And("^The lesson should be removed$")
    public void theLessonShouldBeRemoved() throws Throwable {
        // TODO
    }

}
