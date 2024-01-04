package com.gsq.mvvm.util;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleClassTest extends TestCase {

    @BeforeClass
    public static void beforeClass() throws Exception{

    }

    @Before
    public void setUp() throws Exception{

    }

    @Test public void testAdd() {
        SimpleClass simpleClass = new SimpleClass();
        int sum = simpleClass.add(1,2);
        Assert.assertEquals(3,sum);
    }

    @After
    public void tearDown() throws Exception{

    }

    @AfterClass
    public static void afterClass() throws Exception{

    }
}