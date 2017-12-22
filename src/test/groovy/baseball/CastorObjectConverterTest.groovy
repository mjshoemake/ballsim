package baseball

import com.accenture.core.model.fielddef.FieldDefinition
import com.accenture.core.model.fielddef.FieldDefinitionList
import com.accenture.core.utils.CastorObjectConverter
import org.junit.After
import org.junit.Before
import org.junit.Test

class CastorObjectConverterTest {

    @Before
    void setUp() {
    }

    @After
    void tearDown() {
    }

    @Test
    void testConversion() {
        FieldDefinition def1 = new FieldDefinition(name: "MyField", format: "MyFormat")
        FieldDefinition def2 = new FieldDefinition(name: "MyField2", format: "MyFormat2")
        FieldDefinitionList definitionList = new FieldDefinitionList()
        definitionList.field << def1
        definitionList.field << def2
        def url = CastorObjectConverter.class.getResource("/mapping/FieldDefMapping.xml");
        String xml = CastorObjectConverter.convertObjectToXML(definitionList, FieldDefinitionList.class, url)
        println xml
    }

}
