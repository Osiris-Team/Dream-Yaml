package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DreamYamlTest {

    @Test
    void getAddedModuleByKeys() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValue("world");
        yaml.save(true);
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void getLoadedModuleByKeys() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValue("world");
        yaml.save(true);
        yaml.load(); // Reload the config so we got the loaded Module
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void reset() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/reset-test.yml");
        yaml.load();
        yaml.put("hello").setDefValue("world");
        yaml.save(true);
        yaml.reset();
        assertEquals(0, yaml.getFile().length());
    }

    @Test
    void save() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/save-test.yml");
        yaml.reset();
        yaml.put("p1");
        yaml.put("p2");
        yaml.put("p3");
        yaml.save(true);
        yaml.load();
        assertEquals("p1", yaml.getAllLoaded().get(0).getKey());
        assertEquals("p2", yaml.getAllLoaded().get(1).getKey());
        assertEquals("p3", yaml.getAllLoaded().get(2).getKey());
        // Test if save() orders the modules to the right parents
        yaml.getAllInEdit().clear();
        yaml.put("p1", "c1");
        yaml.put("p2", "c1");
        yaml.put("p3", "c1");
        yaml.save(true);
        yaml.load();
        yaml.printAll();
        assertEquals("c1", yaml.getAllLoaded().get(1).getKeyByIndex(1));
        assertEquals("c1", yaml.getAllLoaded().get(3).getKeyByIndex(1));
        assertEquals("c1", yaml.getAllLoaded().get(5).getKeyByIndex(1));
    }
}