package seng302.group3.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SkillTest {

    private Skill testSkill;

    @Before
    public void setUp() throws Exception {
        testSkill = new Skill("name", "description");
    }

    @After
    public void tearDown() throws Exception {
        testSkill = null;
    }

    @Test
    public void testSkillWithZeroParametersIsNotNull() throws Exception {
        Skill zeroParamSkill = new Skill();
        Assert.assertNotNull("zeroParamSkill should exist", zeroParamSkill);
    }

    @Test
    public void testSkillWithOneParameterIsNotNull() throws Exception {
        Skill oneParamSkill = new Skill("oneParam");
        Assert.assertNotNull("oneParamSkill should exist", oneParamSkill);
    }

    @Test
    public void testSkillWithTwoParametersIsNotNull() throws Exception {
        Skill twoParamSkill = new Skill("twoParam", "Two Parameter");
        Assert.assertNotNull("twoParamSkill should exist", twoParamSkill);
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("toString should only return the 'name'", "name", testSkill.toString());
    }
}
