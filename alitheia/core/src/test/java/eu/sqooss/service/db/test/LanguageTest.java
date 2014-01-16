package eu.sqooss.service.db.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Language;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBService.class, AlitheiaCore.class})
public class LanguageTest {
	
	@Test
	public void testLanguageEnums()
	{	
		assertArrayEquals(Language.C.extensions(),new String[] {".c", ".h"});
	}
}