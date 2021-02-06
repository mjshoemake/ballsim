package baseball

import baseball.domain.Simulation
import baseball.mongo.SimulationManager
import baseball.processing.HttpHistoricalDataManager
import baseball.processing.ScheduleLoader
import mjs.common.utils.LogUtils
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test

class TestPojo {
    def field1 = "value1"
    def field2 = "value2"
    def field3 = "value3"
    int num1 = 345
    boolean bool1 = false

    boolean equals(TestPojo target) {
        if (field1.equals(target.field1) &&
            field2.equals(target.field2) &&
            field3.equals(target.field3) &&
            num1 == target.num1 &&
            bool1 == target.bool1) {
            return true
        } else {
            return false
        }
    }
}

class TestPojoParent {
    def field1 = "value1"
    def field2 = "value2"
    def field3 = "value3"
    int num1 = 345
    boolean bool1 = false
    def list = [new TestPojo(), new TestPojo()]
    def map = ["key1":new TestPojo(), "key2":new TestPojo()]

    boolean equals(TestPojoParent target) {
        if (field1.equals(target.field1) &&
                field2.equals(target.field2) &&
                field3.equals(target.field3) &&
                num1 == target.num1 &&
                bool1 == target.bool1 &&
                list.equals(target.list) &&
                map.equals(target.map)) {
            return true
        } else {
            return false
        }
    }
}

class EqualsTests {

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void testEquals1() {
        def source = new TestPojo()
        def target = source
        assert source.equals(target): "Source should equal target (nothing changed)."

        target = new TestPojo()
        assert source.field1.equals(target.field1): "Source should equal target (nothing changed)."
        assert source.field2.equals(target.field2): "Source should equal target (nothing changed)."
        assert source.field3.equals(target.field3): "Source should equal target (nothing changed)."
        assert source.num1.equals(target.num1): "Source should equal target (nothing changed)."
        assert source.bool1.equals(target.bool1): "Source should equal target (nothing changed)."
        assert source.equals(target): "Source should equal target (nothing changed)."

        source = new TestPojoParent()
        target = new TestPojoParent()
        assert source.equals(target): "Source should equal target (nothing changed)."

        target.num1 = 346
        assert ! source.equals(target): "Source should NOT equal target (number value changed)."
        target.num1 = 345 // restore
        assert source.equals(target): "Source should equal target (value restored)."

        target.field1 = "changed"
        assert ! source.equals(target): "Source should NOT equal target (string value changed)."
        target.field1 = "value1" // restore
        assert source.equals(target): "Source should equal target (value restored)."

        target.list[0].field1 = "changed"
        assert ! source.equals(target): "Source should NOT equal target (list item string value changed)."
        target.list[0].field1 = "value1"
        assert source.equals(target): "Source should equal target (value restored)."

        target.map.key1.field1 = "changed"
        assert ! source.equals(target): "Source should NOT equal target (map item string value changed)."
        target.map.key1.field1 = "value1"
        assert source.equals(target): "Source should equal target (value restored)."

        source.map.key3 = new TestPojoParent()
        target.map.key3 = new TestPojoParent()
        assert source.equals(target): "Source should equal target (new object added to both sides)."

        target.map.key3.list[0].field1 = "changed"
        assert ! source.equals(target): "Source should NOT equal target (nested list item string value changed)."
        target.map.key3.list[0].field1 = "value1"
        assert source.equals(target): "Source should equal target (value restored)."



    }
}
