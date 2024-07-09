package hiperium.city.data.function;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class ModularityTest {

    private final ApplicationModules applicationModules = ApplicationModules.of(FunctionalApplication.class);

	@Test
	void verifyModularityTest() {
        this.applicationModules.stream().forEach(module -> {
            System.out.println("Module Name >>> "  + module.getName());
            System.out.println("Module Package >>> "  + module.getBasePackage());
        });
        this.applicationModules.verify();
	}
}
