package org.fsp.gitfinder.service;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryServiceTest {

    @Test
    void testJson() {
        // Given an instance of FooClass and the JSON of this instance
        FooClass bar = new FooClass("bar");
        String expectedJson = "{\"name\":\"bar\"}";

        // When we make a JSON
        JSONObject actualJSON = new JSONObject();
        actualJSON.put("name",bar);
        System.out.println(actualJSON);

        //Then they should match
        System.out.println(bar);
        assertEquals(expectedJson, actualJSON.toString());
    }


    class FooClass implements Serializable {
        private String name;

        FooClass(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}